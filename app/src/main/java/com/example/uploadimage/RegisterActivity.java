package com.example.uploadimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.uploadimage.R;

import com.example.uploadimage.MainActivity;
import com.example.uploadimage.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password;
    Button register_button;
    TextView loginTextView;

    private RadioGroup radioGroup;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register_button = (Button) findViewById(R.id.register_button);
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait..");
                pd.show();

                int selectedID = radioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectedID);

                if (radioButton.getText() == null){
                    radioButton.setError("Please select your nationality");
                    radioButton.requestFocus();
                }

                String usr_nm = username.getText().toString();
                String emailID = email.getText().toString();
                String pwd = password.getText().toString();

                if (emailID.isEmpty()) {
                    email.setError("Please enter your email");
                    email.requestFocus();

                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();

                } else if (usr_nm.isEmpty()) {
                    password.setError("Please enter your username");
                    password.requestFocus();

                } else if (emailID.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();

                } else if (!(emailID.isEmpty() && pwd.isEmpty() && usr_nm.isEmpty())) {
                    auth.createUserWithEmailAndPassword(emailID, pwd)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Register Error, Please Try Again", Toast.LENGTH_SHORT).show();

                                    } else {
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        String userID = firebaseUser.getUid();
                                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                                        HashMap <String, Object> hashMap =  new HashMap<>();
                                        hashMap.put("id", userID);
                                        hashMap.put("username", username.getText().toString());
                                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/myapp-1d383.appspot.com/o/human_icon.PNG?alt=media&token=b98297a0-9b6f-437d-b430-be942fbae866");
                                        hashMap.put("nation", radioButton.getText().toString());
//
//                                        User user =new User();
//                                        user.setNation(radioButton.getText().toString());
                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    Intent i = new Intent (RegisterActivity.this, MainActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(i);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

}