package com.sample.thesis17.mytimeapp.map;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;

import static com.sample.thesis17.mytimeapp.Static.MyMath.CUSTOM_DRADIUS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogNewmarkerFragment extends DialogFragment {

    public interface DialogNewmarkerListener{
        //method
        public void registerNewMarker(MarkerData data);
    }

    DialogNewmarkerListener dialogNewmarkerListener = null;

    TextView textViewMarkerName = null;
    TextView textViewMarkerMemo = null;
    Button buttonSelectType = null;

    ArrayList<MarkerTypeData> newMarkerMarkerTypeList = null;   //deprecated

    Context curContext = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("새로운 지역(마커) 생성 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_newmarker, null);
        textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_newmarker_marker_name);
        textViewMarkerMemo = (TextView)retView.findViewById(R.id.fragment_dialog_newmarker_marker_memo);
        buttonSelectType = (Button)retView.findViewById(R.id.fragment_dialog_newmarker_button_selectType);


        buttonSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMarkerTypeSelectFragment dig = new DialogMarkerTypeSelectFragment();
                dig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogMarkerTypeSelectFragment");
            }
        });

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //call MapsAtivity create marker method and close dialog
                        MarkerData tempMarkerData = new MarkerData(0, 0, textViewMarkerName.getText().toString(), CUSTOM_DRADIUS, CUSTOM_DRADIUS, textViewMarkerMemo.getText().toString(), true, false);
                        dialogNewmarkerListener.registerNewMarker(tempMarkerData);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogNewmarkerFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        try{
            dialogNewmarkerListener = (DialogNewmarkerListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogNewmarkerListener");
        }
    }

    //DialogMarkerTypeSelectFragment -> MapsActivity -> DialogNewmarkerFaragment
    public void setNewMarkerMarkerTypeList(ArrayList<MarkerTypeData> markerTypeDataList){
        newMarkerMarkerTypeList = markerTypeDataList;   //deprecated
    }
}
