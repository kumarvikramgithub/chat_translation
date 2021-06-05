package com.example.vmntr.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmntr.ChatActivity;
import com.example.vmntr.Models.MessageModels;
import com.example.vmntr.Models.Users;
import com.example.vmntr.R;
import com.example.vmntr.databinding.UserSampleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapters extends RecyclerView.Adapter<UserAdapters.UserViewHolder> {
    Context context;
    ArrayList<Users> list;
    String time;
    public UserAdapters(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Users models=list.get(position);

        Picasso.get().load(models.getProfileImage()).placeholder(R.drawable.ic_user).into(holder.binding.profileImage);
        holder.binding.userName.setText(models.getName());
        FirebaseDatabase.getInstance().getReference().child("PalChat")
                .child(models.getUserId().concat(FirebaseAuth.getInstance().getUid()))
                .child("message").orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    MessageModels message = dataSnapshot.getValue(MessageModels.class);
                    holder.binding.lastMessage.setText(message.getMessage());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageModels message=new MessageModels();
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("id",models.getUserId());
                intent.putExtra("name",models.getName());
                intent.putExtra("image",models.getProfileImage());
                intent.putExtra("language",models.getUserLanguage());
                intent.putExtra("time",Long.toString(message.getTimestamp()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=UserSampleBinding.bind(itemView);
        }
    }
}
