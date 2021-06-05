package com.example.vmntr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmntr.Models.MessageModels;
import com.example.vmntr.R;
import com.example.vmntr.databinding.ReceiverSampleBinding;
import com.example.vmntr.databinding.SenderSampleBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapters extends RecyclerView.Adapter {
    Context context;
    ArrayList<MessageModels> messages;
    String senderRoom,receiverRoom ;
    final int SENT_ITEM=1,RECEIVE_ITEM=2;
    public MessagesAdapters(Context context, ArrayList<MessageModels> messages, String senderRoom, String receiverRoom ) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENT_ITEM){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_sample,parent,false);
            return new SentViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_sample,parent,false);
            return new ReceiveViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModels message=messages.get(position);
        int reactions[]=new int[]{
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
                R.drawable.ic_plus,
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (positionEmoji) -> {
            if(holder.getClass()==SentViewHolder.class){
                SentViewHolder viewHolder=(SentViewHolder)holder;
                viewHolder.binding.senderFeeling.setImageResource(reactions[positionEmoji]);
                viewHolder.binding.senderFeeling.setVisibility(View.VISIBLE);
            }else{
                ReceiveViewHolder viewHolder=(ReceiveViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[positionEmoji]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            message.setFeeling(positionEmoji);
            FirebaseDatabase.getInstance().getReference()
                    .child("PalChat")
                    .child(senderRoom)
                    .child("message")
                    .child(message.getMessageId()).setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("PalChat")
                    .child(receiverRoom)
                    .child("message")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });


        if(holder.getClass()==SentViewHolder.class){
            SentViewHolder viewHolder=(SentViewHolder)holder;
            viewHolder.binding.senderMessage.setText(message.getMessage());
            if(message.getFeeling()>=0){
                viewHolder.binding.senderFeeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.senderFeeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.senderFeeling.setVisibility(View.GONE);
            }
            viewHolder.binding.senderMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
        }else{
            ReceiveViewHolder viewHolder=(ReceiveViewHolder) holder;
            viewHolder.binding.receiverMessage.setText(message.getMessage());
            if(message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.receiverMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageModels message=messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return SENT_ITEM;
        }else{
            return RECEIVE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public class SentViewHolder extends RecyclerView.ViewHolder {
        SenderSampleBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= SenderSampleBinding.bind(itemView);
        }
    }
    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        ReceiverSampleBinding binding;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ReceiverSampleBinding.bind(itemView);
        }
    }
}