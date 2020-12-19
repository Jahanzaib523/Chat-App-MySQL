package com.example.i160237_i17405;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import Models.DatabaseHelperClass;

public class SignIn extends AppCompatActivity {

    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    AirplaneMode_Receiver airplaneModeReceiver = new AirplaneMode_Receiver();
    PowerConnected_Receiver powerConnectedReceiver = new PowerConnected_Receiver();
    BatteryLow_Receiver batteryLowReceiver = new BatteryLow_Receiver();
    PowerSavingMode_Receiver powerSavingModeReceiver = new PowerSavingMode_Receiver();
    IncomingCall_BroadCastReceiver incomingCallBroadCastReceiver = new IncomingCall_BroadCastReceiver();
    ActionBoot_Receiver actionBootReceiver = new ActionBoot_Receiver();
    Battery_Receiver battery_receiver = new Battery_Receiver();

    Button signInButton;
    LinearLayout registerHereText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton= findViewById(R.id.signInButton);
        registerHereText= findViewById(R.id.registerHereText);
        progressBar= findViewById(R.id.progressBarSignIn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = findViewById(R.id.emailFieldSignin);
                EditText passwordField = findViewById(R.id.passwordFieldSignin);

                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if(email.isEmpty()){
                    emailField.setError("Provide id");
                    emailField.requestFocus();
                }

                else if(password.isEmpty()){
                    passwordField.setError("Provide password");
                    passwordField.requestFocus();
                }

                else {

                    //closing keyboard
                    hideKeyboard(SignIn.this);
                    progressBar.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);

                    signInAuthentication(email,password);

                }

            }
        });

        registerHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister= new Intent(SignIn.this, Register.class);
                startActivity(goToRegister);
            }
        });

    }

    void hideKeyboard(Context c)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(c);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void signInAuthentication(String email, String password){
        if(haveNetworkConnection())
        {
            StringRequest signInRequest= new StringRequest(Request.Method.POST,
                    NetworkConfigurations.getUrlForGettingUserDetails(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                JSONObject responseJson = new JSONObject(response);
                                String status = responseJson.getString("response");
                                if(status.equals("not signed in")) {
                                    Toast.makeText(SignIn.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                                else if(status.equals("signed in")){
                                    Toast.makeText(SignIn.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                                    JSONArray dataJson = responseJson.getJSONArray("data");
                                    int dataSize = dataJson.length();
                                    ArrayList<String> dataString = new ArrayList<String>();
                                    for(int i =0;i<dataSize;i++){
                                        JSONObject obj = dataJson.getJSONObject(i);
                                        dataString.add(obj.getString("UID"));
                                        dataString.add(obj.getString("email"));
                                        dataString.add(obj.getString("password"));
                                    }

                                    Intent goToHome= new Intent(SignIn.this, HomeScreen.class);
                                    goToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(goToHome);
                                    goToHome.putExtra("MY_ID", dataString.get(0));//index corresponds to user id
                                    startActivity(goToHome);
                                }
                                else if(status.equals("error0")){
                                    Toast.makeText(SignIn.this, "Query error",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(SignIn.this, "Invalid db request",
                                            Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SignIn.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
                    Log.d("SIGNIN_ERROR",error.getMessage());
                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params= new HashMap<>();
                    params.put("email", email);
                    params.put("password",password);
                    return params;
                }
            };

            RequestQueue requestQueue= Volley.newRequestQueue(SignIn.this);
            requestQueue.add(signInRequest); //basically calls this request
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            /*boolean val = AuthenticateUsingSQLiteDB(email, password);
            if(val == true) {
                Intent goToHome= new Intent(SignIn.this, HomeScreen.class);
                //goToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToHome);
                Log.d("Pass", "Failutre");
                //finish();
            }
            else
            {
                Log.d("Fail", "Failutre");
                Toast.makeText(getApplicationContext(), "No such user", Toast.LENGTH_LONG).show();
            }*/
        }
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

    private boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean AuthenticateUsingSQLiteDB(String email, String password)
    {
        DatabaseHelperClass db = new DatabaseHelperClass(SignIn.this);
        Cursor c = db.SignInUsingSQLiteDB(email, password);

        if(c!=null)
        {
            Log.i("cursortrue1", c.getString(1));
            Log.i("cursortrue2", c.getString(2));
            return true;
        }
        else
        {
            Log.i("cursorfalse1", c.getString(1));
            Log.i("cursorfalse2", c.getString(2));
            return false;
        }
    }
}