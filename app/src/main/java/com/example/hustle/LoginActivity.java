package com.example.hustle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    TextView signUp,forgot;
    Button login;
    Intent intent;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String id,key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Getting logged in");
        progressDialog.setMessage("Hold on we are getting you logged in");


        email=findViewById(R.id.input_email_login);
        password=findViewById(R.id.input_confirm_login);

        signUp=findViewById(R.id.sign_up);
        forgot=findViewById(R.id.forgot_pass);

        login=findViewById(R.id.sign_in2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    email.findFocus();
                    Toast.makeText(LoginActivity.this,"Email cannot be Empty",Toast.LENGTH_SHORT).show();
                }
                if(password.getText().toString().isEmpty()){
                    password.findFocus();
                    Toast.makeText(LoginActivity.this,"Password cannot be Empty",Toast.LENGTH_SHORT).show();
                }

                else{
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()&&firebaseAuth.getCurrentUser().isEmailVerified())
                                    {
                                        id = task.getResult().getUser().getUid();
                                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                        LoginActivity.this.finish();
                                        startActivity(intent);
                                    }
                                    else{
                                        //task.getException().getMessage()
                                        Toast.makeText(LoginActivity.this,"Email not registered",Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }

            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetPassword=new EditText(v.getContext());
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Reset Password");
                alertDialog.setMessage("Enter your Email to receive password reset link (Beware of spaces)");
                alertDialog.setView(resetPassword);
                alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailResetPassword=resetPassword.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(emailResetPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(v.getContext(),"Reset link has been sent to the mail",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.create().show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        LoginActivity.this.finish();
        Process.killProcess(Process.myPid());
        //startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

}
