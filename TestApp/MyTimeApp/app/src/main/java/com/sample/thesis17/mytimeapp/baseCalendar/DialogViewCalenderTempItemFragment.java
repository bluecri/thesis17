package com.sample.thesis17.mytimeapp.baseCalendar;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_MIN_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.WEEK_STRING;
import static com.sample.thesis17.mytimeapp.Static.MyMath.WEEK_STRING_REAL;

/**
 * Created by kimz on 2017-09-23.
 */


public class DialogViewCalenderTempItemFragment extends DialogFragment{

    private Context curContext = null;
    private DialogViewCalenderTempItemFragmentListener dialogViewCalenderTempItemFragmentListener = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    Spinner spinnerViewTitle = null;
    Button buttonStartWeek = null;
    Button textViewStartTime = null;
    Button buttonEndWeek = null;
    Button textViewEndTime = null;
    EditText textViewMemo = null;
    Spinner spinnerMarkerName = null;

    ArrayList<String> arrListMarkerDataTitle = null;
    ArrayList<String> arrListFixedTimeTable = null;

    List<FixedTimeTableData> listFixedTimeTableData = null;
    List<MarkerData> listMarkerData = null;
    List<FixedTimeTableData> listFixedTimeTableDataQueryedByMarkerData = null;

    ArrayAdapter<String> arrayAdapterFixedTimeTableTitle = null;

    long lStartWeek = 0, lEndWeek = 0;
    int markerDataListIdx = -1;
    int fixedTimeTableDataListIdx = -1;

    int startTimeHour;
    int startTimeMin;
    int endTimeHour;
    int endTimeMin;


    public interface DialogViewCalenderTempItemFragmentListener{
        //method
        //public void modifyMarkerComplete(String title, String memo);
        public void doSave(FixedTimeTableData fttd, long startTime, long endTime, MarkerData md, String memo);
        public void doDeepDelete();
        //public ArrayList<String> getArrayListMarkerDataTitle();
        //public void openModifyDialogWithIdx();
        //public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList();
        public List<MarkerData> getListMarkerData();
        public List<FixedTimeTableData> getListFixedTimeTableData();

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

