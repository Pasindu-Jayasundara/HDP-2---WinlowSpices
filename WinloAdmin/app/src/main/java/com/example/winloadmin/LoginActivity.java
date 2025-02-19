package com.example.winloadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winloadmin.dto.UserDTO;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

//    private GoogleSignInClient googleSignInClient;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    FirebaseFirestore db;
    FirebaseUser user;
    public static HashMap<String,Object> adminHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
            getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        ImageView loginButton = findViewById(R.id.imageView4);
        Button btn = findViewById(R.id.button6);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText(R.string.wait);
                googleSignIn();
                btn.setText(R.string.login);
            }
        });

        if(user!= null){
            goToMainActivity();
        }else{

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                user = currentUser;
                goToMainActivity();
            }

//            if(firebaseAuth.getCurrentUser()!=null){
//                firebaseAuth.signOut();
//                googleSignInClient.signOut();
//            }
        }

    }

    private void googleSignIn() {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1250);

//        ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
//                        try {
//                            GoogleSignInAccount account = task.getResult(ApiException.class);
//                            if (account != null && account.getIdToken() != null) {
//                                checkInDB(account.getIdToken());
//                            }
//                        } catch (ApiException e) {
//                            Log.e("GoogleSignIn", "Sign-in failed", e);
//                        }
//                    }
//                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1250){

            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                checkInDB(googleSignInAccount.getIdToken());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void checkInDB(String idToken) {
//        Log.i("abc", "1 : "+new Gson().toJson(idToken));

        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//                        Log.i("abc",new Gson().toJson(authResult));

                        FirebaseUser firebaseUser = authResult.getUser();
//                Log.i("abc", "ID Token: " + new Gson().toJson(firebaseUser));

                        adminHashMap.put("id",firebaseUser.getUid());
                        adminHashMap.put("name",firebaseUser.getDisplayName());
                        adminHashMap.put("email",firebaseUser.getEmail());
                        adminHashMap.put("profileImage",firebaseUser.getPhotoUrl().toString());



                        db.collection("admin").document(firebaseUser.getEmail())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        boolean exists = documentSnapshot.exists();
                                        if(exists){
                                            user = firebaseUser;

                                            updateDb(documentSnapshot);
//                                            goToMainActivity();

                                        }else{

                                            firebaseAuth.signOut();
                                            googleSignInClient.signOut();

                                            Toast.makeText(LoginActivity.this,R.string.not_registered,Toast.LENGTH_SHORT).show();;
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        firebaseAuth.signOut();
                                        googleSignInClient.signOut();

                                        Toast.makeText(LoginActivity.this,R.string.not_registered,Toast.LENGTH_SHORT).show();;

                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        firebaseAuth.signOut();
                        googleSignInClient.signOut();

                        Toast.makeText(LoginActivity.this,R.string.something_went_wrong,Toast.LENGTH_SHORT).show();;

                    }
                });

    }

    private void updateDb(DocumentSnapshot documentSnapshot) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admin").document(documentSnapshot.getId())
                .update(adminHashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        SharedPreferences sharedPreferences = getSharedPreferences("com.example.winloadmin.data",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("admin",new Gson().toJson(adminHashMap));
                        editor.apply();

                        goToMainActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        goToMainActivity();
                    }
                });

    }

    private void goToMainActivity() {

        UserDTO userDTO = new UserDTO();

        if(adminHashMap.isEmpty()){
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.winloadmin.data",MODE_PRIVATE);
            String admin = sharedPreferences.getString("admin", null);

            if(admin != null){
                adminHashMap = new Gson().fromJson(admin, HashMap.class);
            }else{
                return;
            }

        }

        if(user == null){

            SharedPreferences sharedPreferences = getSharedPreferences("com.example.winloadmin.data",MODE_PRIVATE);
            String admin = sharedPreferences.getString("admin", null);

            if(admin != null){
                adminHashMap = new Gson().fromJson(admin, HashMap.class);

                userDTO.setId(adminHashMap.get("id").toString());
                userDTO.setName(adminHashMap.get("name").toString());
                userDTO.setEmail(adminHashMap.get("email").toString());
                userDTO.setPhoto_url(adminHashMap.get("profileImage").toString());

            }

        }else{

            userDTO.setId(user.getUid());
            userDTO.setName(user.getDisplayName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoto_url(user.getPhotoUrl().toString());

        }


        if(user!=null && !adminHashMap.isEmpty()){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user",new Gson().toJson(userDTO));
            startActivity(intent);
            finish();

        }

    }

}