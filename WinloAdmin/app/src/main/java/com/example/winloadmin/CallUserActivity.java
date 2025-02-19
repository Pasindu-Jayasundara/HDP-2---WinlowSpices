package com.example.winloadmin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class CallUserActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private String mobile;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call_user);

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

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        String profileImage = intent.getStringExtra("profileImage");

        ImageView profileImageView = findViewById(R.id.imageView9);
        TextView nameView = findViewById(R.id.textView50);
        TextView callStatusView = findViewById(R.id.textView51);
        Button endCallBtn = findViewById(R.id.button18);

        Glide.with(getApplicationContext())
                .load(profileImage)
                .circleCrop()
                .error(R.drawable.product_image)
                .placeholder(R.drawable.product_image)
                .into(profileImageView);

        nameView.setText(name);
        callStatusView.setText(R.string.waiting);

        endCallBtn.setOnClickListener(v -> endCall());

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        callStatusView.setText(R.string.rigging);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        callStatusView.setText(R.string.talking);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        callStatusView.setText(R.string.call_end);
                        finish(); // Return to previous screen
                        break;
                }
            }
        };

        requestPermissionsIfNeeded();
    }

    private void requestPermissionsIfNeeded() {
        String[] permissions = {
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CALL_PHONE
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            setupCallListener();
            makeCall();
        }
    }

    private void setupCallListener() {
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void makeCall() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mobile));
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.call_permission_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    private void endCall() {
        Toast.makeText(this, R.string.cannot_call, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allGranted) {
                setupCallListener();
                makeCall();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
