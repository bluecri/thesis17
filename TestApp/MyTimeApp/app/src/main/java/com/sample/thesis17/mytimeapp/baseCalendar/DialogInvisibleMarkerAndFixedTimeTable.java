package com.sample.thesis17.mytimeapp.baseCalendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;

/**
 * Created by kimz on 2017-10-25.
 */



public class DialogInvisibleMarkerAndFixedTimeTable extends DialogFragment {

    private Context curContext = null;
    private DialogInvisibleMarkerAndFixedTimeTableListener dialogInvisibleMarkerAndFixedTimeTableListener = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    /*TextView textViewTitle = null;
    TextView textViewStartWeek = null;
    TextView textViewStartTime = null;
    TextView textViewEndWeek = null;
    TextView textViewEndTime = null;
    TextView textViewMemo = null;
    TextView textViewMarkerName = null;*/

    EditText editTextMarkerName = null;
    EditText editTextFixedTimeTableName = null;


    public interface DialogInvisibleMarkerAndFixedTimeTableListener{
        public void doSaveWithInvisibleMarkerAndTimeTable(String markerTitle, String timetableTitle);
    }

    public DialogInvisibleMarkerAndFixedTimeTable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dialogInvisibleMarkerAndFixedTimeTableListener = (DialogInvisibleMarkerAndFixedTimeTableListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement dialogMarkerTypeModifyFragmentListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogMarkerModifyFr", "DialogWeekItemViewFragment onattach");
        curContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogWeekItemViewFra", "DialogWeekItemViewFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_invisible_marker_and_fixedtimetable, null);

        editTextMarkerName = (EditText)retView.findViewById(R.id.fragment_dialog_invisible_marker_edittext);
        editTextFixedTimeTableName = (EditText)retView.findViewById(R.id.fragment_dialog_invisible_timetable_edittext);



        Bundle tempArg = getArguments();
        if(tempArg.getString("markerTitle") != null){
            editTextMarkerName.setText(tempArg.getString("markerTitle"));
            editTextMarkerName.setEnabled(false);
        }
        else{
            editTextMarkerName.setText("");
            editTextMarkerName.setEnabled(true);
        }

        if(tempArg.getString("timetableTitle") != null){
            editTextFixedTimeTableName.setText(tempArg.getString("timetableTitle"));
            editTextFixedTimeTableName.setEnabled(false);
        }
        else{
            editTextFixedTimeTableName.setText("");
            editTextFixedTimeTableName.setEnabled(true);
        }

        builder.setView(retView)
                // Add action buttons
                /*.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogViewCalenderItemFragmentListener.openModifyDialogWithIdx();
                    }
                })
                */
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //dialogInvisibleMarkerAndFixedTimeTableListener.doSaveWithInvisibleMarkerAndTimeTable(editTextMarkerName.getText().toString(), editTextFixedTimeTableName.getText().toString());
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogInvisibleMarkerAndFixedTimeTable.this.getDialog().cancel();  //cancel dialog

                    }
                });


        return builder.create();
    }
}