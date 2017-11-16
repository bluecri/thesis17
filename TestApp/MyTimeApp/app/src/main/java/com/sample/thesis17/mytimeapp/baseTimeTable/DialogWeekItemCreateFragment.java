package com.sample.thesis17.mytimeapp.baseTimeTable;

import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_MIN_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.WEEK_STRING;

/**
 * Created by kimz on 2017-09-19.
 */

public class DialogWeekItemCreateFragment extends DialogFragment{
    private Context curContext = null;
    private DialogWeekItemCreateFragmentListener dialogWeekItemCreateFragmentListener = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    EditText textViewTitle = null;
    Spinner spinnerStartWeek = null;
    Button textViewStartTime = null;
    Spinner spinnerEndWeek = null;
    Button textViewEndTime = null;
    EditText textViewMemo = null;
    Spinner spinnerMarkerName = null;

    ArrayList<String> arrListMarkerDataTitle = null;
    List<MarkerData> listMarkerDataList = null;
    List<MarkerData> listInnerSpinnerMarkerDataList = null;

    int iStartWeek = 0, iEndWeek = 0;
    int markerDataListIdx = -1;
    int innerSpinnerIdx = -1;

    int startTimeHour;
    int startTimeMin;
    int endTimeHour;
    int endTimeMin;


    public interface DialogWeekItemCreateFragmentListener{
        //method
        //public void modifyMarkerComplete(String title, String memo);
        public void doCreate(String title, long startTime, long endTime, int markerIdx, String memo);
        public List<MarkerData> getArrayListMarkerData();
        //public void openModifyDialogWithIdx();
        //public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList();
    }

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTimeHour = hourOfDay;
            startTimeMin = minute;
            textViewStartTime.setText(String.format(Locale.KOREA, "%02d:%02d", startTimeHour, startTimeMin));

        }
    };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endTimeHour = hourOfDay;
            endTimeMin = minute;
            textViewEndTime.setText(String.format(Locale.KOREA, "%02d:%02d", endTimeHour, endTimeMin));
        }
    };


    public DialogWeekItemCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DialogWeekItemCreat", "DialogWeekItemCreateFragment onCreate");
        //markerTypeList = -Listener.getMarkerTypeModifiedDataList();
        try{
            dialogWeekItemCreateFragmentListener = (DialogWeekItemCreateFragmentListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement dialogMarkerTypeModifyFragmentListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogWeekItemCrea", "DialogWeekItemCreateFragment onattach");
        curContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogWeekItemCrea", "DialogWeekItemCreateFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("새로운 시간표 생성 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_timetable_item_create, null);

        textViewTitle = (EditText)retView.findViewById(R.id.fragment_dialog_timetable_item_create_title);
        spinnerStartWeek = (Spinner)retView.findViewById(R.id.fragment_dialog_timetable_item_create_startweek);
        textViewStartTime = (Button)retView.findViewById(R.id.fragment_dialog_timetable_item_create_starttime);
        spinnerEndWeek = (Spinner)retView.findViewById(R.id.fragment_dialog_timetable_item_create_endweek);
        textViewEndTime = (Button)retView.findViewById(R.id.fragment_dialog_timetable_item_create_endtime);
        textViewMemo = (EditText)retView.findViewById(R.id.fragment_dialog_timetable_item_create_memo);
        spinnerMarkerName = (Spinner)retView.findViewById(R.id.fragment_dialog_timetable_item_create_marker);

        Bundle tempArg = getArguments();

        startTimeHour = 0;
        startTimeMin = 0;
        endTimeHour = 0;
        endTimeMin = 0;

        textViewStartTime.setText(String.format(Locale.KOREA, "%02d:%02d", startTimeHour, startTimeMin));
        textViewEndTime.setText(String.format(Locale.KOREA, "%02d:%02d", endTimeHour, endTimeMin));

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timeDialog = new TimePickerDialog(curContext, startTimePickerListener, startTimeHour, endTimeHour, false);
                timeDialog.show();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timeDialog = new TimePickerDialog(curContext, endTimePickerListener, endTimeHour, endTimeMin, false);
                timeDialog.show();
            }
        });

        //spinner
        iStartWeek = 0;
        iEndWeek = 0;

        ArrayList<String> arrListWeekString = new ArrayList<String>();
        for(String str : WEEK_STRING){
            arrListWeekString.add(str);
        }

        ArrayAdapter arrayAdapterStartWeek = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListWeekString);
        ArrayAdapter arrayAdapterEndWeek = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListWeekString);


        spinnerStartWeek.setAdapter(arrayAdapterStartWeek);
        //최초값 설정
        spinnerStartWeek.setSelection(iStartWeek);
        spinnerEndWeek.setAdapter(arrayAdapterEndWeek);
        spinnerEndWeek.setSelection(iEndWeek);

        spinnerStartWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iStartWeek = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        spinnerEndWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iEndWeek = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        innerSpinnerIdx = -1;
        listMarkerDataList = dialogWeekItemCreateFragmentListener.getArrayListMarkerData();
        listInnerSpinnerMarkerDataList = new ArrayList<>();
        arrListMarkerDataTitle = new ArrayList<>();
        for(MarkerData md : listMarkerDataList){
            if(md.isInvisible() == false){
                listInnerSpinnerMarkerDataList.add(md);
                arrListMarkerDataTitle.add(md.getStrMarkerName());
            }
        }

        ArrayAdapter arrayAdapterMarkerTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListMarkerDataTitle);
        spinnerMarkerName.setAdapter(arrayAdapterMarkerTitle);
        markerDataListIdx = -1; //non selected
        spinnerMarkerName.setSelection(markerDataListIdx);
        spinnerMarkerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                innerSpinnerIdx = position;
                //do nothing
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //calc starttime, endtime
                        long retLStartTimeMillis = (long)startTimeHour*LONG_HOUR_MILLIS + (long)startTimeMin*LONG_MIN_MILLIS + iStartWeek*LONG_DAY_MILLIS;  //hour + min + day(WEEK)
                        long retLEndTimeMillis = (long)endTimeHour*LONG_HOUR_MILLIS + (long)endTimeMin*LONG_MIN_MILLIS + iEndWeek*LONG_DAY_MILLIS;
                        if(innerSpinnerIdx < 0){
                            //not selected
                            Toast.makeText(curContext, "마커를 반드시 선택해야 합니다.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            dialogWeekItemCreateFragmentListener.doCreate(textViewTitle.getText().toString(), retLStartTimeMillis, retLEndTimeMillis, listMarkerDataList.indexOf(listInnerSpinnerMarkerDataList.get(innerSpinnerIdx)), textViewMemo.getText().toString());
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogWeekItemCreateFragment.this.getDialog().cancel();  //cancel dialog

                    }
                });


        return builder.create();
    }
}
