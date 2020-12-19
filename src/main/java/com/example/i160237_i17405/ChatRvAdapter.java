package com.example.i160237_i17405;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatRvAdapter extends RecyclerView.Adapter<ChatRvAdapter.MyViewHolder> {

    final int CHAT_BUBBLE_RIGHT=1;
    final int CHAT_BUBBLE_LEFT=0;

    Contact myProfile;

    ArrayList<Message> mChat;
    Context context;
    public ChatRvAdapter(ArrayList<Message> mChat, Context context) {
        this.mChat = mChat;
        this.context=context;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this view type is determined in getItemViewType function
        if (viewType== CHAT_BUBBLE_RIGHT)
        {
            View itemView= LayoutInflater.from(context).inflate(R.layout.chat_bubble_right, parent, false);
            return new MyViewHolder(itemView);
        }
        else
        {
            View itemView= LayoutInflater.from(context).inflate(R.layout.chat_bubble_left, parent, false);
            return new MyViewHolder(itemView);
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Text and Time is set
        holder.textBody.setText(mChat.get(position).getText());
        holder.timeStamp.setText(mChat.get(position).getTimestamp());


        //Image is set if url aint empty
        if (!mChat.get(position).getImageUrl().isEmpty()) {
            holder.sentImage.setVisibility(View.GONE);
            Picasso.get().load(mChat.get(position).getImageUrl()).into(holder.sentImage);
            holder.sentImage.setVisibility(View.VISIBLE);
        }
        else{
            holder.sentImage.setVisibility(View.GONE);
        }

        holder.seenTick.setImageDrawable(context.getResources().getDrawable(R.drawable.tick_icon));
        if(mChat.get(position).getStatus().contains("unseen")) {
            holder.seenTick.setColorFilter(ContextCompat.getColor(context, R.color.color_grey));
        }
        else {
            holder.seenTick.setColorFilter(ContextCompat.getColor(context, R.color.color_primary));
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textBody,timeStamp;
        ImageView seenTick, sentImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textBody=itemView.findViewById(R.id.textBodyChatBubble);
            timeStamp=itemView.findViewById(R.id.timeChatBubble);
            seenTick= itemView.findViewById(R.id.seenStatus);
            sentImage= itemView.findViewById(R.id.textImage);
        }
    }

    @Override
    public int getItemViewType(int position) {

        //checks if my ID is same as sender
        if(mChat.get(position).getSenderId().equals(myProfile.getUserId()))
        {
            return CHAT_BUBBLE_RIGHT;
        }
        else{
            return CHAT_BUBBLE_LEFT;
        }
    }

    public String convertTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(((AppCompatActivity)context).getResources().getConfiguration().locale);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000L);
        String dateString = DateFormat.format("h:mm a", cal).toString();
        return dateString;
    }

    public void setMyProfille(Contact myProfile) {
        this.myProfile= myProfile;
    }
}
