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

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String senderRoom,receiverRoom;
    String senderId,receiverId,sourceLanguage;
    ArrayList<MessageModels> list;
    MessagesAdapters adapters;
    HashMap<String ,String> langCode=new HashMap<>();
    private String originalText;
    private String translatedText;
    private boolean connected;
    Translate translate;
    String targetLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        addLanguageCodeInHashMap();


        messageShow();
        allOnClick();



    }

    private void addLanguageCodeInHashMap() {
        langCode.put("Hindi","hi");
        langCode.put("Urdu","ur");
        langCode.put("English","en");
        langCode.put("Arabic","ar");
        langCode.put("French","fr");
        langCode.put("German","de");
        langCode.put("Afrikaans","af");
        langCode.put("Albanian","sq");
        langCode.put("Amharic","am");
        langCode.put("Armenian","hy");
        langCode.put("Bengali","bn");
        langCode.put("Bulgarian","bg");
        langCode.put("Chinese","zh");
        langCode.put("Gujarati","gu");
        langCode.put("Greek","el");
        langCode.put("Dutch","nl");
        langCode.put("Hungarian","hu");
        langCode.put("Irish","ga");
        langCode.put("Italian","it");
        langCode.put("Japanese","ja");
        langCode.put("Kannada","kn");
        langCode.put("Korean","ko");
        langCode.put("Latin","la");
        langCode.put("Marathi","mr");
        langCode.put("Nepali","ne");
        langCode.put("Odia (Oriya)","or");
        langCode.put("Russian","ru");
        langCode.put("Persian","fa");
        langCode.put("Romanian","ro");
        langCode.put("Spanish","es");
        langCode.put("Tamil","ta");
        langCode.put("Telugu","te");
        langCode.put("Thai","th");
        langCode.put("Punjabi","pa");





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
        targetLanguage=getIntent().getStringExtra("language");

        if(userName==null){
            binding.userNames.setText(auth.getCurrentUser().getPhoneNumber());
        }else{binding.userNames.setText(userName);}
        String profilePic=getIntent().getStringExtra("image");
        String time=getIntent().getStringExtra("time");
       // binding.lastSeen.setText("Last seen by "+time);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_user).into(binding.profileImage);


        senderRoom=senderId+receiverId;
        receiverRoom=receiverId+senderId;

        list=new ArrayList<>();
        adapters=new MessagesAdapters(ChatActivity.this,list,senderRoom,receiverRoom);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.messageRecyclerView.setAdapter(adapters);
        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String messageText=binding.writeMessage.getText().toString();
                if(messageText.isEmpty()){
                    return;
                }
                if (checkInternetConnection()) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    translate();

                } else {

                    //If not, display "no connection" warning:
                    binding.writeMessage.setText(getResources().getString(R.string.no_connection));
                    return;
                }
                Date date=new  Date();
                MessageModels originalMessage=new MessageModels(messageText,senderId,date.getTime());
                MessageModels translatedMessage=new MessageModels(translatedText,senderId,date.getTime());
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
                                .setValue(translatedMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
            }
        });
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "Profile", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();


        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate() {

        //Get input text to be translated:
        originalText = binding.writeMessage.getText().toString();
        //traget language code
        String targetLanguageCode=langCode.get(targetLanguage);
        // translate = Translate.
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(targetLanguageCode), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
       // translatedTv.setText(translatedText);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
}