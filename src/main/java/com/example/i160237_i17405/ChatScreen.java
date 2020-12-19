package com.example.i160237_i17405;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import Models.DatabaseHelperClass;
import Models.MessageModel;
import Models.MessagesDatabaseHelperClass;
import Models.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen extends AppCompatActivity {
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    AirplaneMode_Receiver airplaneModeReceiver = new AirplaneMode_Receiver();
    PowerConnected_Receiver powerConnectedReceiver = new PowerConnected_Receiver();
    BatteryLow_Receiver batteryLowReceiver = new BatteryLow_Receiver();
    PowerSavingMode_Receiver powerSavingModeReceiver = new PowerSavingMode_Receiver();
    IncomingCall_BroadCastReceiver incomingCallBroadCastReceiver = new IncomingCall_BroadCastReceiver();
    ActionBoot_Receiver actionBootReceiver = new ActionBoot_Receiver();
    Battery_Receiver battery_receiver = new Battery_Receiver();

    ImageView sendButton, cameraButton, backButton, selectedImage;
    TextView nameOfContact, onlineStatus;
    CircleImageView contactImage;
    EditText messageBody;

    RecyclerView recyclerView;
    ChatRvAdapter rvAdapter;

    ArrayList<com.example.i160237_i17405.Message> mChat;

    Uri selectedImageUri;//for storing selected profile image- if any
    String encodedImage;// base64 string

    Contact myProfile;//contact of user that is logged in currently
    Contact contactProfile;//contact of user with whom this conversation is

    //ArrayList<String> keysMessagesInFirebase;// keys stored each time listOfContacts populated

    int GALLERY_PICK = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        //Gets passed Contact Object in Intent
        Intent intent= getIntent();

        myProfile= intent.getParcelableExtra("MY_PROFILE_TO_CHAT");
        contactProfile= intent.getParcelableExtra("CONTACT_PROFILE_TO_CHAT");

        System.out.println("MY PROFILE: " + myProfile.getUserId()+ "Contact profile:  " + contactProfile.getUserId());


        mChat= new ArrayList<>();

        //Recycler View stuff
        recyclerView= findViewById(R.id.chatRecyclerView);


        contactImage= findViewById(R.id.contactImageIconChatScreen);
        messageBody= findViewById(R.id.messageEditText);
        sendButton= findViewById(R.id.sendMessageButton);
        cameraButton= findViewById(R.id.imageButtonChatScreen);
        backButton=findViewById(R.id.backButtonChatScreen);
        selectedImage= findViewById(R.id.selectedImage);

        nameOfContact= findViewById(R.id.contactNameToolBarChat);
        onlineStatus= findViewById(R.id.contactOnlineStatusToolBarChat);
        nameOfContact.setText(contactProfile.getFullName());
        onlineStatus.setText(contactProfile.getOnlineStatus());
        if(! contactProfile.getImageUrl().isEmpty())
        {
            Picasso.get().load(contactProfile.getImageUrl()).into(contactImage);
        }

        buildRecyclerView();

        populateChatMessages(myProfile.getUserId(), contactProfile.getUserId());



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=messageBody.getText().toString().trim();
                if(text.isEmpty())
                {
                    Toast.makeText(com.example.i160237_i17405.ChatScreen.this, "Message body cant be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    String senderId= myProfile.getUserId();
                    String receiverId= contactProfile.getUserId();
                    String timestamp= getCurrentTime();//Calendar.getInstance().getTime().toString();

                    sendNewMessage(senderId, receiverId, text, timestamp, "unseen", selectedImageUri);
                }
            }
        });


        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(com.example.i160237_i17405.ChatScreen.this, "contact image pressed", Toast.LENGTH_SHORT).show();
                ContactBottomSheetDialog contactBottomSheetDialog= new ContactBottomSheetDialog(contactProfile);
                contactBottomSheetDialog.show(getSupportFragmentManager(), "bottomSheetContact");
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ChatScreen.this, "camera button pressed", Toast.LENGTH_SHORT).show();
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryintent, "Select Image"),GALLERY_PICK);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }




    private void populateChatMessages(String senderIdFromActivity, String receiverIdFromActivity) {

        StringRequest getMessages= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForGettingMessages(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ChatScreen.this, "Response: " + response + "\n ", Toast.LENGTH_SHORT).show();
                        //This is where we get the response
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("response").equals("got all messages")) {
                                //this is where we know that there is a json array inside that has all the messages

                                JSONArray allMessagesJsonArray = jsonObject.getJSONArray("data");

                                String msgId, senderId, receiverId, text, timestamp, status, imageUrl;
                                for(int i=0;i<allMessagesJsonArray.length();i++) {
                                    JSONObject messageObject = allMessagesJsonArray.getJSONObject(i); //single msg


                                    msgId = messageObject.getString("MsgId");
                                    senderId=messageObject.getString("senderId");
                                    receiverId= messageObject.getString("receiverId");
                                    text=messageObject.getString("text");
                                    timestamp= messageObject.getString("timestamp");
                                    status=messageObject.getString("status");
                                    imageUrl= messageObject.getString("image");

                                    if(!imageUrl.isEmpty())
                                    {
                                        //not empty, we'll assign it the proper path
                                        imageUrl= NetworkConfigurations.getRootPath()+imageUrl;
                                    }

                                    //this is where we are populating complete chat
                                    mChat.add(new Message(msgId, senderId, receiverId, text, timestamp, status, imageUrl));

                                    //if other contact sent the message we gonna call set seen status
                                    if (! senderId.equals(myProfile.getUserId())) {
                                        makeMessageStatusSeen(msgId);
                                    }

                                    rvAdapter.notifyDataSetChanged();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //===========

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatScreen.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("senderId", senderIdFromActivity);
                params.put("receiverId", receiverIdFromActivity);
                return params;

            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(ChatScreen.this);
        requestQueue.add(getMessages); //basically calls this request


    }

    private void makeMessageStatusSeen(String msgId) {

        StringRequest updateSeenStatus= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForUpdatingMessages(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ChatScreen.this, "Response: " + response + "\n ", Toast.LENGTH_SHORT).show();
                        //This is where we get the response
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("msg").equals("Msg Updated")) {
                                //Toast.makeText(ChatScreen.this, "Seen Status Set", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatScreen.this, "Seen Status Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("MsgId", msgId);
                params.put("status", "seen");
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(ChatScreen.this);
        requestQueue.add(updateSeenStatus); //basically calls this request



    }

    private void sendNewMessage(String senderId, String receiverId, String text, String timestamp, String seenStatus, Uri imageUri) {
        if (imageUri!=null)
        {
            //if image selected, we encode it
            try {
                encodedImage= getEncodedImageFromUri(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        StringRequest messageInsertionRequest= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForInsertingMessage(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ChatScreen.this, "Response: " + response + "\n ", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String messageId= responseJson.getString("id");
                            if(messageId.equals("NA"))
                            {
                                Toast.makeText(ChatScreen.this, "Could not send message", Toast.LENGTH_SHORT).show();
                                Toast.makeText(ChatScreen.this, responseJson.getString("msg"), Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                //message added
                                Toast.makeText(ChatScreen.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                //image was sent
                                if(selectedImageUri!=null)
                                {
                                    String urlFromResponse= responseJson.getString("msg"); //we will get a path of image in msg only if we sent an image along
                                    mChat.add(new Message(messageId, senderId, receiverId, text, timestamp, "unseen", NetworkConfigurations.getRootPath()+urlFromResponse));
                                    boolean res = insertMsgToSQLiteDB(messageId, senderId, receiverId, text, timestamp, "seen");

                                    selectedImage.setVisibility(View.VISIBLE);
                                }
                                //image not sent
                                else{
                                    mChat.add(new Message(messageId, senderId, receiverId, text, timestamp, "unseen", ""));
                                    boolean res = insertMsgToSQLiteDB(messageId, senderId, receiverId, text, timestamp, "seen");
                                }

                                //add to list and notify adapter
                                rvAdapter.notifyItemInserted(mChat.size()-1);
                                recyclerView.scrollToPosition(mChat.size() - 1);

                                //on success of sending message we clear stuff in ui
                                messageBody.setText("");
                                selectedImageUri= null;
                                selectedImage.setImageURI(null);
                                selectedImage.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChatScreen.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params= new HashMap<>();
                params.put("senderId", senderId);
                params.put("receiverId", receiverId);
                params.put("text", text);
                params.put("timestamp", timestamp);
                params.put("status", seenStatus);
                //php has if else if on the set it receives, if we dont send display pic, php script will decide what to store
                if(encodedImage!=null) {
                    params.put("image", encodedImage);
                }
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(ChatScreen.this);
        requestQueue.add(messageInsertionRequest); //basically calls this request


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            assert data != null;
            selectedImageUri = data.getData();//for storing in firebase

            selectedImage.setVisibility(View.VISIBLE);
            selectedImage.setImageURI(selectedImageUri);
        }
    }

    private String getEncodedImageFromUri(Uri imageUri) throws FileNotFoundException {

        String enImg;

        //URI -> InputStream -> Bitmap
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);

        //Bitmap --compressed into--> ByteArrayOutputSteam -> byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] imageByteArray = stream.toByteArray();

        //byte array -> base 64 string
        enImg = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        return enImg;
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance(getResources().getConfiguration().locale);
        long timeStamp= cal.getTimeInMillis();
        cal.setTimeInMillis(timeStamp);
        String dateString = DateFormat.format("h:mm a", cal).toString();
        return dateString;
    }

    public String convertTimestamp(String timestamp){
        Calendar cal = Calendar.getInstance(getResources().getConfiguration().locale);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateString = DateFormat.format("h:mm a", cal).toString();
        return dateString;
    }

    void buildRecyclerView(){
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //adapter for recycler view
        rvAdapter = new ChatRvAdapter(mChat, ChatScreen.this);
        rvAdapter.setMyProfille(myProfile); //sending rv myProfile so it knows which side bubble to inflate
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

    public boolean insertMsgToSQLiteDB(String messageId, String senderId, String receiverId, String text, String timestamp, String onlineStatus)
    {

        try {
            MessageModel MessagesSqLiteDBModel = new MessageModel(messageId, senderId, receiverId, text, timestamp, onlineStatus);
            MessagesDatabaseHelperClass dbhelper =  new MessagesDatabaseHelperClass(ChatScreen.this);

            boolean res = dbhelper.AddMessage(MessagesSqLiteDBModel);
            Log.d("printme", "message sent to chat screen");
            return  res;
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error In creating a User table", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}