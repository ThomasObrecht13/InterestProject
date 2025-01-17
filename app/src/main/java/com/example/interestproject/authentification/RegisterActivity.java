package com.example.interestproject.authentification;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interestproject.MainActivity;
import com.example.interestproject.R;
import com.example.interestproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegEmail, etRegPassword, etRegLastname, etRegFirstName, etRegUsername;
    private TextView tvLoginHere;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegLastname = findViewById(R.id.registerLastname);
        etRegFirstName = findViewById(R.id.registerFirstname);
        etRegUsername = findViewById(R.id.registerUsername);
        etRegEmail = findViewById(R.id.registerEmail);
        etRegPassword = findViewById(R.id.registerPassword);

        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view ->{
            String email = etRegEmail.getText().toString();
            String password = etRegPassword.getText().toString();
            String username = etRegUsername.getText().toString();

            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(etRegLastname.getText().toString()) || TextUtils.isEmpty(etRegFirstName.getText().toString())){
                Toast.makeText(RegisterActivity.this,"Entrer vos données",Toast.LENGTH_SHORT);
            }else if(password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Mot de passe trop court", Toast.LENGTH_SHORT);
            }else{
                register(username, email, password);
            }
        });

        tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

    }

    private void register(String username, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    User user = new User(
                            userid,
                            username,
                            etRegLastname.getText().toString(),
                            etRegFirstName.getText().toString(),
                            "",
                            "default",
                            "offline",
                            "");

                    HashMap<String, String> hashMap = user.toMap();


                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"You can't register",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


