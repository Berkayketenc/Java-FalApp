package com.infobk.fall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button girisyap ;
    EditText mailtext;
    EditText sifretext;
    Button hesapolustur ;

     FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        girisyap = findViewById(R.id.loginButton);
        mailtext = findViewById(R.id.emailText);
        sifretext = findViewById(R.id.passwordText);
        hesapolustur=findViewById(R.id.hesapolustur);



        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser !=null){
            Intent intent = new Intent(LoginActivity.this, SaveActivity.class);
            startActivity(intent);
            finish();
        }

        girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mailtext.getText().toString();
                String password = sifretext.getText().toString();



                firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(LoginActivity.this, SaveActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(LoginActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


        hesapolustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(LoginActivity.this, UsersCreateActivity.class);
                startActivity(intent);


            }
        });

    }


}