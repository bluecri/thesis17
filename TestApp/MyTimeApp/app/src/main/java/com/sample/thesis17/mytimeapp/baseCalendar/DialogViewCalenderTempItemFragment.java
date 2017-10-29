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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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


public class DialogViewCalenderTempItemFragment extends DialogFragment implements DialogInvisibleMarkerAndFixedTimeTable.DialogInvisibleMarkerAndFixedTimeTableListener {

    private Context curContext = null;
    private Fragment curFragment = null;
    private DialogViewCalenderTempItemFragmentListener dialogViewCalenderTempItemFragmentListener = null;
    private Fragment targetFragment = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    Spinner spinnerViewTitle = null;
    Button buttonStartWeek = null;
    Button textViewStartTime = null;
    Button buttonEndWeek = null;
    Button textViewEndTime = null;
    EditText textViewMemo = null;
    Spinner spinnerMarkerName = null;
    CheckBox checkBoxTargetMarker = null;
    CheckBox checkBoxTargetTimetable = null;
    EditText textMarkerTitle = null;
    EditText textTimetableTitle = null;


    ArrayList<String> arrListMarkerDataTitle = null;
    ArrayList<String> arrListFixedTimeTable = null;

    List<FixedTimeTableData> listFixedTimeTableData = null;
    List<MarkerData> listMarkerData = null;

    List<FixedTimeTableData> listFixedTimeTableDataQueryedByMarkerData = null;

    List<FixedTimeTableData> listFixedTimeTableDataToAdapter = null;
    List<MarkerData> listMarkerDataToAdapter = null;

    ArrayAdapter<String> arrayAdapterFixedTimeTableTitle = null;

    long lStartWeek = 0, lEndWeek = 0;
    int markerDataListIdx = -1;
    int fixedTimeTableDataListIdx = -1;
    int markerDataAdpaterListIdx = 0;
    int fixedTimeTableDataAdpaterListIdx = 0;

    int startTimeHour;
    int startTimeMin;
    int endTimeHour;
    int endTimeMin;

