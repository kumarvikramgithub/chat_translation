package com.example.vmntr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.vmntr.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth= FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        if(auth.getCurrentUser()!=null){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding.etPhone.requestFocus();
        binding.btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo=binding.etPhone.getText().toString();
                if(phoneNo.isEmpty()||phoneNo.length()!=10){
                    binding.etPhone.setError("phone must be 10 digit");
                    return;
                }
                Intent intent=new Intent(LoginActivity.this,OtpActivity.class);
                intent.putExtra("phoneNo",phoneNo);
                startActivity(intent);
            }
        });
    }
}