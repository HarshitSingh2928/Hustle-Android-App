package com.example.hustle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    EditText name,email,password,confirmPassword;
    Button registerButton;
    TextView signIn;
    Intent intent;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Hold on we are creating you account");


        name=findViewById(R.id.input_name);
        email=findViewById(R.id.input_email);
        password=findViewById(R.id.input_password);
        confirmPassword=findViewById(R.id.input_confirm);
        registerButton=findViewById(R.id.register_button);
        signIn=findViewById(R.id.sign_in);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password.getText().toString().isEmpty() || email.getText().toString().isEmpty()
                        || name.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Some fields are missing",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.show();
                    if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                        firebaseAuth.createUserWithEmailAndPassword
                                (email.getText().toString(), confirmPassword.getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getApplicationContext(),"Verification email has been sent",Toast.LENGTH_LONG).show();
                                                    //Toast.makeText(RegisterActivity.this, "User Created Successfully", Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(RegisterActivity.this,EmailVerificationActivity.class);
                                                    intent.putExtra("userName",name.getText().toString());
                                                    intent.putExtra("userEmail",email.getText().toString());
                                                    intent.putExtra("userPassword",confirmPassword.getText().toString());
                                                    intent.putExtra("userId",task.getResult().getUser().getUid());
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });


                                            Users users = new Users(name.getText().toString(), email.getText().toString(), confirmPassword.getText().toString(),"android.resource://com.example.hustle/drawable/defaultpic");
                                            String id = task.getResult().getUser().getUid();
                                            firebaseDatabase.getReference().child("Users").child(id).setValue(users);
                                            //firebaseDatabase.getReference().child("Users").child(id).child("name").setValue(name.getText().toString());
                                            //firebaseDatabase.getReference().child("Users").child("mail").setValue(email.getText().toString());
                                            //firebaseDatabase.getReference().child("Users").child("password").setValue(confirmPassword.getText().toString());
                                            //firebaseDatabase.getReference().child("Users").child(id).child("image").setValue("android.resource://com.example.hustle/drawable/defaultpic");



                                        } else {
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Passwords do not match",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }


            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        RegisterActivity.this.finish();
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
    }
}
