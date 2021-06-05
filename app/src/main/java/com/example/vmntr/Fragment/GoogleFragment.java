package com.example.vmntr.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vmntr.Adapters.UserAdapters;
import com.example.vmntr.Models.Users;
import com.example.vmntr.R;
import com.example.vmntr.databinding.FragmentGoogleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoogleFragment extends Fragment {



    public GoogleFragment() {
        // Required empty public constructor
    }
    FragmentGoogleBinding binding;
    ArrayList<Users> list=new ArrayList<>();
    FirebaseDatabase database;
    ArrayList<String> contact =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentGoogleBinding.inflate(inflater,container,false);
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},PackageManager.PERMISSION_GRANTED);
        }else{
            Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.TYPE},
                    null,null,null);
            while (cursor.moveToNext()){
                contact.add(cursor.getString(0));
            }
        }

        UserAdapters chatUserAdapter =new UserAdapters(getContext(),list);
        binding.chatRecyclerView.setAdapter(chatUserAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.showShimmerAdapter();
        database =FirebaseDatabase.getInstance();
        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users =dataSnapshot.getValue(Users.class);
                    if(!users.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){

                        list.add(users);
                    }
//                    if(contact.contains(users.getPhoneNumber())){
//
//                        list.add(users);
//                    }

                }
                binding.chatRecyclerView.hideShimmerAdapter();
                chatUserAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}