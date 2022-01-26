package com.example.interestproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.interestproject.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView username;
    CircleImageView profilPicture;
    Button backButton;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar =  findViewById(R.id.toolbar_chat);

        backButton = findViewById(R.id.back_button_chat);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        username = findViewById(R.id.username_chat_toolbar);
        profilPicture = findViewById(R.id.profil_picture_chat_toolbar);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);



        reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             User user = dataSnapshot.getValue(User.class);
             username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {

                    Glide.with(getApplicationContext())
                            .load("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg")
                            .into(profilPicture);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .into(profilPicture);
                }
            }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });


    }
}
