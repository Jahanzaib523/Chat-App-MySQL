package com.example.i160237_i17405;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    EditText ipAddressField;
    Button goButton;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //First thing to do would be get the IP of the localhost server
        ipAddressField= findViewById(R.id.ipAddressField);
        goButton= findViewById(R.id.goButton);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddress= ipAddressField.getText().toString().trim();
                if(ipAddress.isEmpty())
                {
                    Toast.makeText(com.example.i160237_i17405.SplashScreen.this, "Please insert IP Address of localhost server", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //setting ipaddress in NetworkConfig class where it is a static String
                    com.example.i160237_i17405.NetworkConfigurations.setIpAddress(ipAddress);

                    //take decision where to go, then send IP address as extra ALWAYS
                    Intent goToMain= new Intent(com.example.i160237_i17405.SplashScreen.this, com.example.i160237_i17405.MainActivity.class);

                    //goToMain.putExtra("IP_ADDRESS", ipAddress);
                    //finish();

                    //Toast.makeText(SplashScreen.this, "ipAddress in netconfig: " + NetworkConfigurations.getUrlForInsertingUserDetails() +"_", Toast.LENGTH_SHORT).show();
                    startActivity(goToMain);
                }
            }
        });


    }
}