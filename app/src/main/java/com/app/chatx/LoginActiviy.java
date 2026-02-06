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

public class LoginActiviy extends AppCompatActivity {
    EditText email, password;
    TextView registerBtn, frgtPswdBtn;
    Button loginBtn;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activiy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        frgtPswdBtn=findViewById(R.id.frgetBtn);
        email=findViewById(R.id.emlField);
        password=findViewById(R.id.pswField);
        loginBtn=findViewById(R.id.lginBtn);
        registerBtn=findViewById(R.id.sgnupBtn);
        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActiviy.this, MainActivity.class));
            finish();
            return;
        }

        loginBtn.setOnClickListener(v->{
            auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        startActivity(new Intent(this, MainActivity.class));
                    });
        });
        registerBtn.setOnClickListener(v->{
            startActivity(new Intent(this,ResgisterActivity.class));
        });
        frgtPswdBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();

            if (userEmail.isEmpty()) {
                email.setError("Enter your email first");
                email.requestFocus();
                return;
            }

            auth.sendPasswordResetEmail(userEmail)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}