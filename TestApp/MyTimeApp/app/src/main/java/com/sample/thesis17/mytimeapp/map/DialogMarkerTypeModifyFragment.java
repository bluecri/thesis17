package com.sample.thesis17.mytimeapp.map;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class DialogMarkerTypeModifyFragment extends DialogFragment {

    public interface DialogMarkerTypeModifyFragmentListener{
        public void modifyMarkerTypeData(MarkerTypeData mtd);
    }

    DialogMarkerTypeModifyFragmentListener dialogMarkerTypeModifyFragmentListener = null;

    TextView textViewNewMarkerTypeTitle = null;
    TextView textViewNewMarkerTypeMemo = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dialogMarkerTypeModifyFragmentListener = (DialogMarkerTypeModifyFragmentListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement dialogMarkerTypeModifyFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View retView = inflater.inflate(R.layout.fragment_dialog_new_marker_type, null);
        textViewNewMarkerTypeTitle = (TextView)retView.findViewById(R.id.fragment_dialog_new_marker_type_title);
        textViewNewMarkerTypeMemo = (TextView)retView.findViewById(R.id.fragment_dialog_new_marker_type_memo);

        textViewNewMarkerTypeTitle.setText(getArguments().getString("title"));
        textViewNewMarkerTypeMemo.setText(getArguments().getString("memo"));

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //call MapsAtivity create marker method and close dialog
                        MarkerTypeData tempMarkerTypeData = new MarkerTypeData(textViewNewMarkerTypeTitle.getText().toString(), textViewNewMarkerTypeMemo.getText().toString());
                        dialogMarkerTypeModifyFragmentListener.modifyMarkerTypeData(tempMarkerTypeData);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerTypeModifyFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            dialogMarkerTypeModifyFragmentListener = (DialogMarkerTypeModifyFragmentListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement dialogMarkerTypeModifyFragmentListener");
        }
    }
    */
}