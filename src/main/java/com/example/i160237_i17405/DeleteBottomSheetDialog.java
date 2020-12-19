package com.example.i160237_i17405;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DeleteBottomSheetDialog extends BottomSheetDialogFragment {

    private com.example.i160237_i17405.DeleteBottomSheetDialog.BottomSheetListener bottomSheetListener;

    int position;
    DeleteBottomSheetDialog(int position){
        this.position=position;
    }

    LinearLayout deleteButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_delete_dialogue, container,false);
        deleteButton= view.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onDeleteClicked(position);
                dismiss(); //dismiss the dialogue
            }
        });


        return view;
    }



    public interface BottomSheetListener{
        //this we will communicate to main activity
        void onDeleteClicked(int pos);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //context attaches the dialogue with the home activity

        //exception here so we remember to implement the interface above in activity. Just a good practice
        try {
            bottomSheetListener = (DeleteBottomSheetDialog.BottomSheetListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    +"Space must implement bottom sheet listener");
        }

    }
}
