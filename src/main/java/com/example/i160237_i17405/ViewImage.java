package com.example.i160237_i17405;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewImage extends AppCompatActivity {

    CircleImageView backButton;
    ImageView mainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent=getIntent();
        String downloadUrl= intent.getStringExtra("downloadUrl");
        mainImage=findViewById(R.id.imageFullScreen);
        Picasso.get().load(downloadUrl).into(mainImage);


        backButton= findViewById(R.id.backButtonViewImage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}