package com.example.i160237_i17405;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Models.DatabaseHelperClass;
import Models.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String uid;//holds string value of firebase uid

    Button saveButton;
    EditText firstNameField;
    EditText lastNameField;
    EditText dateField;
    RadioGroup genderGroup;
    EditText phoneNoField;
    EditText bioField;
    ProgressBar progressBar;
    CircleImageView circularImageField;
    Uri imageUri;//for storing selected profile image- if any
    String downloadUrl;//for storing download url of uploaded image - to store in Contact class
    int GALLERY_PICK = 1;


    String email, password;
    String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //getting email pass from previous activity
        Intent intent = getIntent();
        email= intent.getStringExtra("email");
        password= intent.getStringExtra("password");
       //Toast.makeText(this, "email and password " + email + " " + password + " ", Toast.LENGTH_SHORT).show();

        //this is done so tool bar is attached to this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.createProfileToolBar);
        setSupportActionBar(toolbar);

        firstNameField = findViewById(R.id.firstNameFieldCreateProfile);
        lastNameField = findViewById(R.id.lastNameFieldCreateProfile);
        genderGroup = findViewById(R.id.genderGroupCreateProfile);
        phoneNoField = findViewById(R.id.phoneNoFieldCreateProfile);
        bioField = findViewById(R.id.bioFieldCreateProfile);
        progressBar= findViewById(R.id.progressBarCreateProfile);

        //edit text clicked and dialog for date picker shown
        dateField = findViewById(R.id.dateFieldCreateProfile);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogue();
            }
        });

        circularImageField = findViewById(R.id.circularImageCreateProfile);
        circularImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent, "Select Image"),GALLERY_PICK);
            }
        });

        saveButton= findViewById(R.id.saveButtonCreateProfile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String firstName = firstNameField.getText().toString();
                    String lastName = lastNameField.getText().toString();
                    String date = dateField.getText().toString();
                    String phoneNo = phoneNoField.getText().toString();
                    String bio = bioField.getText().toString();
                    int selectedId = genderGroup.getCheckedRadioButtonId();//getting id of selected radio button
                    RadioButton genderRadioButton = findViewById(selectedId);//searching for that particular id
                    String gender = genderRadioButton.getText().toString();

                    if(firstName.isEmpty()){
                        firstNameField.setError("Provide first name");
                    }
                    else if(lastName.isEmpty()){
                        lastNameField.setError("Provide last name");
                    }
                    else if(date.isEmpty()){
                        dateField.setError("Select date");
                    }
                    else if(phoneNo.isEmpty()){
                        phoneNoField.setError("Provide phone no");
                    }
                    else if(bio.isEmpty()){
                        bioField.setError("Provide bio");
                    }
                    else if(gender.isEmpty()){
//                        genderGroup.setError("Provide phone no");
                        genderRadioButton.setError("Select a gender");
                    }
                    else {
                        downloadUrl = "";
                        progressBar.setVisibility(View.VISIBLE);
                        hideKeyboard(CreateProfile.this);

                        //This function stores/inserts user's data into mysql.
                        insertUserToDB(firstName, lastName, phoneNo, date, gender, bio, imageUri);
                        //This function stores/inserts user's data into SQLite.
                        boolean res = insertUserToSQLiteDB( email.toString(), password.toString(), firstName, lastName, phoneNo, date, gender, bio, "YES");
                        Toast.makeText(getApplicationContext(), "SQLite entry" + res, Toast.LENGTH_LONG);
                        Intent goToHomeScreen = new Intent(CreateProfile.this, HomeScreen.class);

                        progressBar.setVisibility(View.GONE);
                        goToHomeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
            }
        });
    }

    private void insertUserToDB(String firstName, String lastName, String phoneNo, String date, String gender, String bio, Uri imageUri) {
        if (imageUri!=null)
        {
            //if image selected, we encode it
            try {
                //converting URI -> a lot of stuff -> base64 encoded STRING
                encodedImage= getEncodedImageFromUri(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        StringRequest userInsertionRequest= new StringRequest(Request.Method.POST, NetworkConfigurations.getUrlForInsertingUserDetails(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(CreateProfile.this, "Response: " + response + "\n ", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject responseJson = new JSONObject(response);
                    String myId= responseJson.getString("id");

                    //NA is recieved in case of failure
                    if(! myId.equals("NA"))
                    {
                        //go to home with myId and from there we can populate all users and my profile
                        Intent goToHome= new Intent(CreateProfile.this, HomeScreen.class);
                        goToHome.putExtra("MY_ID", myId);
                        //Toast.makeText(CreateProfile.this, "myId: " + myId, Toast.LENGTH_SHORT).show();
                        startActivity(goToHome);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateProfile.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
            }
        }){
            //overriding this method here
            //We use this method to send params to our script.php
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params= new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("DOB", date);
                params.put("gender", gender);
                params.put("phoneNo", phoneNo);
                params.put("bio", bio);

                //php has if else if on the set it receives, if we dont send display pic, php script will decide what to store
                if(encodedImage!=null) {
                    params.put("displayPic", encodedImage);
                }

                params.put("onlineStatus", "Online");
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(CreateProfile.this);
        requestQueue.add(userInsertionRequest); //basically calls this request
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


    //For Picking Date
    private void showDatePickerDialogue(){
        DatePickerDialog datePickerDialog= new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateString= dayOfMonth + "/" + (month+1) + "/" + year;
        dateField.setText(dateString);
        dateField.setError(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            assert data != null;
            imageUri = data.getData();//for storing in firebase
            circularImageField.setImageURI(imageUri);

        }
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

    public boolean insertUserToSQLiteDB(String userEmail, String userPassword, String firstName, String lastName, String phoneNo, String dateOfBirth, String gender, String bio, String onlineStatus)
    {

        try {
            UserModel UserSqLiteDBModel = new UserModel(-1, userEmail, userPassword, firstName, lastName, phoneNo, dateOfBirth, gender, bio/*, String imageUrl*/, onlineStatus);
            DatabaseHelperClass dbhelper =  new DatabaseHelperClass(CreateProfile.this);

            boolean res = dbhelper.AddUser(UserSqLiteDBModel);
            Log.d("printme", "Here in try block");
            return  res;
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error In creating a User table", Toast.LENGTH_SHORT).show();
            //UserSqLiteDBModel = new UserModel(-1, "null", "null", "null",
                    //"null", "null", "null", "null", "null", "null");
        }
        return false;
    }
}