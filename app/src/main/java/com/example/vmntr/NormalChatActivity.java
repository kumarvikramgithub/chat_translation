package com.example.vmntr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.example.vmntr.Adapters.MessagesAdapters;
import com.example.vmntr.Models.MessageModels;
import com.example.vmntr.Models.Users;
import com.example.vmntr.databinding.ActivityChatBinding;
import com.example.vmntr.databinding.ActivityNormalChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NormalChatActivity extends AppCompatActivity {

    ActivityNormalChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderRoom,receiverRoom;
    String senderId,receiverId,sourceLanguage;
    ArrayList<MessageModels> list;
    MessagesAdapters adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNormalChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        messageShow();
        allOnClick();



    }



    private void messageShow() {
        senderId=auth.getUid();
        receiverId=getIntent().getStringExtra("id");
        String userName=getIntent().getStringExtra("name");
        database.getReference().child("User").child(senderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user= snapshot.getValue(Users.class);
                sourceLanguage = user.getUserLanguage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(userName==null){
            binding.userNames.setText(auth.getCurrentUser().getPhoneNumber());
        }else{binding.userNames.setText(userName);}
        String profilePic=getIntent().getStringExtra("image");
        String time=getIntent().getStringExtra("time");
        //binding.lastSeen.setText("Last seen by "+time);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_user).into(binding.profileImage);


        senderRoom=senderId+receiverId;
        receiverRoom=receiverId+senderId;

        list=new ArrayList<>();
        adapters=new MessagesAdapters(NormalChatActivity.this,list,senderRoom,receiverRoom);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(NormalChatActivity.this));
        binding.messageRecyclerView.setAdapter(adapters);
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String messageText=binding.writeMessage.getText().toString();
                if(messageText.isEmpty()){
                    return;
                }

                Date date=new  Date();
                MessageModels originalMessage=new MessageModels(messageText,senderId,date.getTime());
                binding.writeMessage.setText("");

                String messageId=database.getReference().push().getKey();

                database.getReference().child("PalChat")
                        .child(senderRoom)
                        .child("message")
                        .child(messageId)
                        .setValue(originalMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("PalChat")
                                .child(receiverRoom)
                                .child("message")
                                .child(messageId)
                                .setValue(originalMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
        database.getReference().child("PalChat")
                .child(senderRoom)
                .child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    MessageModels models=dataSnapshot.getValue(MessageModels.class);
                    models.setMessageId(dataSnapshot.getKey());
                    list.add(models);
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void allOnClick() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NormalChatActivity.this,MainActivity.class));
            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NormalChatActivity.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });


    }

}