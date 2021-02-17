package com.infobk.fall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class SaveActivity extends AppCompatActivity {



    FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


    ImageView fotogoster;
    ImageButton kamera ;
    ImageButton galeri;
    Button kaydet;
    Bitmap secilenresim;
    Bitmap secilenresimkamera;
    Uri imagedata ;

    private NotificationCompat.Builder builder;

    Intent i ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.cikisyap){
            firebaseAuth.signOut();
            Intent intentGirisYap = new Intent(SaveActivity.this, LoginActivity.class);
            startActivity(intentGirisYap);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        fotogoster = findViewById(R.id.imageView);
        kamera = findViewById(R.id.imageButton);
        galeri=findViewById(R.id.imageButton2);
        kaydet=findViewById(R.id.button2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }
    public void gonder ( View view){

        if(imagedata !=null){

            UUID uuid = UUID.randomUUID();
            final String imagename="images/"+uuid+".jpg";
            Toast.makeText(SaveActivity.this,"Gönderiliyor...",Toast.LENGTH_LONG).show();


            storageReference.child(imagename).putFile(imagedata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //stroge kaydettik
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference DownloadReference = FirebaseStorage.getInstance().getReference(imagename);
                    DownloadReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String DownloadUrl = uri.toString();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //
                            String userEmail=firebaseUser.getEmail();

                            HashMap<String, Object>kaydetdata = new HashMap<>();
                            kaydetdata.put("Email",userEmail);
                            kaydetdata.put("downloadurl",DownloadUrl);
                            kaydetdata.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Fal").add(kaydetdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {



                                    Intent intent = new Intent(SaveActivity.this, NoneActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    zamanlama();



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SaveActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                                }
                            });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SaveActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                }
            });


        }

    }

    public void zamanlama(){
        new CountDownTimer(30000,1000){

            @Override
            public void onTick(long l) {


            }

            @Override
            public void onFinish() {

                bildirim();

            }
        }.start();

    }

    public void bildirim(){
        NotificationManager BildirimManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(SaveActivity.this, NotificationActivity.class);

        PendingIntent falyorumIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            String kanalId="kanalId";
            String kanalAd="kanalAd";
            String kanalTanım = "KanalTanım";
            int kanalOncelik = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel kanal  = BildirimManager.getNotificationChannel(kanalId);
            if(kanal == null){ // oreo surumu ıcın

                kanal = new NotificationChannel(kanalId,kanalAd,kanalOncelik);
                kanal.setDescription(kanalTanım);
                BildirimManager.createNotificationChannel(kanal);
            }

            builder = new NotificationCompat.Builder(this,kanalId);
            builder.setContentTitle("Falınız Yorumlandı!");
            builder.setContentText("Bildirime Tıklayın");
            builder.setSmallIcon(R.drawable.bildirim);
            builder.setAutoCancel(true);
            builder.setContentIntent(falyorumIntent);

        }else{
            builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Falınız Yorumlandı!");
            builder.setContentText("Bildirime Tıklayın!");
            builder.setSmallIcon(R.drawable.bildirim);
            builder.setAutoCancel(true);
            builder.setContentIntent(falyorumIntent);
            builder.setPriority(Notification.PRIORITY_HIGH);


        }


        BildirimManager.notify(1,builder.build());
    }




    public void galeri (View view){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentGaleri = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGaleri,2); //sonuç için başlat

        }



    }

    public void kameraa(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},5);
        }else{
            i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i,5);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==1){
            if(grantResults.length >0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){

                Intent intentGaleri = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGaleri,2);
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==2 && resultCode==RESULT_OK && data !=null){

            imagedata = data.getData();

            try {
                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imagedata);
                    secilenresim = ImageDecoder.decodeBitmap(source);
                    fotogoster.setImageBitmap(secilenresim);

                }else{
                    secilenresim = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagedata);
                    fotogoster.setImageBitmap(secilenresim);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}