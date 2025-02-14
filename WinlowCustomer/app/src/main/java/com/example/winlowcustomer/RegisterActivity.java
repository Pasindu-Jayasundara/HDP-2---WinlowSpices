package com.example.winlowcustomer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.CartOperations;
import com.example.winlowcustomer.modal.SQLiteHelper;
import com.example.winlowcustomer.modal.SendOtp;
import com.example.winlowcustomer.modal.SetUpLanguage;
import com.example.winlowcustomer.modal.Verify;
import com.example.winlowcustomer.modal.callback.IsNewUserCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    static int step = 1;
    public static UserDTO userDTO;
    public static String otp;
    int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SetUpLanguage.setAppLanguage(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // load first step
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView3, RegisterStepOneFragment.class, null)
                .setReorderingAllowed(true)
                .commit();


        // back button
        ImageButton backButton1 = findViewById(R.id.imageButton16);
        ImageButton backButton2 = findViewById(R.id.imageButton3);
        backButton1.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        // progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                animateProgressBar(1);
            }
        });

        // next button
        Button nextButton = findViewById(R.id.button11);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

    }

    private void animateProgressBar(int upTo) {

        ProgressBar progressBar = findViewById(R.id.progressBar);

        ImageButton stepPointOne = findViewById(R.id.imageButton17);
        ImageButton stepPointTwo = findViewById(R.id.imageButton18);
        ImageButton stepPointThree = findViewById(R.id.imageButton19);

        if (upTo == 0) {
            progress = 0;
        } else if (upTo == 1) {
            progress = getStepOneProgress(progressBar, stepPointOne);
        } else if (upTo == 2) {
            progress = getStepTwoProgress(progressBar, stepPointTwo);
        } else if (upTo == 3) {
            progress = getStepThreeProgress(progressBar, stepPointThree);
        } else if (upTo == 4) {
            progress = 100;
        }

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress);
        animation.setDuration(1000);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (upTo == 1) {
                    stepPointOne.setImageResource(R.drawable.tick3);
                } else if (upTo == 2) {
                    stepPointTwo.setImageResource(R.drawable.tick3);
                } else if (upTo == 3) {
                    stepPointThree.setImageResource(R.drawable.tick3);
                }
            }
        });
        animation.start();

    }

    private int getStepOneProgress(ProgressBar progressBar, ImageButton stepPointOne) {
        int stepOneXCenter = stepPointOne.getLeft() + stepPointOne.getWidth() / 2;
        int progressBarWidth = progressBar.getWidth();
        return (int) (((float) stepOneXCenter / progressBarWidth) * 100);
    }

    private int getStepTwoProgress(ProgressBar progressBar, ImageButton stepPointTwo) {
        int stepTwoXCenter = stepPointTwo.getLeft() + stepPointTwo.getWidth() / 2;
        int progressBarWidth = progressBar.getWidth();
        return (int) (((float) stepTwoXCenter / progressBarWidth) * 100);
    }

    private int getStepThreeProgress(ProgressBar progressBar, ImageButton stepPointThree) {
        int stepThreeXCenter = stepPointThree.getLeft() + stepPointThree.getWidth() / 2;
        int progressBarWidth = progressBar.getWidth();
        return (int) (((float) stepThreeXCenter / progressBarWidth) * 100);
    }

    private void next() {

        if (userDTO == null) {
            userDTO = new UserDTO();
        }

        Button nextButton = findViewById(R.id.button11);

        if (RegisterActivity.step == 1) {
            if (verifyStepOneComplete()) {

                Toast toast = Toast.makeText(RegisterActivity.this, R.string.please_wait, Toast.LENGTH_SHORT);
                toast.show();

                isNewUser(new IsNewUserCallback() {
                    @Override
                    public void onResult(boolean isNew) {

                        toast.cancel();

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView3, RegisterStepTwoFragment.class, null)
                                .setReorderingAllowed(true)
                                .commit();

                        RegisterActivity.step = 2;

                        if (otp == null) {
                            otp = SendOtp.send(userDTO.getMobile());
                        }

                        animateProgressBar(2);
                    }
                });

            } else {
                Toast.makeText(RegisterActivity.this, R.string.step_1_not_complete, Toast.LENGTH_SHORT).show();
            }

        } else if (RegisterActivity.step == 2) {

            if (verifyStepTwoComplete()) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView3, RegisterFragmentThreeFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();

                RegisterActivity.step = 3;
                nextButton.setText(R.string.step_3_done);
                animateProgressBar(3);
            } else {
                Toast.makeText(RegisterActivity.this, R.string.step_2_not_complete, Toast.LENGTH_SHORT).show();
            }

        } else if (RegisterActivity.step == 3) {

            if (verifyStepThreeComplete()) {

                animateProgressBar(4);
                // store user in firebase
                addUserToFirebase();

            } else {
                Toast.makeText(RegisterActivity.this, R.string.step_3_not_complete, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addUserToFirebase() {

        if (userDTO.getName().isBlank() || userDTO.getMobile().isBlank() || userDTO.getEmail().isBlank() || userDTO.getAddress().isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.step_1_not_complete, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String docId = firestore.collection("user").document().getId(); // Generate unique ID
        userDTO.setId(docId);

        firestore.collection("user")
                .document(docId)
                .set(userDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        Toast.makeText(RegisterActivity.this, R.string.user_registration_success, Toast.LENGTH_SHORT).show();

                        // store user in sqlite
                        SQLiteHelper sqLiteHelper = new SQLiteHelper(RegisterActivity.this, "winlow.db", null, 1);
                        sqLiteHelper.insertSingleUser(sqLiteHelper, docId, userDTO.getName(), userDTO.getMobile(), userDTO.getEmail());

                        // store user in shared preferences
                        CartOperations.isLoggedIn(getApplicationContext());

                        Intent reIntent = getIntent();
                        if (reIntent.hasExtra("fromCart")) {

                            reIntent.removeExtra("fromCart");

                            Intent intent = new Intent(RegisterActivity.this, ProductViewActivity.class);
                            startActivity(intent);

                            finish();

                        } else {
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);

                            finish();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this, R.string.user_registration_failed, Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void goBack() {

        Button nextButton = findViewById(R.id.button11);

        if (RegisterActivity.step == 1) {
            getOnBackPressedDispatcher().onBackPressed();

            animateProgressBar(0);

            ImageButton stepPointTwo = findViewById(R.id.imageButton17);
            stepPointTwo.setImageResource(R.drawable.add);

        } else if (RegisterActivity.step == 2) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView3, RegisterStepOneFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

            RegisterActivity.step = 1;
            nextButton.setText(R.string.registration_step_next);

            animateProgressBar(1);

            ImageButton stepPointThree = findViewById(R.id.imageButton18);
            stepPointThree.setImageResource(R.drawable.add);

        } else if (RegisterActivity.step == 3) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView3, RegisterStepTwoFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

            RegisterActivity.step = 2;
            nextButton.setText(R.string.registration_step_next);

            animateProgressBar(2);
            ImageButton stepPointThree = findViewById(R.id.imageButton19);
            stepPointThree.setImageResource(R.drawable.add);
        }
    }

    private boolean verifyStepOneComplete() {

        TextInputEditText nameView = findViewById(R.id.reg_name);
        TextInputEditText mobileView = findViewById(R.id.reg_mobile);
        TextInputEditText emailView = findViewById(R.id.reg_email);

        String name = nameView.getText().toString();
        String mobile = mobileView.getText().toString();
        String email = emailView.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.name_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mobile.isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.mobile_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.email_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Verify.verifyMobileNumber(mobile)) {
            Toast.makeText(RegisterActivity.this, R.string.invalid_mobile_number, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Verify.verifyEmail(email)) {
            Toast.makeText(RegisterActivity.this, R.string.invalid_email_address, Toast.LENGTH_SHORT).show();
            return false;
        }

        userDTO.setName(name);
        userDTO.setMobile(mobile);
        userDTO.setEmail(email);

        return true;

    }

    private void isNewUser(IsNewUserCallback isNewUserCallback) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .where(Filter.equalTo("mobile", userDTO.getMobile()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        boolean isNew = true;
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {

                            Toast.makeText(RegisterActivity.this, R.string.user_already_exist, Toast.LENGTH_SHORT).show();
                            isNew = false;

                        }

                        isNewUserCallback.onResult(isNew);
                    }
                });

    }

    private boolean verifyStepTwoComplete() {

        TextInputEditText otpChar1View = findViewById(R.id.otpChar1);
        TextInputEditText otpChar2View = findViewById(R.id.otpChar2);
        TextInputEditText otpChar3View = findViewById(R.id.otpChar3);
        TextInputEditText otpChar4View = findViewById(R.id.otpChar4);
        TextInputEditText otpChar5View = findViewById(R.id.otpChar5);
        TextInputEditText otpChar6View = findViewById(R.id.otpChar6);

        String otp2 = "";
        otp2 = otp2 + otpChar1View.getText().toString();
        otp2 = otp2 + otpChar2View.getText().toString();
        otp2 = otp2 + otpChar3View.getText().toString();
        otp2 = otp2 + otpChar4View.getText().toString();
        otp2 = otp2 + otpChar5View.getText().toString();
        otp2 = otp2 + otpChar6View.getText().toString();

        if (otp2.length() != 6) {
            Toast.makeText(RegisterActivity.this, R.string.otp_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (otp.equals(otp2)) {
            return true;
        }

        return false;
    }

    private boolean verifyStepThreeComplete() {

        TextInputEditText addressView = findViewById(R.id.reg_address);
        String address = addressView.getText().toString();

        if (address.isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.address_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        List<String> userAddressList = new ArrayList<>();
        userAddressList.add(address);

        userDTO.setAddress(userAddressList);

        return true;

    }
}