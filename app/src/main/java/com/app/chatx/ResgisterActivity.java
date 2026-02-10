package com.app.chatx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ResgisterActivity extends AppCompatActivity {
    EditText email, password;
    TextView backBtn;
    Button registerBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.resgister_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backBtn=findViewById(R.id.rgstrScrn_bckBtn);

        email=findViewById(R.id.emlField);
        password=findViewById(R.id.pswField);

        registerBtn=findViewById(R.id.rgstrBtn);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        backBtn.setOnClickListener(v->{
            startActivity(new Intent(this, LoginActiviy.class));
        });
        registerBtn.setOnClickListener(v->{


            String mail=email.getText().toString().trim();
            String pass=password.getText().toString().trim();

            if (mail.isEmpty()||pass.isEmpty() ){
                Toast.makeText(this, "Invalid Information", Toast.LENGTH_SHORT).show();
            }
            auth.createUserWithEmailAndPassword(mail,pass)
                    .addOnSuccessListener(authResult -> {
                        String uid=auth.getUid();
                        User user=new User(uid,mail);
                        database.getReference("Users").child(uid).setValue(user)
                                        .addOnSuccessListener(unused->{
                                            startActivity(new Intent(ResgisterActivity.this, MainActivity.class));
                                        });
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e->{
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        });
    }
}