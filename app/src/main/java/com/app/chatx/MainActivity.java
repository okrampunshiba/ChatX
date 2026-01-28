package com.app.chatx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView userList;
    ArrayList<String> users;
    ArrayList<String> userIds;
    FirebaseAuth auth;
    Button lgOtBtn;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView emptyText = findViewById(R.id.emptyText);
        userList=findViewById(R.id.userList);
        users=new ArrayList<>();
        userIds=new ArrayList<>();
        lgOtBtn=findViewById(R.id.lgotBtn);
        lgOtBtn.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainActivity.this,LoginActiviy.class);
            intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        if(auth. getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActiviy.class));
            finish();
            return;
        }
        database.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                userIds.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user != null && !user.uid.equals(auth.getUid())) {
                        users.add(user.email);
                        userIds.add(user.uid);
                    }
                }

                userList.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, users));

                emptyText.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        userList.setOnItemClickListener((parent,view,position,id)->{
            Intent intent=new Intent(this,ChatACtivity.class);
            intent.putExtra("uid",userIds.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActiviy.class));
            finish();
        }
    }
}