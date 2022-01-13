package com.example.hustle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerificationActivity extends AppCompatActivity {

    Button next;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String name,email,password;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        next=findViewById(R.id.verified);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        //next.setVisibility(View.GONE);

        getSupportActionBar().hide();

        if(firebaseUser.isEmailVerified()){
            //next.setVisibility(View.VISIBLE);
            intent=getIntent();
            name=intent.getStringExtra("userName");
            email=intent.getStringExtra("userEmail");
            password=intent.getStringExtra("userPassword");
            Users users = new Users(name, email, password,"android.resource://com.example.hustle/drawable/defaultpic");
            String id = intent.getStringExtra("userId");
            firebaseDatabase.getReference().child("Users").child(id).setValue(users);
            firebaseDatabase.getReference().child("Users").child(id).child("image").setValue("android.resource://com.example.hustle/drawable/defaultpic");
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailVerificationActivity.this.finish();
                startActivity(new Intent(EmailVerificationActivity.this,LoginActivity.class));

            }
        });

    }
}