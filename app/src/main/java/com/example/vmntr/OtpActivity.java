package com.example.vmntr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.vmntr.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding binding;
    String phoneNo;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String verificationId;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        binding.etOtp.requestFocus();

        phoneNo=getIntent().getStringExtra("phoneNo").toString();
        binding.verifyPhone.setText("Verify +91"+phoneNo);
        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        dialog=new ProgressDialog(OtpActivity.this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();

        PhoneAuthOptions options= PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91"+phoneNo)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OtpActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId=s;
                        dialog.dismiss();
                        InputMethodManager imm=(InputMethodManager)getSystemService(OtpActivity.this.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,2);
                        binding.etOtp.requestFocus();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
//        binding.etOtp.setOtpCompletionListener(new OnOtpCompletionListener() {
//            @Override
//            public void onOtpCompleted(String otp) {
//                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,otp);
//                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            startActivity(new Intent(OtpAtivity.this,MainActivity.class));
//                            finish();
//                        }else{
//                            Toast.makeText(OtpAtivity.this, "Otp fail", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                });
//            }
//        });
        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp=binding.etOtp.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(OtpActivity.this,AccountActivity.class));
                            finish();
                        }else{
                            Toast.makeText(OtpActivity.this, "Otp fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });

    }
}