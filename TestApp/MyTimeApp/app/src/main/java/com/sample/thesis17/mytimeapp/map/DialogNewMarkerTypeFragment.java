package com.sample.thesis17.mytimeapp.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

public class DialogNewMarkerTypeFragment extends DialogFragment {

    public interface DialogNewMarkerTypeFragmentListener{
        public void createNewMarkerType(MarkerTypeData mtd);
    }

    DialogNewMarkerTypeFragmentListener dialogNewMarkerTypeListener = null;

    TextView textViewNewMarkerTypeTitle = null;
    TextView textViewNewMarkerTypeMemo = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("새로운 마커 타입 생성 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View retView = inflater.inflate(R.layout.fragment_dialog_new_marker_type, null);
        textViewNewMarkerTypeTitle = (TextView)retView.findViewById(R.id.fragment_dialog_new_marker_type_title);
        textViewNewMarkerTypeMemo = (TextView)retView.findViewById(R.id.fragment_dialog_new_marker_type_memo);


        builder.setView(retView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //call MapsAtivity create marker method and close dialog
                        MarkerTypeData tempMarkerTypeData = new MarkerTypeData(textViewNewMarkerTypeTitle.getText().toString(), textViewNewMarkerTypeMemo.getText().toString());
                        dialogNewMarkerTypeListener.createNewMarkerType(tempMarkerTypeData);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogNewMarkerTypeFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            dialogNewMarkerTypeListener = (DialogNewMarkerTypeFragmentListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogNewmarkerListener");
        }
    }
}
