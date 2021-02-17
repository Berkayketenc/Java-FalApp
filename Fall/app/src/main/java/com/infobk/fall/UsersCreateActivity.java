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


public class UsersCreateActivity extends AppCompatActivity {

    Button kaydet;
    EditText mail;
    EditText Sifre;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        kaydet=findViewById(R.id.kaydet);

        mail=findViewById(R.id.mail);

        Sifre=findViewById(R.id.sifre);

        firebaseAuth = FirebaseAuth.getInstance();

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = Sifre.getText().toString();



                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Toast.makeText(UsersCreateActivity.this,"Kullanıcı Oluşturuldu",Toast.LENGTH_LONG).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UsersCreateActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });
                Intent intent = new Intent(UsersCreateActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


}