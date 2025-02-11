package com.example.winlowcustomer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.ProductDTO;
import com.example.winlowcustomer.dto.UserDTO;
import com.example.winlowcustomer.modal.CartOperations;
import com.example.winlowcustomer.modal.SQLiteHelper;
import com.example.winlowcustomer.modal.SendOtp;
import com.example.winlowcustomer.modal.Verify;
import com.example.winlowcustomer.modal.callback.GetDataCallback;
import com.example.winlowcustomer.modal.callback.IsNewUserCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // register btn
        Button createAccountBtn = findViewById(R.id.button14);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent receivedIntent = getIntent();
                String fromCart = receivedIntent.getStringExtra("fromCart");
                String productDTO = receivedIntent.getStringExtra("productDTO");

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                intent.putExtra("fromCart", fromCart);
                intent.putExtra("productDTO", productDTO);
                startActivity(intent);

            }
        });

        // login btn
        Button loginBtn = findViewById(R.id.button13);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendOTP();

            }
        });

    }

    private void sendOTP() {

        TextInputEditText mobileNumber = findViewById(R.id.loginMobile);
        String stringMobileNumber = mobileNumber.getText().toString();

        if (Verify.verifyMobileNumber(stringMobileNumber, getApplicationContext())) {

//            String otp = SendOtp.send(stringMobileNumber);
            String otp = "123";

            // show dialog popup to enter otp
            LayoutInflater layoutInflater = getLayoutInflater();
            View inflate = layoutInflater.inflate(R.layout.login_get_user_entered_otp_dialog_customize_view, null, false);
            TextView otpInvalidText = inflate.findViewById(R.id.textView73);
            otpInvalidText.setVisibility(View.GONE);

            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                    .setView(inflate)
                    .setCancelable(false)
                    .show();

            Button dialogButton = inflate.findViewById(R.id.button15);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextInputEditText dialogOtp = inflate.findViewById(R.id.dialogOtp);

                    if (!dialogOtp.getText().toString().equals(otp)) {
                        otpInvalidText.setVisibility(View.VISIBLE);
                    } else {
                        alertDialog.dismiss();

                        otpMatched(stringMobileNumber);
                    }

                }
            });


        }

    }

    private void otpMatched(String stringMobileNumber) {
        Log.i("otpMatched", "aaaaaaaaaaaa");

        isNewUser(stringMobileNumber, new IsNewUserCallback() {
            @Override
            public void onResult(boolean isNew) {

                if (!isNew) {
                    Log.i("otpMatched", "bbbbbbbbbbbbbbbbbbb");

                    Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

                    Intent receivedIntent = getIntent();
                    String fromCart = receivedIntent.getStringExtra("fromCart");
                    if(fromCart != null){
                        String productDTO = receivedIntent.getStringExtra("productDTO");

                        Gson gson = new Gson();
                        Boolean b = gson.fromJson(fromCart, Boolean.class);
                        if (b) {

                            receivedIntent.removeExtra("fromCart");

                            ProductDTO productDTO1 = gson.fromJson(productDTO, ProductDTO.class);
                            if (productDTO1 != null) {

                                receivedIntent.removeExtra("productDTO");

                                CartOperations cartOperations = new CartOperations();
                                cartOperations.addToCart(productDTO1, LoginActivity.this);

                            }

                            if(receivedIntent.hasExtra("productDTO")){
                                receivedIntent.removeExtra("productDTO");
                            }

                            Intent intent = new Intent(LoginActivity.this, ProductViewActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }

                }else{
                    Toast.makeText(LoginActivity.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void isNewUser(String mobile, IsNewUserCallback isNewUserCallback) {
        Log.i("otpMatched", "xxxxxxxxx");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .where(Filter.equalTo("mobile", mobile))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i("otpMatched", "zzzzzzzzzzz");
                        boolean isNew = true;
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            isNew = false;
                            Log.i("otpMatched", "mmmmmmmmmmmmmm");

                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            for (DocumentSnapshot documentSnapshot : documents){
                                String id = documentSnapshot.getId();
                                String name = documentSnapshot.getString("name");
                                String mobile = documentSnapshot.getString("mobile");
                                String email = documentSnapshot.getString("email");

                                // store user in sqlite
                                SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext(), "winlow.db", null, 1);
                                sqLiteHelper.insertSingleUser(sqLiteHelper, id, name, mobile, email);

                                // store user in shared preferences
                                CartOperations.isLoggedIn(getApplicationContext());

                                // store order list
                                SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
                                String userJson = sharedPreferences.getString("user",null);
                                if(userJson!=null){

                                    UserDTO userDTO = new Gson().fromJson(userJson, UserDTO.class);
                                    List<String> orderHistoryList = userDTO.getOrderHistory();

                                    // order history
                                    List<String> orderHistory = (List<String>) documentSnapshot.get("order_history");
                                    orderHistoryList.addAll(orderHistory);

                                    userDTO.setOrderHistory(orderHistoryList);

                                    sharedPreferences.edit().putString("user",new Gson().toJson(userDTO)).apply();

                                }


                            }

//                            isNew[1] = false;

                        }

                        isNewUserCallback.onResult(isNew);
                    }
                });

    }
}