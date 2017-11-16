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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.Static.MyMath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMarkerFragment extends DialogFragment {
    public static int DOUBLE_DECIMAL_ROUND = 5;

    public interface DialogMarkerListener{
        //method
        public void modifyMarker();
        public void deleteMarker();
        public ArrayList<String> getSpinnerMarkerTypeDataStringList();
    }

    DialogMarkerListener dialogMarkerListener = null;

    TextView textViewMarkerName = null;
    TextView textViewMarkerMemo = null;
    TextView textViewMarkerLatLng = null;
    Spinner spinnerSelectType = null;

    ArrayList<String> markerTypeStringList = null;

    Context curContext = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DialogMarkerFragment", "DialogMarkerFragment onCreate");
        markerTypeStringList = dialogMarkerListener.getSpinnerMarkerTypeDataStringList();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogMarkerFragment", "DialogMarkerFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("마커 정보 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_marker, null);
        textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_marker_marker_name);
        textViewMarkerMemo = (TextView)retView.findViewById(R.id.fragment_dialog_marker_marker_memo);
        textViewMarkerLatLng = (TextView)retView.findViewById(R.id.fragment_dialog_marker_marker_latLng);
        spinnerSelectType = (Spinner)retView.findViewById(R.id.fragment_dialog_marker_spinner_selectType);


        if(spinnerSelectType == null){
            Log.d("DialogMarkerFragment", "spinner null onCreateDialog");
        }

        spinnerSelectType.setAdapter(new ArrayAdapter<String>(curContext,R.layout.support_simple_spinner_dropdown_item, markerTypeStringList));
        spinnerSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //do nothing
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
        Bundle tempArg = getArguments();
        textViewMarkerName.setText(tempArg.getString("title"));
        textViewMarkerMemo.setText(tempArg.getString("memo"));
        textViewMarkerLatLng.setText("latitude : " + MyMath.doubleRound(tempArg.getDouble("lat"), DOUBLE_DECIMAL_ROUND) + ", longitude : " + MyMath.doubleRound(tempArg.getDouble("lng"), DOUBLE_DECIMAL_ROUND));
        builder.setView(retView)
                // Add action buttons
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogMarkerListener.modifyMarker();
                    }
                })
                .setNeutralButton("삭제", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogMarkerListener.deleteMarker();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogMarkerFragment", "DialogMarkerFragment onattach");
        curContext = context;
        try{
            dialogMarkerListener = (DialogMarkerListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogNewmarkerListener");
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        //databaseHelperMain.close();
    }


}