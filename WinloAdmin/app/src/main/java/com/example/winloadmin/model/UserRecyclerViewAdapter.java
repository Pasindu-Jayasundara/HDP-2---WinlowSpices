package com.example.winloadmin.model;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.winloadmin.MainActivity.customerDTOList;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.winloadmin.CallUserActivity;
import com.example.winloadmin.R;
import com.example.winloadmin.dto.CustomerDTO;
import com.google.android.material.textfield.TextInputEditText;

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

                Intent intent = new Intent(v.getContext(), CallUserActivity.class);
                intent.putExtra("name",customerDTO.getName());
                intent.putExtra("mobile",customerDTO.getMobile());
                intent.putExtra("profileImage",customerDTO.getProfile_image());
                v.getContext().startActivity(intent);

            }
        });

        // message
        holder.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View inflated = layoutInflater.inflate(R.layout.message_card, v.findViewById(R.id.userRecyclerView), false);

                TextView name = inflated.findViewById(R.id.textView49);
                name.setText(customerDTO.getName());

                TextInputEditText message = inflated.findViewById(R.id.messageTxt);

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

                            message.setText("");
                            Toast.makeText(v.getContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });

        // email
        holder.emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View inflated = layoutInflater.inflate(R.layout.email_card, v.findViewById(R.id.userRecyclerView), false);

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
                            subject.setError(v.getContext().getString(R.string.subject_is_required));
                        }else if(bodyText.isEmpty()){
                            body.setError(v.getContext().getString(R.string.body_is_required));
                        }else{

                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"+customerDTO.getEmail()));
                            intent.putExtra(Intent.EXTRA_TEXT,bodyText);
                            intent.putExtra(Intent.EXTRA_SUBJECT, subjectText);

                            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                                startActivity(v.getContext(),intent,null);
                            }else{
                                Toast.makeText(v.getContext(), R.string.action_cannot_be_completed, Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });


                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext()).setView(inflated);
                alert.show();

            }
        });

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
