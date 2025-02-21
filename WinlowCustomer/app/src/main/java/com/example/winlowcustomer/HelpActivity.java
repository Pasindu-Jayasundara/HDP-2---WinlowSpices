package com.example.winlowcustomer;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.winlowcustomer.dto.UserDTO;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class HelpActivity extends AppCompatActivity {

    public static final String videoPath = "https://winloflavors.com/cdn/shop/videos/c/vp/74e7e121e9af4181a24b66abc97b3e94/74e7e121e9af4181a24b66abc97b3e94.HD-1080p-2.5Mbps-24438935.mp4?v=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView12, BottomNavigationFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        // back
        ImageButton back = findViewById(R.id.imageButton60);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // send Msg
        Button btn = findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.winlowcustomer.data", MODE_PRIVATE);
                    String user = sharedPreferences.getString("user", null);
                    if(user!=null){
                        UserDTO userDTO = new Gson().fromJson(user, UserDTO.class);
                        String mobile = userDTO.getMobile();
                        sendSms(mobile);
                    }

                } else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 100);
                }

            }
        });

        // load video
        VideoView videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoPath));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.sms_permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sms_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms(String mobile) {

        EditText message = findViewById(R.id.editTextTextMultiLine);

        if(message.getText().toString().isBlank()){
            message.setError(getApplicationContext().getString(R.string.fill_message));
        }else{

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    mobile,
                    null,
                    message.getText().toString(),
                    null,
                    null
            );

            message.setText("");

            Toast.makeText(getApplicationContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();

        }

    }

}