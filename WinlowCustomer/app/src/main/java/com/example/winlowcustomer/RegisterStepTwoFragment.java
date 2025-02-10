package com.example.winlowcustomer;

import static com.example.winlowcustomer.RegisterActivity.userDTO;
import static com.example.winlowcustomer.RegisterActivity.otp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.winlowcustomer.modal.SendOtp;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RegisterStepTwoFragment extends Fragment {

    private boolean isTextChanging = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_step_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // resend
        TextView resendOtp = view.findViewById(R.id.textView70);
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startOtpReSendingProcess(resendOtp);
                otp = SendOtp.send(userDTO.getMobile());
            }
        });

        // paste
        TextInputEditText char1 = view.findViewById(R.id.otpChar1);
        TextInputEditText char2 = view.findViewById(R.id.otpChar2);
        TextInputEditText char3 = view.findViewById(R.id.otpChar3);
        TextInputEditText char4 = view.findViewById(R.id.otpChar4);
        TextInputEditText char5 = view.findViewById(R.id.otpChar5);
        TextInputEditText char6 = view.findViewById(R.id.otpChar6);

        char1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        char2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        char2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        char4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        char5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        char6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isTextChanging) {
                    return; // Skip setting text if already in the process of changing text
                }
                isTextChanging = true;
                assignCharactersToViews(view);
                isTextChanging = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void assignCharactersToViews(View view) {
        if (otp != null && otp.length() == 6) {

            TextView[] textViews = {
                    view.findViewById(R.id.otpChar1),
                    view.findViewById(R.id.otpChar2),
                    view.findViewById(R.id.otpChar3),
                    view.findViewById(R.id.otpChar4),
                    view.findViewById(R.id.otpChar5),
                    view.findViewById(R.id.otpChar6)
            };

            for (int i = 0; i < otp.length(); i++) {
                textViews[i].setText(String.valueOf(otp.charAt(i)));
            }
        } else {
            Toast.makeText(view.getContext(), R.string.invalid_otp, Toast.LENGTH_SHORT).show();
        }
    }


    private void startOtpReSendingProcess(TextView resendOtp) {

        resendOtp.setText(R.string.step_2_resendIn);
        resendOtp.setEnabled(false);
        resendOtp.setClickable(false);

        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 60; i > 0; i--) {
                    int finalI = i;
                    handler.post(() -> resendOtp.setText(getString(R.string.step_2_resendIn) + " " + finalI));

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(() -> {
                    resendOtp.setEnabled(true);
                    resendOtp.setClickable(true);
                    resendOtp.setText(R.string.step_2_resend);
                });
            }
        }).start();
    }

}