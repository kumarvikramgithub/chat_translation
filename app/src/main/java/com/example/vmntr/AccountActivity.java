package com.example.vmntr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.vmntr.Models.Users;
import com.example.vmntr.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImage;
    String imageUri;
    ProgressDialog dialog;
    ProgressDialog dialogLoad;
    HashMap<String ,String> langCode=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(AccountActivity.this,LoginActivity.class));
            }
        });
        ArrayList<String> ids=new ArrayList<>();
        ids.add("Select a Language");
        ids.add("Hindi");
        ids.add("Urdu");
        ids.add("English");
        ids.add("Arabic");
        ids.add("French");
        ids.add("German");
        ids.add("Afrikaans");
        ids.add("Albanian");
        ids.add("Amharic");
        ids.add("Armenian");
        ids.add("Bengali");
        ids.add("Bulgarian");
        ids.add("Chinese");
        ids.add("Gujarati");
        ids.add("Greek");
        ids.add("Dutch");
        ids.add("Hungarian");
        ids.add("Irish");
        ids.add("Italian");
        ids.add("Japanese");
        ids.add("Kannada");
        ids.add("Korean");
        ids.add("Latin");
        ids.add("Marathi");
        ids.add("Nepali");
        ids.add("Odia (Oriya)");
        ids.add("Russian");
        ids.add("Persian");
        ids.add("Romanian");
        ids.add("Spanish");
        ids.add("Tamil");
        ids.add("Telugu");
        ids.add("Thai");
        ids.add("Punjabi");


//        Spinner for updates Ledger

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(AccountActivity.this,android.R.layout.simple_spinner_item,ids);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.laguageSpiner.setAdapter(arrayAdapter);
        binding.laguageSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                binding.laguageSpiner.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog=new ProgressDialog(AccountActivity.this);
        dialog.setMessage("Updating Account.....");
        dialog.setCancelable(false);

        dialogLoad=new ProgressDialog(AccountActivity.this);
        dialogLoad.setMessage("Please Wait..");
        dialogLoad.setCancelable(false);
        dialogLoad.show();
        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren() ){
                            Users users = snapshot.getValue(Users.class);
                            Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.ic_user).into(binding.profileImage);
                            binding.etName.setText(users.getName());
                            binding.etEmail.setText(users.getEmail());
                            binding.etAboutUs.setText(users.getAboutUs());
                            //binding.laguageSpiner.set

                        }
                        dialogLoad.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
        dialog.setCancelable(false);
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,37);
            }
        });
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.show();
                Users user=new Users();
                user.setUserId(auth.getCurrentUser().getUid());
                user.setName(binding.etName.getText().toString());
                user.setEmail(binding.etEmail.getText().toString());
                user.setPhoneNumber(auth.getCurrentUser().getPhoneNumber());
                user.setAboutUs(binding.etAboutUs.getText().toString());
                user.setUserLanguage(binding.laguageSpiner.getSelectedItem().toString());
                if(selectedImage!=null){
                    StorageReference reference=storage.getReference().child("ProfileImage").
                            child(auth.getCurrentUser().getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUri=uri.toString();
                                        database.getReference().child("User").child(auth.getCurrentUser().getUid()).
                                                child("profileImage").setValue(imageUri);
                                        Toast.makeText(AccountActivity.this, "Image Uri", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }

                database.getReference().child("User").child(auth.getCurrentUser().getUid()).setValue(user);
                startActivity(new Intent(AccountActivity.this,MainActivity.class));
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                binding.profileImage.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }
}