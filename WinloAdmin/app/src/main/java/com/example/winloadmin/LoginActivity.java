package com.example.winloadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    public static FirebaseUser user;

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

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        Button loginButton = findViewById(R.id.button4);
        loginButton.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.i("abc",new Gson().toJson(data));

        if(requestCode == 1250){
//            Log.i("abc",new Gson().toJson(data));

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

        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();

    }

}