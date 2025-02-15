package com.example.winloadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

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
                googleSignIn();
            }
        });

        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            goToMainActivity();
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

//        Log.i("abc",new Gson().toJson(data));

        if(requestCode == 1250){
//            Log.i("abc",new Gson().toJson(data));

//            if(resultCode != RESULT_OK){
//                Toast.makeText(this,R.string.login_failed,Toast.LENGTH_SHORT).show();
//                return;
//            }

            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
//                Log.i("abc",new Gson().toJson(googleSignInAccountTask));

                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
//                Log.i("abc", "ID Token: " + googleSignInAccount.getIdToken());

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
                Log.i("abc", "ID Token: " + new Gson().toJson(firebaseUser));

//                        HashMap<String,Object> hashMap = new HashMap<>();
//                        hashMap.put("id",firebaseUser.getUid());
//                        hashMap.put("name",firebaseUser.getDisplayName());
//                        hashMap.put("email",firebaseUser.getEmail());
//                        hashMap.put("profileImage",firebaseUser.getPhotoUrl().toString());

                        db.collection("admin").document(firebaseUser.getEmail())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        Log.i("abc",new Gson().toJson(documentSnapshot));

                                        user = firebaseUser;
                                        goToMainActivity();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(LoginActivity.this,R.string.not_registered,Toast.LENGTH_SHORT).show();;

                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(LoginActivity.this,R.string.something_went_wrong,Toast.LENGTH_SHORT).show();;

                    }
                });

    }

    private void goToMainActivity() {

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUid());
        userDTO.setName(user.getDisplayName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoto_url(user.getPhotoUrl().toString());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user",new Gson().toJson(userDTO));
        startActivity(intent);
        finish();

    }

}