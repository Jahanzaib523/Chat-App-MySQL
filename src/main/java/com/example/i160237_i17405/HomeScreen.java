package com.example.i160237_i17405;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import BroadCastReceivers.ActionBoot_Receiver;
import BroadCastReceivers.AirplaneMode_Receiver;
import BroadCastReceivers.BatteryLow_Receiver;
import BroadCastReceivers.Battery_Receiver;
import BroadCastReceivers.ConnectivityReceiver;
import BroadCastReceivers.IncomingCall_BroadCastReceiver;
import BroadCastReceivers.PowerConnected_Receiver;
import BroadCastReceivers.PowerSavingMode_Receiver;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreen extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener, MyProfileBottomSheetDialog.BottomSheetListener, com.example.i160237_i17405.DeleteBottomSheetDialog.BottomSheetListener {

    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    AirplaneMode_Receiver airplaneModeReceiver = new AirplaneMode_Receiver();
    PowerConnected_Receiver powerConnectedReceiver = new PowerConnected_Receiver();
    BatteryLow_Receiver batteryLowReceiver = new BatteryLow_Receiver();
    PowerSavingMode_Receiver powerSavingModeReceiver = new PowerSavingMode_Receiver();
    IncomingCall_BroadCastReceiver incomingCallBroadCastReceiver = new IncomingCall_BroadCastReceiver();
    ActionBoot_Receiver actionBootReceiver = new ActionBoot_Receiver();
    Battery_Receiver battery_receiver = new Battery_Receiver();

    private AppBarConfiguration mAppBarConfiguration;

    Contact myProfile; //basically a model class to store this user
    String uid;

    Toolbar toolbar; //top tool bar
    ImageView menuButton; //button on toolbar
    DrawerLayout drawerLayout; //drawer layout that surrounds the whole screen
    NavigationView navigationView; //this is used for selection and navigation

    RelativeLayout searchBarLayout; //this is the search bar itself
    CircleImageView searchBarProfileIcon; //the icon of this user that when clicked should show personal info

    ProgressBar progressBar;

    RecyclerView recyclerView;
    HomeRvAdapter rvAdapter;

    ArrayList<Contact> listOfContacts;//contacts that are displayed in app
    String myId;

    int totalContacts;
    int totalLastMessagesGotten;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        myId= getIntent().getStringExtra("MY_ID");

        //Toast.makeText(this, "My ID received in Home: " + myId + " ", Toast.LENGTH_SHORT).show();
        progressBar= findViewById(R.id.progressBarHome);

        listOfContacts=new ArrayList<>();
        totalContacts=0;
        totalLastMessagesGotten=0;

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //for menu selected
        navigationView= findViewById(R.id.nav_view_home);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout= findViewById(R.id.drawer_layout);



        //setTemplateData();
        buildRecyclerView();

        populateMyProfileAndListOfContacts();

        //search bar that takes to search activity
        searchBarLayout= findViewById(R.id.searchBarHome);
        searchBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HomeScreen.this, "Search bar pressed", Toast.LENGTH_LONG).show();
                Intent goToSearch = new Intent(HomeScreen.this, Search.class);

                ArrayList<Contact> arr= new ArrayList<>(listOfContacts);
                goToSearch.putParcelableArrayListExtra("contacts", arr);
                goToSearch.putExtra("MY_PROFILE_TO_SEARCH", myProfile);
                startActivity(goToSearch);
            }
        });

        //for drawer layout
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_logout)
                .setDrawerLayout(drawerLayout)
                .build();


        menuButton=findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //image icon on search bar
        searchBarProfileIcon= findViewById(R.id.searchBarProfileIcon);
        searchBarProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreen.this, "Profile Icon pressed", Toast.LENGTH_LONG).show();
                //Bottom sheet stuff... This is where we probably will send myProfile to populate stuff
                MyProfileBottomSheetDialog myProfileBottomSheetDialog= new MyProfileBottomSheetDialog(myProfile);
                myProfileBottomSheetDialog.show(getSupportFragmentManager(), "myProfileBottomSheet");
            }
        });


    }

    private void populateMyProfileAndListOfContacts() {
        progressBar.setVisibility(View.VISIBLE);

        StringRequest getUserDetailsRequest= new StringRequest(Request.Method.POST, NetworkConfigurations.getUrlForGettingUserDetails(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("response").equals("got all users")) {
                                //this is where we know that there is a json array inside that has all the users


                                JSONArray allUsersJsonArray = jsonObject.getJSONArray("data");

                                String userId, userEmail, userPassword, firstName, lastName, phoneNo, dateOfBirth,gender,bio,  imageUrl, onlineStatus;
                                for(int i=0;i<allUsersJsonArray.length();i++) {
                                    JSONObject userObject = allUsersJsonArray.getJSONObject(i); //single user



                                    userId= userObject.getString("UID");
                                    userEmail=userObject.getString("email");
                                    userPassword= userObject.getString("password");
                                    firstName=userObject.getString("firstName");
                                    lastName= userObject.getString("lastName");
                                    dateOfBirth=userObject.getString("DOB");
                                    gender= userObject.getString("gender");
                                    phoneNo=userObject.getString("phoneNo");
                                    bio=userObject.getString("bio");
                                    imageUrl= userObject.getString("displayPic");
                                    if(!imageUrl.isEmpty())
                                    {
                                        //not empty, we'll assign it the proper path
                                        imageUrl= NetworkConfigurations.getRootPath()+imageUrl;
                                    }

                                    onlineStatus= userObject.getString("onlineStatus");

                                    if(userId.equals(myId)){
                                        myProfile= new Contact(userId, userEmail, userPassword, firstName, lastName, phoneNo, dateOfBirth, gender, bio, imageUrl, onlineStatus);
                                        setNavHeader(myProfile);
                                        rvAdapter.setMyProfile(myProfile);

                                        //image of my profile on search bar being set after retrieval
                                        if(!myProfile.getImageUrl().isEmpty()){
                                            Picasso.get().load(myProfile.getImageUrl()).into(searchBarProfileIcon);
                                        }

                                        //setting online status to online ONLINE ONLINE ONLINE
                                        setOnlineStatus(userId, "online");
                                    }

                                    //LIST OF CONTACTS IS GETTING POPULATED IN HERE
                                    else{
                                        listOfContacts.add(new Contact(userId, userEmail, userPassword, firstName, lastName, phoneNo, dateOfBirth, gender, bio, imageUrl, onlineStatus));
                                        rvAdapter.notifyDataSetChanged();
                                        //gonna get last message for conversation with this userId contact and send it to rvAdapter
                                    }


                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeScreen.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }); //no need for getParams


        RequestQueue requestQueue= Volley.newRequestQueue(HomeScreen.this);
        requestQueue.add(getUserDetailsRequest); //basically calls this request

    }

    private void setOnlineStatus(String userId, String onlineStatus) {

        StringRequest updateOnlineStatus= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForUpdatingUserDetails(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("msg").equals("Online Status Updated")) {

                                if(onlineStatus.equals("online")) {
                                    //Toast.makeText(HomeScreen.this, onlineStatus + " ", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(com.example.i160237_i17405.HomeScreen.this, "Logging out", Toast.LENGTH_SHORT).show();
                                    Intent goToMain = new Intent(HomeScreen.this, com.example.i160237_i17405.MainActivity.class);
                                    finish();
                                    startActivity(goToMain);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(com.example.i160237_i17405.HomeScreen.this, "Online Status Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("UID", userId);
                params.put("onlineStatus", onlineStatus);
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(HomeScreen.this);
        requestQueue.add(updateOnlineStatus); //basically calls this request
    }

    private void logout() {
        setOnlineStatus(myProfile.getUserId(), "offline");
    }

    //LOGOUT
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                //THIS IS WHERE LOGOUT IS PRESSED
//              logging out here

                logout();
                //Toast.makeText(this, "Logout pressed", Toast.LENGTH_SHORT).show();

                return true;

            default:
                return true;
        }
    }



    //edit profile button is pressed
    @Override
    public void onButtonClicked(int buttonPressedCode) {
        if(buttonPressedCode==1) {
            Toast.makeText(this, "Edit Profile Pressed", Toast.LENGTH_SHORT).show();
            Intent goToEditProfile= new Intent(HomeScreen.this, EditProfile.class);

            goToEditProfile.putExtra("MY_PROFILE_TO_EDIT", myProfile); //sending to edit page
            startActivity(goToEditProfile);
        }
    }

    //where we delete
    @Override
    public void onDeleteClicked(int pos) {

        Toast.makeText(this, "Deleting " + Integer.toString(pos) + " ", Toast.LENGTH_SHORT).show();

        listOfContacts.remove(pos);
        if(pos==0){
            //no animation but solid
            rvAdapter.notifyDataSetChanged();
        }
        else {
            //does animation but crashes on 0 index
            rvAdapter.notifyItemRangeChanged(pos, listOfContacts.size());
        }

    }

    private void setNavHeader(Contact profile)
    {
        View headerView= navigationView.getHeaderView(0);
        TextView navHeaderName = (TextView) headerView.findViewById(R.id.headerName);
        TextView navHeaderEmail = (TextView) headerView.findViewById(R.id.headerEmail);
        CircleImageView navHeaderImage = (CircleImageView) headerView.findViewById(R.id.headerImage);

        navHeaderName.setText(myProfile.getFullName());
        //lol didnt save email in myProfile lol
        navHeaderEmail.setText(myProfile.getUserEmail());
        if (!myProfile.getImageUrl().isEmpty()) {
            Picasso.get().load(profile.getImageUrl()).into(navHeaderImage);//to set image from firebase storage with picasso
        }
    }

    //building recylcer
    void buildRecyclerView(){
        //Recycler view for contacts
        recyclerView = findViewById(R.id.recyclerViewHomeScreen);

        //layout manager for Recycler view
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rvAdapter= new HomeRvAdapter(listOfContacts, this);

        recyclerView.setAdapter(rvAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
        unregisterReceiver(airplaneModeReceiver);
        unregisterReceiver(powerConnectedReceiver);
        unregisterReceiver(batteryLowReceiver);
        unregisterReceiver(powerSavingModeReceiver);
        unregisterReceiver(incomingCallBroadCastReceiver);
        unregisterReceiver(actionBootReceiver);
        unregisterReceiver(battery_receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(connectivityReceiver, intentFilter);
        registerReceiver(airplaneModeReceiver, intentFilter);
        registerReceiver(powerConnectedReceiver, intentFilter);
        registerReceiver(batteryLowReceiver, intentFilter);
        registerReceiver(powerSavingModeReceiver, intentFilter);
        registerReceiver(incomingCallBroadCastReceiver, intentFilter);
        registerReceiver(actionBootReceiver, intentFilter);
        registerReceiver(battery_receiver, intentFilter);
    }
}