package com.example.winloadmin.model;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getString;
import static androidx.core.content.ContextCompat.startActivity;
import static com.example.winloadmin.MainActivity.customerDTOList;
//import static com.example.winloadmin.MainActivity.packageManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.CallUserActivity;
import com.example.winloadmin.MainActivity;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.CustomerDTO;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserRecyclerViewHolder> {

    @NonNull
    @Override
    public UserRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_card, parent, false);

        return new UserRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewHolder holder, int position) {

        CustomerDTO customerDTO = customerDTOList.get(position);

        holder.name.setText(customerDTO.getName());

        // call
        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivityFromContext(v.getContext());
                if (activity == null) {
                    Toast.makeText(v.getContext(), R.string.unable_to_get_activity_context, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    makeCall(customerDTO.getMobile(), v.getContext());
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 200);
                }
            }
        });



        // message
        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Context context = v1.getContext();

                // Get the correct Activity reference
                Activity activity = null;
                if (context instanceof Activity) {
                    activity = (Activity) context;
                } else if (context instanceof ContextThemeWrapper) {
                    while (context instanceof ContextThemeWrapper) {
                        context = ((ContextThemeWrapper) context).getBaseContext();
                    }
                    if (context instanceof Activity) {
                        activity = (Activity) context;
                    }
                }

                if (activity == null) {
                    Toast.makeText(v1.getContext(), R.string.unable_to_get_activity_context, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendSms(v1, customerDTO);
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 100);
                }
            }
        });


        // email
        holder.emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                LayoutInflater layoutInflater = LayoutInflater.from(v1.getContext());
                View inflated = layoutInflater.inflate(R.layout.email_card, v1.findViewById(R.id.userRecyclerView), false);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v1.getContext()).setView(inflated);
                AlertDialog alertDialog = alertBuilder.create(); // Create the AlertDialog

                TextView email = inflated.findViewById(R.id.textView45);
                email.setText(customerDTO.getEmail());

                TextView subject = inflated.findViewById(R.id.emailSubject);
                TextView body = inflated.findViewById(R.id.emailBody);
                Button sendBtn = inflated.findViewById(R.id.button16);

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String subjectText = subject.getText().toString();
                        String bodyText = body.getText().toString();

                        if(subjectText.isEmpty()){
                            subject.setError(v1.getContext().getString(R.string.subject_is_required));
                        } else if(bodyText.isEmpty()){
                            body.setError(v1.getContext().getString(R.string.body_is_required));
                        } else {

                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + customerDTO.getEmail()));
                            intent.putExtra(Intent.EXTRA_TEXT, bodyText);
                            intent.putExtra(Intent.EXTRA_SUBJECT, subjectText);

                            v1.getContext().startActivity(Intent.createChooser(intent, getString(v1.getContext(),R.string.choose_email_app)));

                            alertDialog.dismiss();
                        }
                    }
                });

                alertDialog.show();


            }
        });

    }


    private void makeCall(String mobile, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mobile));
            context.startActivity(intent);
        } else {
            Toast.makeText(context, R.string.call_permission_not_granted, Toast.LENGTH_SHORT).show();
        }
    }

    private Activity getActivityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextThemeWrapper) {
            while (context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
            }
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }


    private void sendSms(View v1,CustomerDTO customerDTO) {

        LayoutInflater layoutInflater = LayoutInflater.from(v1.getContext());
        View inflated = layoutInflater.inflate(R.layout.message_card, v1.findViewById(R.id.userRecyclerView), false);

        TextView name = inflated.findViewById(R.id.textView49);
        name.setText(customerDTO.getName());

        TextInputEditText message = inflated.findViewById(R.id.messageTxt);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(v1.getContext()).setView(inflated);
        AlertDialog alertDialog = alertBuilder.create();

        Button btn = inflated.findViewById(R.id.button17);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().toString().isBlank()){
                    message.setError(v.getContext().getString(R.string.fill_message));
                }else{

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(
                            customerDTO.getMobile(),
                            null,
                            message.getText().toString(),
                            null,
                            null
                    );

                    alertDialog.dismiss();

                    Toast.makeText(v.getContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();

                }

            }
        });

        alertDialog.show();

    }

    @Override
    public int getItemCount() {
        return customerDTOList.size();
    }

    public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Button callBtn;
        Button messageBtn;
        Button emailBtn;

        public UserRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView19);
            callBtn = itemView.findViewById(R.id.button13);
            messageBtn = itemView.findViewById(R.id.button14);
            emailBtn = itemView.findViewById(R.id.button15);
        }
    }
}