    private DatePickerDialog.OnDateSetListener lStartWeekDatePickerListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            lStartWeek = calendar.getTimeInMillis();
            buttonStartWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", month, dayOfMonth));
        }
    };

    private DatePickerDialog.OnDateSetListener lEndWeekDatePickerListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            lEndWeek = calendar.getTimeInMillis();
            buttonEndWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", month, dayOfMonth));
        }
    };



    public DialogViewCalenderTempItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DialogMarkerModifyFra", "DialogViewCalenderTempItemFragment onCreate");
        //markerTypeList = -Listener.getMarkerTypeModifiedDataList();
        try{
            dialogViewCalenderTempItemFragmentListener = (DialogViewCalenderTempItemFragmentListener) getTargetFragment();
            listFixedTimeTableData = dialogViewCalenderTempItemFragmentListener.getListFixedTimeTableData();
            listMarkerData = dialogViewCalenderTempItemFragmentListener.getListMarkerData();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement dialogMarkerTypeModifyFragmentListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogWeekItemModi", "DialogViewCalenderTempItemFragment onattach");
        curContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogWeekItemMo", "DialogViewCalenderTempItemFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_timetable_item_modify, null);

        spinnerViewTitle = (Spinner)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_title);
        buttonStartWeek = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_startweek);
        textViewStartTime = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_starttime);
        buttonEndWeek = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_endweek);
        textViewEndTime = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_endtime);
        textViewMemo = (EditText)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_memo);
        spinnerMarkerName = (Spinner)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_marker);



        Bundle tempArg = getArguments();

        textViewMemo.setText(tempArg.getString("memo"));

        //button

        long startTimeArg =tempArg.getLong("starttime");
        long endTimeArg =tempArg.getLong("endtime");

        startTimeHour = (int)((startTimeArg%LONG_DAY_MILLIS)/LONG_HOUR_MILLIS);
        startTimeMin = (int)(startTimeArg%LONG_HOUR_MILLIS/LONG_MIN_MILLIS);
        endTimeHour = (int)((endTimeArg%LONG_DAY_MILLIS)/LONG_HOUR_MILLIS);
        endTimeMin = (int)(endTimeArg%LONG_HOUR_MILLIS/LONG_MIN_MILLIS);

        textViewStartTime.setText(String.format(Locale.KOREA, "%02d:%02d", startTimeHour, startTimeMin));
        textViewEndTime.setText(String.format(Locale.KOREA, "%02d:%02d", endTimeHour, endTimeMin));

        //TimePickerDialog timeDialog = new TimePickerDialog(curContext, timePickerListener, , , false);
        //timeDialog.show();

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

        //long startTimeArg =tempArg.getLong("starttime");
        //long endTimeArg =tempArg.getLong("endtime");

        lStartWeek = startTimeArg - startTimeArg%LONG_DAY_MILLIS;
        lEndWeek = endTimeArg - endTimeArg%LONG_DAY_MILLIS;


        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(startTimeArg);
        buttonStartWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        calendar = new GregorianCalendar();
        calendar.setTimeInMillis(endTimeArg);
        buttonEndWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(lStartWeek);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(curContext, lStartWeekDatePickerListener, year, month, day);
                dateDialog.show();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(lStartWeek);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog = new DatePickerDialog(curContext, lEndWeekDatePickerListener, year, month, day);
                dateDialog.show();
            }
        });

        /*
        //spinner
        lStartWeek = (int)((startTimeArg%LONG_WEEK_MILLIS)/LONG_DAY_MILLIS);
        lEndWeek = (int)((endTimeArg%LONG_WEEK_MILLIS)/LONG_DAY_MILLIS);

        ArrayList<String> arrListWeekString = new ArrayList<String>();
        for(String str : WEEK_STRING_REAL){
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
        */

        arrListMarkerDataTitle = null;
        arrListMarkerDataTitle = new ArrayList<String>();
        for(MarkerData md : listMarkerData){
            arrListMarkerDataTitle.add(md.getStrMarkerName());
        }
        ArrayAdapter arrayAdapterMarkerTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListMarkerDataTitle);
        spinnerMarkerName.setAdapter(arrayAdapterMarkerTitle);
        markerDataListIdx = tempArg.getInt("markerIdx");
        spinnerMarkerName.setSelection(markerDataListIdx);
        spinnerMarkerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                markerDataListIdx = position;
                //update FixedTimeTableList
                arrayAdapterFixedTimeTableTitle.clear();

                arrListFixedTimeTable = new ArrayList<String>();
                //marker에 해당하는 FixedTimeTableData list 생성
                listFixedTimeTableDataQueryedByMarkerData = null;
                listFixedTimeTableDataQueryedByMarkerData = new ArrayList<FixedTimeTableData>();
                for(FixedTimeTableData fttd : listFixedTimeTableData){
                    if(fttd.getForeMarkerData().equals((listMarkerData.get(markerDataListIdx)))){
                        listFixedTimeTableDataQueryedByMarkerData.add(fttd);
                    }
                }
                for(FixedTimeTableData fttd : listFixedTimeTableDataQueryedByMarkerData){
                    arrListFixedTimeTable.add(fttd.getStrFixedTimeTableName());
                    arrayAdapterFixedTimeTableTitle.add(fttd.getStrFixedTimeTableName());
                }
                arrayAdapterFixedTimeTableTitle.notifyDataSetChanged();
                spinnerMarkerName.setSelection(-1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });


        //spinnerViewTitle

        arrListFixedTimeTable = null;
        arrListFixedTimeTable = new ArrayList<String>();

        //marker에 해당하는 FixedTimeTableData list 생성
        listFixedTimeTableDataQueryedByMarkerData = null;
        listFixedTimeTableDataQueryedByMarkerData = new ArrayList<FixedTimeTableData>();
        for(FixedTimeTableData fttd : listFixedTimeTableData){
            if(fttd.getForeMarkerData().equals((listMarkerData.get(markerDataListIdx)))){
                listFixedTimeTableDataQueryedByMarkerData.add(fttd);
            }
        }
        for(FixedTimeTableData fttd : listFixedTimeTableDataQueryedByMarkerData){
            arrListFixedTimeTable.add(fttd.getStrFixedTimeTableName());
        }
        arrayAdapterFixedTimeTableTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListFixedTimeTable);
        spinnerMarkerName.setAdapter(arrayAdapterFixedTimeTableTitle);
        fixedTimeTableDataListIdx = tempArg.getInt("fixedTimeTableIdx");
        fixedTimeTableDataListIdx = listFixedTimeTableDataQueryedByMarkerData.indexOf(listFixedTimeTableData.get(fixedTimeTableDataListIdx));
        spinnerMarkerName.setSelection(fixedTimeTableDataListIdx);
        spinnerMarkerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //fixedTimeTableDataListIdx : listFixedTimeTableData index -> listFixedTimeTableDataQueryedByMarkerData
                fixedTimeTableDataListIdx = position;
                //do nothing
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });



        builder.setView(retView)
                // Add action buttons
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //calc starttime, endtime
                        long retLStartTimeMillis = startTimeHour*LONG_HOUR_MILLIS + startTimeMin*LONG_MIN_MILLIS + lStartWeek;  //hour + min + day(WEEK);
                        long retLEndTimeMillis = endTimeHour*LONG_HOUR_MILLIS + endTimeMin*LONG_MIN_MILLIS + lEndWeek;
                        dialogViewCalenderTempItemFragmentListener.doSave(listFixedTimeTableDataQueryedByMarkerData.get(fixedTimeTableDataListIdx), retLStartTimeMillis, retLEndTimeMillis, listMarkerData.get(markerDataListIdx), textViewMemo.getText().toString());
                    }
                })
                .setNeutralButton("완전삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogViewCalenderTempItemFragmentListener.doDeepDelete();

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogViewCalenderTempItemFragment.this.getDialog().cancel();  //cancel dialog

                    }
                });


        return builder.create();
    }
}