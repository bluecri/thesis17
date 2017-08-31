package com.sample.thesis17.mytimeapp.map;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sample.thesis17.mytimeapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMarkerTypeSelectFragment extends DialogFragment {
    public interface DialogMarkerTypeSelectListener{
        //method
        public void setINewMarkerType(int iMarkerTypesBit);
    }

    DialogMarkerTypeSelectListener dialogMarkerTypeSelectListener = null;

    public int iMarkerTypesBit = 0;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dialogMarkerTypeSelectListener = (DialogMarkerTypeSelectListener) context;
            Activity tempActivity = getActivity();
            if(tempActivity != null && tempActivity instanceof MapsActivity){
                iMarkerTypesBit = ((MapsActivity) tempActivity).getINewMarkerType();    //다시 수정하는 경우 기존의 type bit를 MapsActivity에서 가져온다.
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).toString() + "must implement DialogMarkerTypeSelectListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        //TODO : ?
        View retView = inflater.inflate(R.layout.fragment_dialog_newmarker, null);
       // textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_newmarker_marker_name);


        builder.setView(retView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialogMarkerTypeSelectListener.setINewMarkerType(10);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerTypeSelectFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

}
