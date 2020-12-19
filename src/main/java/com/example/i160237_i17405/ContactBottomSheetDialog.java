package com.example.i160237_i17405;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactBottomSheetDialog extends BottomSheetDialogFragment {

    Contact contact;
    TextView name, phoneNo, genderAndAge, bio;
    CircleImageView imageIcon;

    ContactBottomSheetDialog(Contact contact)
    {
        this.contact= contact;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_contact, container, false);

        name= view.findViewById(R.id.contactNameBottomSheetContact);
        phoneNo= view.findViewById(R.id.phoneNoBottomSheetContact);
        genderAndAge= view.findViewById(R.id.genderAndAgeBottomSheetContact);
        bio= view.findViewById(R.id.bioBottomSheetContact);
        imageIcon= view.findViewById(R.id.contactImageBottomSheet);

        //setting data on bottom sheet
        name.setText(contact.getFullName());
        phoneNo.setText(contact.getPhoneNo());
        genderAndAge.setText(contact.getGenderAndAge());
        bio.setText(contact.getBio());

        if(! contact.getImageUrl().isEmpty())
        {
            Picasso.get().load(contact.getImageUrl()).into(imageIcon);
        }

        return view;
    }
}