    public interface DialogViewCalenderTempItemFragmentListener{
        //method
        //public void modifyMarkerComplete(String title, String memo);
        public void doSave(FixedTimeTableData fttd, long startTime, long endTime, MarkerData md, String memo, String markerTitle, String timetableTitle);
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
            if(lStartWeek >= lEndWeek){
                lStartWeek = lEndWeek - LONG_MIN_MILLIS;    //lStartWeek always lt lEndWeek
            }
            buttonStartWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", month+1, dayOfMonth));
        }
    };

    private DatePickerDialog.OnDateSetListener lEndWeekDatePickerListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            lEndWeek = calendar.getTimeInMillis();
            if(lStartWeek >= lEndWeek){
                lEndWeek = lStartWeek + LONG_MIN_MILLIS;    //lStartWeek always lt lEndWeek
            }
            buttonEndWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", month+1, dayOfMonth));
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
        //curFragment = this;
        targetFragment = getTargetFragment();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("예상되는 일정 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_view_calender_temp_item, null);

        spinnerViewTitle = (Spinner)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_title);
        buttonStartWeek = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_startweek);
        textViewStartTime = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_starttime);
        buttonEndWeek = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_endweek);
        textViewEndTime = (Button)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_endtime);
        textViewMemo = (EditText)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_memo);
        spinnerMarkerName = (Spinner)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_marker);
        checkBoxTargetMarker = (CheckBox)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_targetMarker_check);
        checkBoxTargetTimetable = (CheckBox)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_targetTimeTable_check);
        textMarkerTitle = (EditText)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_markerTitle);
        textTimetableTitle = (EditText)retView.findViewById(R.id.fragment_dialog_view_calender_temp_item_timetableTitle);

        checkBoxTargetMarker.setChecked(false);
        checkBoxTargetTimetable.setChecked(false);

        checkBoxTargetMarker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBoxTargetTimetable.setChecked(true);
                    //checkBoxTargetTimetable.notifyAll();
                    spinnerMarkerName.setEnabled(false);
                    spinnerViewTitle.setEnabled(false); //target marker off -> target timetable also off
                    textMarkerTitle.setEnabled(true);
                    textTimetableTitle.setEnabled(false);
                }
                else{
                    spinnerMarkerName.setEnabled(true);
                    textMarkerTitle.setEnabled(false);
                }
            }
        });

        checkBoxTargetTimetable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinnerViewTitle.setEnabled(false);
                    textTimetableTitle.setEnabled(true);
                }
                else{
                    spinnerViewTitle.setEnabled(true);
                    textTimetableTitle.setEnabled(false);
                }
            }
        });

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

        lStartWeek = startTimeArg - startTimeArg%LONG_DAY_MILLIS;   //start day - modulo with day millis = day with 00:00:00
        lEndWeek = endTimeArg - endTimeArg%LONG_DAY_MILLIS;


        GregorianCalendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(startTimeArg - LONG_HOUR_MILLIS * 9);
        //calendar.add(Calendar.HOUR_OF_DAY, 9);
        buttonStartWeek.setText(String.format(Locale.KOREA,"%02d월 %02d일", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)));

        calendar = new GregorianCalendar();
        Log.d("testCal" , "calBefore : " + calendar.toString());
        calendar.setTimeInMillis(endTimeArg - LONG_HOUR_MILLIS * 9);
        //calendar.add(Calendar.HOUR_OF_DAY, 9);
        Log.d("testCal" , "calAftre : " + calendar.toString());
        buttonEndWeek.setText(String.format(Locale.KOREA, "%02d월 %02d일", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)));

        buttonStartWeek.setOnClickListener(new View.OnClickListener() {
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

        buttonEndWeek.setOnClickListener(new View.OnClickListener() {
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

        //init markerDataTitle LIst
        arrListMarkerDataTitle = null;
        arrListMarkerDataTitle = new ArrayList<String>();
        markerDataListIdx = tempArg.getInt("markerIdx");

        for(int loop = 0, lloop = listMarkerData.size(); loop < lloop; loop++){
            //invisible 마커만 직접선택 가능
            MarkerData tempMd = listMarkerData.get(loop);
            if(tempMd.isInvisible() == false && tempMd.isCache()){
                listMarkerDataToAdapter.add(tempMd);
                arrListMarkerDataTitle.add(tempMd.getStrMarkerName());
            }
        }

        //if invisible marker is selected.. add marker to list.
        if(markerDataListIdx != -1 && listMarkerData.get(markerDataListIdx).isInvisible() == true){
            listMarkerDataToAdapter.add(listMarkerData.get(markerDataListIdx));
            arrListMarkerDataTitle.add(listMarkerData.get(markerDataListIdx).getStrMarkerName());
        }
        ArrayAdapter arrayAdapterMarkerTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListMarkerDataTitle);
        spinnerMarkerName.setAdapter(arrayAdapterMarkerTitle);


        if(markerDataListIdx == -1){
            //there is no match marker
            checkBoxTargetMarker.setChecked(true);
            if(arrListMarkerDataTitle.size() != 0){
                markerDataAdpaterListIdx = 0;  //default marker
            }
        }
        else{
            if(arrListMarkerDataTitle.size() != 0){
                //idx is not -1 && num of total marker is not 0
                markerDataAdpaterListIdx = listMarkerDataToAdapter.indexOf(listMarkerData.get(markerDataListIdx));
                spinnerMarkerName.setSelection(listMarkerDataToAdapter.indexOf(markerDataAdpaterListIdx));

            }
            else{
                //idx is not -1 && num of total marker is 0 : 마커가 하나도 없지만 해당하는 마커가 있다.
                Log.d("exception", "impossible in DialogViewCalenderTempItemFragment else");
            }
        }
        spinnerMarkerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(arrListMarkerDataTitle.size() != 0){
                    markerDataAdpaterListIdx = position;


                    //update FixedTimeTableList
                    arrayAdapterFixedTimeTableTitle.clear();
                    arrListFixedTimeTable.clear();
                    listFixedTimeTableDataQueryedByMarkerData = null;
                    listFixedTimeTableDataQueryedByMarkerData = new ArrayList<FixedTimeTableData>();

                    //arrListFixedTimeTable = new ArrayList<String>();
                    //marker에 해당하는 FixedTimeTableData list 생성

                    //전체 FixedTimeTableData에서 해당 마커에 해당하는 timetable 찾기
                    for(FixedTimeTableData fttd : listFixedTimeTableData){
                        if(fttd.getForeMarkerData().equals((listMarkerDataToAdapter.get(markerDataAdpaterListIdx)))){
                            if(fttd.isInvisible() == false && fttd.isCache()){
                                listFixedTimeTableDataQueryedByMarkerData.add(fttd);
                                arrListFixedTimeTable.add(fttd.getStrFixedTimeTableName());

                            }
                        }
                    }
                    //if invisible timetable is target... add to list
                    if(listMarkerData.get(markerDataListIdx).equals((listMarkerDataToAdapter.get(markerDataAdpaterListIdx))) && fixedTimeTableDataListIdx != -1 && listFixedTimeTableData.get(fixedTimeTableDataListIdx).isInvisible() == true) {
                        listFixedTimeTableDataQueryedByMarkerData.add(listFixedTimeTableData.get(fixedTimeTableDataListIdx));
                        arrListFixedTimeTable.add(listFixedTimeTableData.get(fixedTimeTableDataListIdx).getStrFixedTimeTableName());     //timetable to string
                    }
                    arrayAdapterFixedTimeTableTitle.addAll(arrListFixedTimeTable);
                    arrayAdapterFixedTimeTableTitle.notifyDataSetChanged();
                    //spinnerMarkerName.setSelection(-1);
                }
                else{
                    //do nothing.. no marker in app
                }
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

        if(markerDataListIdx == -1){    //&& arrListMarkerDataTitle.size == 0
            //there is no match marker
            checkBoxTargetTimetable.setChecked(true);

            if(listMarkerData.size() != 0){
                //marker is not selected. default -> 0
                for(FixedTimeTableData fttd : listFixedTimeTableData){
                    if(fttd.getForeMarkerData().equals((listMarkerData.get(0)))){
                        if(fttd.isInvisible() == false && fttd.isCache()){
                            listFixedTimeTableDataQueryedByMarkerData.add(fttd);
                            arrListFixedTimeTable.add(fttd.getStrFixedTimeTableName());
                        }
                    }
                }
            }
            //set adapter
            arrayAdapterFixedTimeTableTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListFixedTimeTable);
            spinnerViewTitle.setAdapter(arrayAdapterFixedTimeTableTitle);
        }
        else {
            //get listFixedTimeTable with marker
            for(FixedTimeTableData fttd : listFixedTimeTableData){
                if(fttd.getForeMarkerData().equals((listMarkerData.get(markerDataListIdx)))){
                    if(fttd.isInvisible() == false && fttd.isCache()){
                        listFixedTimeTableDataQueryedByMarkerData.add(fttd);
                        arrListFixedTimeTable.add(fttd.getStrFixedTimeTableName());     //timetable to string
                    }
                }
            }
            fixedTimeTableDataListIdx = tempArg.getInt("fixedTimeTableIdx");

            //if invisible timetable is target... add to list
            if(fixedTimeTableDataListIdx != -1 && listFixedTimeTableData.get(fixedTimeTableDataListIdx).isInvisible() == true) {
                listFixedTimeTableDataQueryedByMarkerData.add(listFixedTimeTableData.get(fixedTimeTableDataListIdx));
                arrListFixedTimeTable.add(listFixedTimeTableData.get(fixedTimeTableDataListIdx).getStrFixedTimeTableName());     //timetable to string
            }

            //set adapter
            arrayAdapterFixedTimeTableTitle = new ArrayAdapter<String>(curContext, R.layout.support_simple_spinner_dropdown_item, arrListFixedTimeTable);
            spinnerViewTitle.setAdapter(arrayAdapterFixedTimeTableTitle);

            //get target tFixedTimetable index

            if(fixedTimeTableDataListIdx == -1){
                //there is no match fixedTimeTable
                checkBoxTargetTimetable.setChecked(true);
                if(arrListFixedTimeTable.size() != 0){
                    //no match with fixedTimeTable
                    fixedTimeTableDataAdpaterListIdx = 0;  //default value
                    spinnerViewTitle.setSelection(fixedTimeTableDataAdpaterListIdx);
                }
            }
            else{
                //has target timetable
                fixedTimeTableDataAdpaterListIdx = listFixedTimeTableDataQueryedByMarkerData.indexOf(listFixedTimeTableData.get(fixedTimeTableDataListIdx));
                spinnerViewTitle.setSelection(fixedTimeTableDataAdpaterListIdx);

            }
        }


        spinnerViewTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //fixedTimeTableDataListIdx : listFixedTimeTableData index -> listFixedTimeTableDataQueryedByMarkerData
                fixedTimeTableDataAdpaterListIdx = position;
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
                        if(checkBoxTargetMarker.isChecked() || arrListMarkerDataTitle.size() == 0){
                            //markerData => no bind, fixed also no bind
                            //DialogInvisibleMarkerAndFixedTimeTable invisibleDig = new DialogInvisibleMarkerAndFixedTimeTable();
                            //invisibleDig.setTargetFragment(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("DialogViewCalenderTempItemFragment"), 0);
                            //invisibleDig.setTargetFragment(targetFragment, 0);
                            ///Bundle arg = new Bundle();
                            //invisibleDig.setArguments(arg); //empty bundle
                            //invisibleDig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogInvisibleMarkerAndFixedTimeTable");
                            dialogViewCalenderTempItemFragmentListener.doSave(null, retLStartTimeMillis, retLEndTimeMillis, null, textViewMemo.getText().toString(), textMarkerTitle.getText().toString(), textTimetableTitle.getText().toString());
                        }
                        else if(checkBoxTargetTimetable.isChecked() || arrListFixedTimeTable.size() == 0){
                            //DialogInvisibleMarkerAndFixedTimeTable invisibleDig = new DialogInvisibleMarkerAndFixedTimeTable();
                            //invisibleDig.setTargetFragment(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("DialogViewCalenderTempItemFragment"), 0);
                            //invisibleDig.setTargetFragment(targetFragment, 0);
                            //Bundle arg = new Bundle();
                            //arg.putString("markerTitle", listMarkerData.get(markerDataListIdx).getStrMarkerName());
                            //invisibleDig.setArguments(arg); //empty bundle
                            //invisibleDig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogInvisibleMarkerAndFixedTimeTable");
                            dialogViewCalenderTempItemFragmentListener.doSave(null, retLStartTimeMillis, retLEndTimeMillis, listMarkerData.get(markerDataListIdx), textViewMemo.getText().toString(), null, textTimetableTitle.getText().toString());
                        }
                        else{
                            dialogViewCalenderTempItemFragmentListener.doSave(listFixedTimeTableDataQueryedByMarkerData.get(fixedTimeTableDataAdpaterListIdx), retLStartTimeMillis, retLEndTimeMillis, listMarkerDataToAdapter.get(markerDataAdpaterListIdx), textViewMemo.getText().toString(), null, null);
                        }
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


    @Override
    public void doSaveWithInvisibleMarkerAndTimeTable(String markerTitle, String timetableTitle) {
        /*long retLStartTimeMillis = startTimeHour*LONG_HOUR_MILLIS + startTimeMin*LONG_MIN_MILLIS + lStartWeek;  //hour + min + day(WEEK);
        long retLEndTimeMillis = endTimeHour*LONG_HOUR_MILLIS + endTimeMin*LONG_MIN_MILLIS + lEndWeek;
        if(checkBoxTargetMarker.isChecked() || arrListMarkerDataTitle.size() == 0){
            //with new markerTitle, timetableTitle

            //create marker with invisible
            dialogViewCalenderTempItemFragmentListener.doSave(null, retLStartTimeMillis, retLEndTimeMillis, null, textViewMemo.getText().toString(), markerTitle, timetableTitle);
        }
        else if(checkBoxTargetTimetable.isChecked() || arrListFixedTimeTable.size() == 0){
            //with new markerTitle, timetableTitle
            dialogViewCalenderTempItemFragmentListener.doSave(null, retLStartTimeMillis, retLEndTimeMillis, listMarkerData.get(markerDataListIdx), textViewMemo.getText().toString(), null, timetableTitle);
        }
        else{
            dialogViewCalenderTempItemFragmentListener.doSave(listFixedTimeTableDataQueryedByMarkerData.get(fixedTimeTableDataListIdx), retLStartTimeMillis, retLEndTimeMillis, listMarkerData.get(markerDataListIdx), textViewMemo.getText().toString(), null, null);
        }
        */
    }

}