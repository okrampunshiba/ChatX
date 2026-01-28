package com.app.chatx;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatACtivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText messageBox;
    Button sendBtn, OpnFilBtn;
    ArrayList<Messages> messages;
    ChatAdapter adapter;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderId, receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chat_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.chatRecycler);
        messageBox=findViewById(R.id.messageBox);
        sendBtn=findViewById(R.id.sendBtn);

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        senderId=auth.getUid();
        receiverId=getIntent().getStringExtra( "uid");
        messages=new ArrayList<>();
        adapter=new ChatAdapter(messages,FirebaseAuth.getInstance().getUid());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        String chatId;
        if(senderId. compareTo(receiverId)<0) {
            chatId = senderId + "_" + receiverId;
        }
            else {
            chatId = receiverId + "_" + senderId;
        }
        sendBtn.setOnClickListener( v-> {
                    Messages msg = new Messages(senderId, messageBox.getText().toString());
                    database.getReference("Chats").child(chatId).push().setValue(msg);
                    messageBox.setText("");
                });
            database.getReference(  "Chats") . child(chatId)
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange (@NonNull DataSnapshot snapshot) {
                            messages.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                messages.add(data.getValue(Messages.class));

                            }
                            adapter.notifyDataSetChanged();
                            recyclerView. scrollToPosition(messages.size()-1);
                        }
                                @Override
                                public void onCancelled (@NonNull DatabaseError error){

                                }
                    });
        }

    }