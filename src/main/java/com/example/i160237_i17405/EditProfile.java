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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    EditText firstNameField, lastNameField, phoneNoField, dateField, bioField;
    RadioGroup genderGroup;
    CircleImageView imageField;
    Button saveButton;
    ProgressBar progressBar;

    Uri imageUri;//for storing selected profile image- if any
    String downloadUrl;//for storing download url of uploaded image - to store in Contact class

    int GALLERY_PICK = 2;
    Contact tempContact;
    String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setTitle("Edit profile");

        //myprofile retrieved
        tempContact = getIntent().getParcelableExtra("MY_PROFILE_TO_EDIT");

        //this is done so tool bar is attached to this activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.editProfileToolBar);
        setSupportActionBar(toolbar);

        firstNameField= findViewById(R.id.firstNameFieldEditProfile);
        lastNameField= findViewById(R.id.lastNameFieldEditProfile);
        phoneNoField= findViewById(R.id.phoneNoFieldEditProfile);
        bioField= findViewById(R.id.bioFieldEditProfile);
        imageField = findViewById(R.id.circularImageEditProfile);
        genderGroup = findViewById(R.id.genderGroupEditProfile);
        progressBar= findViewById(R.id.progressBarEditProfile);

        //edit text clicked and dialog for date picker shown
        dateField= findViewById(R.id.dateFieldEditProfile);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogue();
            }
        });


        //basically where we'll get this user data and populate by default in editTexts
        firstNameField.setText(tempContact.getFirstName());
        lastNameField.setText(tempContact.getLastName());
        phoneNoField.setText(tempContact.getPhoneNo());
        dateField.setText(tempContact.getDateOfBirth());
        bioField.setText(tempContact.getBio());
        if(!tempContact.getImageUrl().isEmpty())
        {
            Picasso.get().load(tempContact.getImageUrl()).into(imageField);
        }

        imageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryintent, "Select Image"),GALLERY_PICK);
            }
        });


        saveButton= findViewById(R.id.saveButtonEditProfile);
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

                if (firstName.isEmpty()) {
                    firstNameField.setError("Provide first name");
                } else if (lastName.isEmpty()) {
                    lastNameField.setError("Provide last name");
                } else if (date.isEmpty()) {
                    dateField.setError("Select date");
                } else if (phoneNo.isEmpty()) {
                    phoneNoField.setError("Provide phone no");
                } else if (bio.isEmpty()) {
                    bioField.setError("Provide bio");
                } else if (gender.isEmpty()) {
//                        genderGroup.setError("Provide phone no");
                    genderRadioButton.setError("Select a gender");
                } else {
                    downloadUrl = "";////initially is null - will stay null if no image is selected for profile

//                  will upload selected image to firebase storage and set download url in global string
//                    created this function to reduce clutter

                    hideKeyboard(EditProfile.this);
                    progressBar.setVisibility(View.VISIBLE);
                    updateProfile(firstName, lastName, phoneNo, date, gender, bio);

                    //finish();//uploadImageToStorageAndSetDownloadUrl(firstName, lastName, phoneNo, date, gender, bio);
                }
            }

        });
    }



    void updateProfile(String firstName, String lastName, String phoneNo, String date, String gender, String bio)
    {
        //new image selected definitely
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

        StringRequest userUpdationRequest= new StringRequest(Request.Method.POST,
                NetworkConfigurations.getUrlForUpdatingUserDetails(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(com.example.i160237_i17405.EditProfile.this, "Response: " + response + "\n ", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            String myId= responseJson.getString("id");
                            String responseMsg= responseJson.getString("msg");
                            //User Updated received in case of success
                            if(responseMsg.equals("User Updated"))
                            {
                                Toast.makeText(com.example.i160237_i17405.EditProfile.this, "Profile updated", Toast.LENGTH_LONG).show();
                                //go to home with myId and from there we can populate all users and my profile
                                Intent goToHome= new Intent(com.example.i160237_i17405.EditProfile.this, com.example.i160237_i17405.HomeScreen.class);
                                goToHome.putExtra("MY_ID", myId); //just sending my id to home while starting, home gets it every time
                                finish();
                                startActivity(goToHome);
                            }
                            else{
                                Toast.makeText(com.example.i160237_i17405.EditProfile.this, "Couldn't update profile", Toast.LENGTH_LONG).show();
                                finish();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(com.example.i160237_i17405.EditProfile.this, "Error Message: " + error.getMessage() + "\n ", Toast.LENGTH_LONG).show();
            }
        }){
            //overriding this method here
            //We use this method to send params to our script.php
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params= new HashMap<>();
                params.put("UID", tempContact.getUserId());
                params.put("email", tempContact.getUserEmail());
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("DOB", date);
                params.put("gender", gender);
                params.put("phoneNo", phoneNo);
                params.put("bio", bio);

                //php has if else if on the set it receives, if we dont send display pic, php script will decide what to store
                //in this case if we dont have an encoded image, means we didnt select a new dp
                //that means php will not take an update request of the displayPic column
                if(encodedImage!=null) {
                    params.put("displayPic", encodedImage);
                }
                params.put("onlineStatus", "online");
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(com.example.i160237_i17405.EditProfile.this);
        requestQueue.add(userUpdationRequest); //basically calls this request
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            assert data != null;
            imageUri = data.getData();//for storing in firebase
            imageField.setImageURI(imageUri);

        }
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
}