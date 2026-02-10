package com.app.chatx;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChatACtivity extends AppCompatActivity {
    TextView backBtn, usrNm;
    RecyclerView recyclerView;
    EditText messageBox;
    Button sendBtn, attachBtn,voiceBtn;
    ArrayList<Messages> messages;
    ChatAdapter adapter;
    private static final int MEDIA_PERMISSION_CODE = 200;
    String chatId;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderId, receiverId;
    MediaRecorder recorder;
    String audioPath;
    boolean isRecording=false;

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
        voiceBtn=findViewById(R.id.vcBtn);
        sendBtn=findViewById(R.id.sendBtn);
        attachBtn=findViewById(R.id.attachBtn);
        backBtn=findViewById(R.id.ChtScrn_bckBtn);
        backBtn.setOnClickListener(v->{
            startActivity(new Intent(this, MainActivity.class));
        });
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        senderId=auth.getUid();
        receiverId=getIntent().getStringExtra("uid");
        messages=new ArrayList<>();
        adapter=new ChatAdapter(messages,FirebaseAuth.getInstance().getUid());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        attachBtn.setOnClickListener(v->{
            Intent intent=new Intent();
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/*","video/*"});
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,101);
        });

        voiceBtn.setOnClickListener(v->{
            if(!isRecording){
                startRecording();
                voiceBtn.setText("⏺️");
            }
            else{
                stopRecording();
                voiceBtn.setText("🎙️");
            }
        });

        if(senderId.compareTo(receiverId)<0){
            chatId=senderId+"_"+receiverId;
        }
        else{
            chatId=receiverId+"_"+senderId;
        }
        sendBtn.setOnClickListener(v->{
            Messages msg=new Messages(senderId,messageBox.getText().toString(),"","text","");
            database.getReference("Chats").child(chatId).push().setValue(msg);
            messageBox.setText("");
        });
        database.getReference("Chats").child(chatId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot data: snapshot.getChildren()){
                            messages.add(data.getValue(Messages.class));
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder=null;
            isRecording=false;
            uploadAudioToFirebase(audioPath);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadAudioToFirebase(String audioPath) {
        Uri audioUri=Uri.fromFile(new File(audioPath));
        StorageReference storageReference=FirebaseStorage.getInstance()
                .getReference("VoiceMessage")
                .child(System.currentTimeMillis()+".3gp");
        storageReference.putFile(audioUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri->{
                            sendAudioMessage(uri.toString());
                        }));
    }

    private void sendAudioMessage(String string) {
        String senderId=FirebaseAuth.getInstance().getUid();
        Messages msg=new Messages(senderId,"","","audio",string);
        DatabaseReference ref=FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatId);
        ref.push().setValue(msg);
    }

    private void startRecording() {
        try{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},101);
                return;
            }
            audioPath=getExternalCacheDir().getAbsolutePath()+"/voice_"+System.currentTimeMillis()+".3gp";
            recorder=new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audioPath);
            recorder.prepare();
            recorder.start();
            isRecording=true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK && data !=null){
            Uri fileUri=data.getData();
            String type=getContentResolver().getType(fileUri);
            if(type.startsWith("image")){
                uploadMedia(fileUri, "image");
            } else if (type.startsWith("video")) {
                uploadMedia(fileUri,"video");

            }
        }
    }

    private void uploadMedia(Uri fileUri, String mediaType) {
        StorageReference storageRef=FirebaseStorage.getInstance().getReference("ChatsMedia")
                .child(System.currentTimeMillis()+"");
        storageRef.putFile(fileUri).continueWithTask(task-> storageRef.getDownloadUrl())
                .addOnSuccessListener(uri->{
                    Messages msg=new Messages(
                            senderId,"","",uri.toString(),mediaType
                    );
                    database.getReference("Chats")
                            .child(chatId).push().setValue(msg);
                });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] Permissions, @NonNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode, Permissions, grantResults);
        if(requestCode==101 && grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            startRecording();
            voiceBtn.setText("⏺️");
        }
    }

}