package com.example.i160237_i17405;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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

import org.json.JSONException;
import org.json.JSONObject;

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

public class Register extends AppCompatActivity {
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    AirplaneMode_Receiver airplaneModeReceiver = new AirplaneMode_Receiver();
    PowerConnected_Receiver powerConnectedReceiver = new PowerConnected_Receiver();
    BatteryLow_Receiver batteryLowReceiver = new BatteryLow_Receiver();
    PowerSavingMode_Receiver powerSavingModeReceiver = new PowerSavingMode_Receiver();
    IncomingCall_BroadCastReceiver incomingCallBroadCastReceiver = new IncomingCall_BroadCastReceiver();
    ActionBoot_Receiver actionBootReceiver = new ActionBoot_Receiver();
    Battery_Receiver battery_receiver = new Battery_Receiver();

    String email, password;
    LinearLayout signInHereText;
    Button registerButton;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton= findViewById(R.id.registerButton);
        signInHereText=findViewById(R.id.signInHereText);
        progressBar=findViewById(R.id.progressBarRegister);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = findViewById(R.id.emailFieldRegister);
                EditText passwordField = findViewById(R.id.passwordFieldRegister);
                EditText confirmPasswordField = findViewById(R.id.passwordConfirmFieldRegister);

                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString().trim();
                String confirmPassword = confirmPasswordField.getText().toString().trim();

//                checks for ui fields
                if(email.isEmpty()){
                    emailField.setError("Provide email id");
                    emailField.requestFocus();
                }

                else if(password.isEmpty()){
                    passwordField.setError("Provide password");
                    passwordField.requestFocus();
                }
                else if(confirmPassword.isEmpty()){
                    confirmPasswordField.setError("Provide password");
                    confirmPasswordField.requestFocus();
                }
//                 if ui fields acceptable
                else {
//                will only try to sign in if passwords same
                    if (password.equals(confirmPassword)) {
                        hideKeyboard(Register.this);
                        progressBar.setVisibility(View.VISIBLE);

                        //TODO --- done
                        emailAuthentication(email,password);

                    } else {
                        confirmPasswordField.setError("Password does not match");
                        confirmPasswordField.requestFocus();
                        Toast.makeText(Register.this, "passwords do not match! Try again"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signInHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignIn= new Intent(Register.this, SignIn.class);
                startActivity(goToSignIn);
            }
        });

    }
    protected void createAccount(String email, String password, Context c){

        Intent goToCreateProfile= new Intent(Register.this, CreateProfile.class);

        goToCreateProfile.putExtra("Email", email);
        goToCreateProfile.putExtra("Password", password);

        progressBar.setVisibility(View.GONE);
        goToCreateProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToCreateProfile);
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
    public void emailAuthentication(String email, String password){
        StringRequest emailAuthenticationRequest= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForGettingUserDetails(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            JSONObject responseJson = new JSONObject(response);
                            String emailStatus = responseJson.getString("response");
                            if(emailStatus.equals("email already exists")) {
                                Toast.makeText(Register.this, "Authentication Failed, Email Already Exists",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else if(emailStatus.equals("email does not exist")){
                                Intent goToCreateProfile= new Intent(Register.this, CreateProfile.class);
                                goToCreateProfile.putExtra("email", email);//index corresponds to email
                                goToCreateProfile.putExtra("password", password);//index corresponds to password
                                startActivity(goToCreateProfile);
                            }
                            else if(emailStatus.equals("error1")){
                                Toast.makeText(Register.this, "Query error",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Register.this, "Invalid db request",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Register.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
                Log.d("REGISTRATION_ERROR",error.getMessage());
            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params= new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(Register.this);
        requestQueue.add(emailAuthenticationRequest);
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
