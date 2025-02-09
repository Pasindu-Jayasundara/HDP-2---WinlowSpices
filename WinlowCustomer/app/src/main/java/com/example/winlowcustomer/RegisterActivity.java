package com.example.winlowcustomer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.winlowcustomer.modal.Verify;
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
    UserDTO userDTO;
    String otp;

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

        // next button
        Button nextButton = findViewById(R.id.button11);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

    }

    private void next() {

        if (userDTO == null) {
            userDTO = new UserDTO();
        }

        Button nextButton = findViewById(R.id.button11);

        if (RegisterActivity.step == 1) {

            if (verifyStepOneComplete()) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView3, RegisterStepTwoFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();

                RegisterActivity.step = 2;

                if (otp == null) {
                    otp = SendOtp.send(userDTO.getMobile());
                }

                TextView resendOtp = findViewById(R.id.textView70);
//                resendOtp.setEnabled(false);
//                resendOtp.setClickable(false);
//                resendOtp.setText(R.string.step_2_resendIn);

                startOtpReSendingProcess();

                resendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        resendOtp.setText(R.string.step_2_resending);

                        otp = SendOtp.send(userDTO.getMobile());
                        resendOtp.setText(R.string.step_2_resend);

                        startOtpReSendingProcess();

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

            } else {
                Toast.makeText(RegisterActivity.this, R.string.step_2_not_complete, Toast.LENGTH_SHORT).show();
            }

        } else if (RegisterActivity.step == 3) {

            if (verifyStepThreeComplete()) {

                // store user in firebase
                addUserToFirebase();

            } else {
                Toast.makeText(RegisterActivity.this, R.string.step_3_not_complete, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startOtpReSendingProcess() {

        TextView resendOtp = findViewById(R.id.textView70);
        resendOtp.setText(R.string.step_2_resendIn);
        resendOtp.setEnabled(false);
        resendOtp.setClickable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 60; i > 0; i--) {
                    try{

                        int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String txt = R.string.step_2_resendIn + " " + String.valueOf(finalI);
                                resendOtp.setText(txt);

                            }
                        });

                        Thread.sleep(1000);
                    } catch (Exception e) {
//                                throw new RuntimeException(e);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        resendOtp.setEnabled(true);
                        resendOtp.setClickable(true);
                        resendOtp.setText(R.string.step_2_resend);

                    }
                });

            }
        }).start();

    }

    private void addUserToFirebase() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("user")
                .add(userDTO)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(RegisterActivity.this, R.string.user_registration_success, Toast.LENGTH_SHORT).show();

                        // store user in sqlite
                        SQLiteHelper sqLiteHelper = new SQLiteHelper(RegisterActivity.this, "winlow.db", null, 1);
                        sqLiteHelper.insertSingleUser(sqLiteHelper,documentReference.getId(), userDTO.getName(), userDTO.getMobile(), userDTO.getEmail());

                        // store user in shared preferences
                        CartOperations.isLoggedIn(getApplicationContext());

                        Intent reIntent = getIntent();
                        if (reIntent.hasExtra("fromCart")) {

                            Intent intent = new Intent(RegisterActivity.this, CartActivity.class);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
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
        } else if (RegisterActivity.step == 2) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView3, RegisterStepOneFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

            RegisterActivity.step = 1;
            nextButton.setText(R.string.registration_step_next);

        } else if (RegisterActivity.step == 3) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView3, RegisterStepTwoFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

            RegisterActivity.step = 2;
            nextButton.setText(R.string.registration_step_next);
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

        return isNewUser();

    }

    private boolean isNewUser() {

        final boolean[] isNew = {true};

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .where(Filter.equalTo("mobile", userDTO.getMobile()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {

                            Toast.makeText(RegisterActivity.this, R.string.user_already_exist, Toast.LENGTH_SHORT).show();
                            isNew[0] = false;
                        }
                    }
                });

        return isNew[0];

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