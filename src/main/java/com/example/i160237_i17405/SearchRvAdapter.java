package com.example.i160237_i17405;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRvAdapter extends RecyclerView.Adapter<SearchRvAdapter.MyViewHolder> {

    ArrayList<Contact> listOfContacts;
    Context context;


    Contact myProfile;//contact of current signed in user

    public SearchRvAdapter(ArrayList<Contact> listOfContacts, Context context) {
        this.listOfContacts= listOfContacts;
        this.context=context;
    }

    public void setMyProfile(Contact myProfile) {
        this.myProfile = myProfile;
    }


    @NonNull
    @Override
    public com.example.i160237_i17405.SearchRvAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.contact_row_search, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.i160237_i17405.SearchRvAdapter.MyViewHolder holder, int position) {
        final String fullName= listOfContacts.get(position).getFirstName() + " " + listOfContacts.get(position).getLastName();
        holder.name.setText(fullName);
        final String gaa=listOfContacts.get(position).getGenderAndAge();
        holder.genderAndAge.setText(gaa);

        //holder.imageIcon.setImageResource();
        if(listOfContacts.get(position).getOnlineStatus().contains("online")) {
            holder.onlineStatus.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.onlineStatus.setVisibility(View.GONE);
        }

        if(listOfContacts.get(position).getImageUrl()!=null && !listOfContacts.get(position).getImageUrl().isEmpty())
        {
            Picasso.get().load(listOfContacts.get(position).getImageUrl()).into(holder.imageIcon);
        }
        else
        {
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
        }
        holder.rowItself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), Integer.toString(position)+" position pressed", Toast.LENGTH_SHORT).show();

                //will go to chat screen

                Intent goToChatScreen= new Intent(context, com.example.i160237_i17405.ChatScreen.class);

                goToChatScreen.putExtra("CONTACT_PROFILE_TO_CHAT", listOfContacts.get(position));
                if (myProfile == null) {
                    Log.i("SENDER_CONTACT_HOME", "sender is null");
                }
                goToChatScreen.putExtra("MY_PROFILE_TO_CHAT", myProfile);

                context.startActivity(goToChatScreen);

            }
        });
    }


    @Override
    public int getItemCount() {
        if (listOfContacts != null) {
            return listOfContacts.size();
        }
        else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageIcon, onlineStatus;
        TextView name, genderAndAge;
        RelativeLayout rowItself;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.nameOfContactRowSearch);
            genderAndAge= itemView.findViewById(R.id.genderAndAgeRowSearch);
            onlineStatus= itemView.findViewById(R.id.onlineStatusRowSearch);
            imageIcon= itemView.findViewById(R.id.imageContactRowSearch);
            rowItself= itemView.findViewById(R.id.rowSearchItself);
        }
    }


    //for search filtering
    public void filterList(ArrayList<Contact> filteredList){
        listOfContacts= filteredList;
        notifyDataSetChanged();
    }

}
