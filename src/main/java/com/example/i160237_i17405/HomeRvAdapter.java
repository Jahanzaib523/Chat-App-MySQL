package com.example.i160237_i17405;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRvAdapter extends RecyclerView.Adapter<HomeRvAdapter.MyViewHolder> {

    ArrayList<Contact> listOfContacts;
    Context context;

    Contact myProfile;

    ArrayList<Message> listOfLastMessages;


    public HomeRvAdapter(ArrayList<Contact> listOfContacts, Context context) {
        this.listOfContacts= listOfContacts;
        this.context=context;
    }
    public void setMyProfile(Contact myProfile) {
        this.myProfile = new Contact(myProfile);
    }


    @NonNull
    @Override
    public HomeRvAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.contact_row_home_screen, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRvAdapter.MyViewHolder holder, int position) {
        Contact receiverContact = listOfContacts.get(position);
        final String fullName = listOfContacts.get(position).getFirstName() + " " + listOfContacts.get(position).getLastName();
        holder.name.setText(fullName);


        //get conversation id

        holder.latestMessage.setText("Tap to view conversation");
        holder.date.setText("");
        holder.unreadIndicator.setVisibility(View.GONE);
        final Typeface face = ResourcesCompat.getFont(context, R.font.roboto_light);
//        Typeface face = Typeface.createFromAsset(((AppCompatActivity)context).getAssets(), "roboto_light.ttf");
        holder.latestMessage.setTypeface(face);
        holder.latestMessage.setTextColor(((AppCompatActivity)context).getResources().getColor(R.color.color_grey));

        //holder.imageIcon.setImageResource();

        if (listOfContacts.get(position).getOnlineStatus().contains("online")) {
            holder.onlineStatus.setVisibility(View.VISIBLE);
        } else {
            holder.onlineStatus.setVisibility(View.GONE);
        }

        if (listOfContacts.get(position).getImageUrl() != null && !listOfContacts.get(position).getImageUrl().isEmpty()) {
            Picasso.get().load(listOfContacts.get(position).getImageUrl()).into(holder.imageIcon);
        } else {
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
        }

        holder.rowItself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToChatScreen = new Intent(context, ChatScreen.class);
                goToChatScreen.putExtra("CONTACT_PROFILE_TO_CHAT", listOfContacts.get(position));
                if (myProfile == null) {
                    Log.i("SENDER_CONTACT_HOME", "sender is null");
                }
                goToChatScreen.putExtra("MY_PROFILE_TO_CHAT", myProfile);
                context.startActivity(goToChatScreen);
            }
        });

        holder.rowItself.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Long Pressed " + Integer.toString(position) + " ", Toast.LENGTH_SHORT).show();

                DeleteBottomSheetDialog deleteBottomSheetDialog = new DeleteBottomSheetDialog(position);
                deleteBottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "deleteBottomSheet");

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfContacts.size();
    }

    public void setListOfLastMessages(ArrayList<Message> listOfCorrespondingLastMessage) {
        listOfLastMessages=listOfCorrespondingLastMessage;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageIcon, onlineStatus,unreadIndicator;
        TextView name, latestMessage, date;
        RelativeLayout rowItself;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name= itemView.findViewById(R.id.nameOfContactRowHome);
            date= itemView.findViewById(R.id.timeRowHome);
            latestMessage= itemView.findViewById(R.id.latestMessageRowHome);
            onlineStatus= itemView.findViewById(R.id.onlineStatusRowHome);
            imageIcon= itemView.findViewById(R.id.imageContactRowHome);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            rowItself= itemView.findViewById(R.id.rowHomeItself);
        }
    }



    public String convertTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(((AppCompatActivity)context).getResources().getConfiguration().locale);
        cal.setTimeInMillis(Long.parseLong(timestamp) * 1000L);
        String dateString = DateFormat.format("h:mm a", cal).toString();
        return dateString;
    }
}
