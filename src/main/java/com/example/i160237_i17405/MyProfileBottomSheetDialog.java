package com.example.i160237_i17405;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener bottomSheetListener;

    Contact myProfile;
    TextView name, phoneNo, genderAndAge, bio;
    CircleImageView image;

    MyProfileBottomSheetDialog(Contact myProfile){
        this.myProfile= myProfile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_my_profile, container, false);

        //setting textview values
        name= view.findViewById(R.id.myNameBottomSheetMyProfile);
        genderAndAge= view.findViewById(R.id.genderAndAgeBottomSheetMyProfile);
        bio= view.findViewById(R.id.bioBottomSheetMyProfile);
        phoneNo= view.findViewById(R.id.phoneNoBottomSheetMyProfile);
        image = view.findViewById(R.id.imageBottomSheetMyProfile);

        name.setText(myProfile.getFullName());
        genderAndAge.setText(myProfile.getGenderAndAge());
        bio.setText(myProfile.getBio());
        phoneNo.setText(myProfile.getPhoneNo());
        if (!myProfile.getImageUrl().isEmpty()) {
            Picasso.get().load(myProfile.getImageUrl()).into(image);//to set image from firebase storage with picasso
        }
        //basically in bottom sheet, if this button is clicked, it will communicate with the activity that this button is clicked
        ImageButton editProfileButton= view.findViewById(R.id.editProfileButtonBottomSheet);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked(1);
                dismiss(); //dismiss the dialogue
            }
        });


        return view;
    }

    public interface BottomSheetListener{

        //this we will communicate to main activity
        void onButtonClicked(int buttonPressedCode);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //context attaches the dialogue with the home activity

        //exception here so we remember to implement the interface above in activity. Just a good practice
        try {
            bottomSheetListener = (BottomSheetListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    +"Space must implement bottom sheet listener");
        }

    }
}
