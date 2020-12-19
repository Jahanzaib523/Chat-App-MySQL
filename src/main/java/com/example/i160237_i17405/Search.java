package com.example.i160237_i17405;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import BroadCastReceivers.ActionBoot_Receiver;
import BroadCastReceivers.AirplaneMode_Receiver;
import BroadCastReceivers.BatteryLow_Receiver;
import BroadCastReceivers.Battery_Receiver;
import BroadCastReceivers.ConnectivityReceiver;
import BroadCastReceivers.IncomingCall_BroadCastReceiver;
import BroadCastReceivers.PowerConnected_Receiver;
import BroadCastReceivers.PowerSavingMode_Receiver;

public class Search extends AppCompatActivity {

    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    AirplaneMode_Receiver airplaneModeReceiver = new AirplaneMode_Receiver();
    PowerConnected_Receiver powerConnectedReceiver = new PowerConnected_Receiver();
    BatteryLow_Receiver batteryLowReceiver = new BatteryLow_Receiver();
    PowerSavingMode_Receiver powerSavingModeReceiver = new PowerSavingMode_Receiver();
    IncomingCall_BroadCastReceiver incomingCallBroadCastReceiver = new IncomingCall_BroadCastReceiver();
    ActionBoot_Receiver actionBootReceiver = new ActionBoot_Receiver();
    Battery_Receiver battery_receiver = new Battery_Receiver();

    ArrayList<Contact> listOfContacts;

    Contact myProfile;

    ImageView backButton;
    EditText searchBar;

    RecyclerView recyclerView;
    SearchRvAdapter rvAdapter;

    int READ_CONTACT_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Gets passed Contact Object in Intent
        Intent intent= getIntent();

        //set up list of contacts that will be shown
        listOfContacts = new ArrayList<Contact>();
        listOfContacts = intent.getParcelableArrayListExtra("contacts");
        myProfile= intent.getParcelableExtra("MY_PROFILE_TO_SEARCH");

        //back button
        backButton= findViewById(R.id.backButtonSearch);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //recycler view + layoutmanager + adapter
        buildRecyclerView();


        //searching functionality
        searchBar= findViewById(R.id.seachBarSearch);
        searchBar.requestFocus(); //opens keyboard by default
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //this is where we wanna filter our list
            //'s' is the content of our EditText
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    //method called every time text changed
    //text is the text inside our edit text
    private void filter(String text) {
        ArrayList<Contact> filteredList= new ArrayList<>();

        //for each contact in our list of contacts
        for(Contact contact : listOfContacts) {
            if(contact.getFullName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(contact);
            }
        }

        //passing our newly created filtered list to adapter
        rvAdapter.filterList(filteredList);
    }

    //makes recycler
    private void buildRecyclerView(){
        //Search Recycler View
        recyclerView= findViewById(R.id.recyclerViewSearch);

        //layout manager for Recycler view
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //adapter for recycler view
        rvAdapter= new SearchRvAdapter(listOfContacts, this);
        rvAdapter.setMyProfile(myProfile);
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