package com.sample.thesis17.mytimeapp.baseCalendar.week;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperLocationMemory;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.DateForTempHisoryData;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryLMData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryMarkerData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.baseCalendar.CalenderWeekItemIdxWithIsHistoryData;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogViewCalenderItemFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogViewCalenderTempItemFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.LocationGroup;
import com.sample.thesis17.mytimeapp.baseTimeTable.week.CustomWeekView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData.FIXEDTIMETABLE_MARKERDATA_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData.LOCATIONMEMORY_BDUMMY_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData.LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData.LOCATIONMEMORY_BINDEDTEMPHISTORYDATA_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData.LOCATIONMEMORY_TIMEWRITTEN_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData.TH_TEMPHISTORYDATA_ENDTIME_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData.TH_TEMPHISTORYDATA_STARTTIME_FIELD_NAME;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_GROUP_INTERVAL;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;


public class CalenderWeekFragment extends Fragment implements DialogViewCalenderItemFragment.DialogViewCalenderItemFragmentListener, DialogViewCalenderTempItemFragment.DialogViewCalenderTempItemFragmentListener{
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View weekGridview;
    CalenderWeekView customWeekView;
    TextView centerText;
    Button prevButton, nextButton, refreshButton;
    Context curContext;

    long weekFirstTimeLong = 0;
    long curTimeLong = 0;

    //DB
    List<FixedTimeTableData> listFixedTimeTableData = null;
    //List<FixedTimeTableData> listFixedTimeTableDataQueryedByMarkerData = null;
    List<MarkerData> listMarkerData = null;
    List<TempHistoryData> listTempHistoryData = null;
    List<HistoryData> listHistoryData = null;

    //List<DateForTempHisoryData> listDateForTempHisoryData = null;

    Dao<FixedTimeTableData, Integer> daoFixedTimeTableDataInteger = null;
    Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    Dao<TempHistoryData, Integer> daoTempHistoryDataInteger = null;
    Dao<DateForTempHisoryData, Integer> daoDateForTempHistoryData = null;
    Dao<HistoryData, Integer> daoHistoryDataInteger = null;
    Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = null;
    Dao<TempHistoryLMData, Integer> daoTempHistoryLMDataInteger = null;
    Dao<TempHistoryMarkerData, Integer> daoTempHistoryMarkerDataInteger = null;

    CalenderWeekAdapter calenderWeekAdapter = null;

    CalenderWeekFragmentListener calenderWeekFragmentListener = null;


    //dialog
    DialogViewCalenderTempItemFragment dialogViewCalenderTempItemFragment = null;
    DialogViewCalenderItemFragment dialogViewCalenderItemFragment = null;
    Bundle bundleArg = null;

    CalenderWeekItemIdxWithIsHistoryData idxWithIsHistoryData = null;        //listFixedTimeTableData(시간표box list)에서 선택된 index

    //origin value
    FixedTimeTableData originFixedTimeTableData = null;
    MarkerData originMarkerData = null;

    //int curYear;        //현재 달력의 년, 월.
    //int curMonth;

    //CONSTANTS
    double DOUBLE_GROUPING_2POW_RADIUS = 0.000000035;  // 0.0.0000000676 공학관 한동, 운동장크기

    public interface CalenderWeekFragmentListener{
        void fragmentChangeToMonthView(long inTime);
    }

    public CalenderWeekFragment() {
        // Required empty public constructor
    }
    public long getStartLongTimeOfFragmentArgument(){
        if(getArguments() != null){
            return getArguments().getLong(ARG_PARAM1);
        }
        return System.currentTimeMillis();
    }

    public static CalenderWeekFragment newInstance(long weekFirstTime) {
        CalenderWeekFragment fragment = new CalenderWeekFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, weekFirstTime);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weekFirstTimeLong = getArguments().getLong(ARG_PARAM1);

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            daoTempHistoryDataInteger = getDatabaseHelperMain().getDaoTempHistoryData();
            daoHistoryDataInteger = getDatabaseHelperMain().getDaoHistoryData();
            daoDateForTempHistoryData = getDatabaseHelperMain().getDaoDateForTempHisoryData();
            daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
            daoTempHistoryLMDataInteger = getDatabaseHelperMain().getDaoTempHistoryLMData();
            daoTempHistoryMarkerDataInteger = getDatabaseHelperMain().getDaoTempHistoryMarkerData();

            listFixedTimeTableData = daoFixedTimeTableDataInteger.queryForAll();
            listMarkerData = daoMarkerDataInteger.queryForAll();

            //query 조건 필요
            //listTempHistoryData = daoTempHistoryDataInteger.queryForAll();
            //listHistoryData = daoHistoryDataInteger.queryForAll();

            //DateForTempHisoryData -> 직접 query 사용


            // check if this week is already calculated
            if(!isAlreadyCalculated()){
                Log.d("ddraw", "isAlreadyCalculated");
                doCalcTempHistoryData();   //create tempHistoryData from locationMem
            }

            //listHistoryData = daoHistoryDataInteger.query with weekFirstTimeLong


            changeWeekWithFirstWeekLong();

            calenderWeekAdapter = new CalenderWeekAdapter(curContext, listHistoryData, listTempHistoryData);  //adapter create
            calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);

        }
        catch(SQLException e){
            Log.d("TimetableWeekF", "getDaoFixedTimeTableData SQL Exception");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_calender_week, container, false);        //fragment에 해당하는 retView

        //button, text
        prevButton = (Button)retView.findViewById(R.id.fragment_calender_week_prev_button);
        nextButton = (Button)retView.findViewById(R.id.fragment_calender_week_next_button);
        refreshButton = (Button)retView.findViewById(R.id.fragment_calender_week_refresh_button);
        centerText = (TextView)retView.findViewById(R.id.fragment_timetable_week_textMonth);

        weekGridview = (View)(retView.findViewById(R.id.calenderWeekView));
        customWeekView = (CalenderWeekView) weekGridview;

        //adapter View에 등록
        customWeekView.setCustomWeekAdapter(calenderWeekAdapter);

        //Move Month button
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekFirstTimeLong = weekFirstTimeLong - LONG_DAY_MILLIS * 7;
                changeWeekWithFirstWeekLong();
                //calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
                calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
                customWeekView.invalidate();
            }
        });

        //add button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekFirstTimeLong = weekFirstTimeLong + LONG_DAY_MILLIS * 7;
                changeWeekWithFirstWeekLong();
                calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
                customWeekView.invalidate();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //weekFirstTimeLong = weekFirstTimeLong + LONG_DAY_MILLIS * 7;
                doCalcTempHistoryData();
                changeWeekWithFirstWeekLong();
                calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
                customWeekView.invalidate();
            }
        });



        return retView;
    }

    private void setCenterText(){
        /*curMonth = timetableWeekAdapter.getCurMonth();
        curYear = timetableWeekAdapter.getCurYear();
        centerText.setText(curYear + " " + (curMonth+1));*/
    }

    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        if (context instanceof CalenderWeekFragmentListener) {
            calenderWeekFragmentListener = (CalenderWeekFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CalenderWeekFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //DB
    private DatabaseHelperMain databaseHelperMain = null;


    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(super.getContext());
        }
        return databaseHelperMain;
    }

    private DatabaseHelperLocationMemory databaseHelperLocationMemory = null;

    private DatabaseHelperLocationMemory getDatabaseHelperLocationMemory() {
        if (databaseHelperLocationMemory == null) {
            databaseHelperLocationMemory = DatabaseHelperLocationMemory.getHelper(curContext);
        }
        return databaseHelperLocationMemory;
    }



    //open dialogWeekItemViewFragment

    void openDialogWithIdx(CalenderWeekItemIdxWithIsHistoryData idxWithIsHistoryData){
        this.idxWithIsHistoryData = idxWithIsHistoryData;
        int iIdx = idxWithIsHistoryData.getIdx();
        if(iIdx == -1){
            return;
        }

        if(idxWithIsHistoryData.isHistory()){
            //open history dialog
            dialogViewCalenderItemFragment = new DialogViewCalenderItemFragment();

            Log.d("ddraw", "openDialogWithIdxopenDialogWithIdx : " + listHistoryData.get(iIdx).getlStartTime());

            bundleArg = new Bundle();
            bundleArg.putString("title", listHistoryData.get(iIdx).getForeFixedTimeTable().getStrFixedTimeTableName());
            bundleArg.putLong("starttime", listHistoryData.get(iIdx).getlStartTime());
            bundleArg.putLong("endtime", listHistoryData.get(iIdx).getlEndTime());
            bundleArg.putString("marker", listHistoryData.get(iIdx).getForeMarkerData().getStrMarkerName());
            bundleArg.putString("memo", listHistoryData.get(iIdx).getMemo());
            dialogViewCalenderItemFragment.setArguments(bundleArg);
            dialogViewCalenderItemFragment.setTargetFragment(this, 0);
            dialogViewCalenderItemFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogViewCalenderItemFragment");
        }
        else{
            //open temp history dialog



            dialogViewCalenderTempItemFragment = new DialogViewCalenderTempItemFragment();

            bundleArg = new Bundle();

            originFixedTimeTableData = listTempHistoryData.get(iIdx).getForeFixedTimeTable();
            originMarkerData = listTempHistoryData.get(iIdx).getForeMarkerData();

            Log.d("ddraw", "openDialogWithIdxopenDialogWithIdx : " + listTempHistoryData.get(iIdx).getlStartTime());

            bundleArg.putInt("fixedTimeTableIdx", listFixedTimeTableData.indexOf(listTempHistoryData.get(iIdx).getForeFixedTimeTable()));
            bundleArg.putLong("starttime", listTempHistoryData.get(iIdx).getlStartTime());
            bundleArg.putLong("endtime", listTempHistoryData.get(iIdx).getlEndTime());
            bundleArg.putInt("markerIdx", listMarkerData.indexOf(listTempHistoryData.get(iIdx).getForeMarkerData()));
            bundleArg.putString("memo", listTempHistoryData.get(iIdx).getMemo());
            dialogViewCalenderTempItemFragment.setArguments(bundleArg);
            dialogViewCalenderTempItemFragment.setTargetFragment(this, 0);
            dialogViewCalenderTempItemFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogViewCalenderTempItemFragment");
        }

    }



    public boolean isAlreadyCalculated(){
        try{
            if(0 == lookupDateForTempHistoryData(weekFirstTimeLong).size()){
                return false;
            }
        }
        catch(SQLException e){
            Log.d("cwFragment", "isAlreadyCalculated() SQL Exception");
        }
        return true;
    }
    //locationmem -> 계산 -> create tempHistoryData & list
    public void doCalcTempHistoryData(){
        //use daoDateForTempHistoryData, weekFirstTimeLong
        //계산 뒤 DateForTempHisoryData에 weekFirstTimeLong추가

        // delete listLocationGroup
        List<LocationGroup> listLocationGroup = new ArrayList<>();

        int ret = 0;
        //calc ~~ weekFirstTimeLong + 3day
        curTimeLong = System.currentTimeMillis();
        Log.d("ddraw", "weekFirstTimeLong:" + weekFirstTimeLong);

        if(curTimeLong < weekFirstTimeLong){
            Log.d("ddraw", "doCalcTempHistoryData - do nothing");
            //do nothing
        }
        else if(curTimeLong < weekFirstTimeLong + 3 * LONG_DAY_MILLIS){      //현재 시간(쿼리 대상)이 중간 분기점보다 이전인 경우 [alpha~curTime] & recursively
            ret = createLocatoinGrouppingUpperSide(curTimeLong, listLocationGroup);
            if(ret == -1){
                Log.d("ddraw", "doCalcTempHistoryData - elseif no list in [starttime-endtime]");
                return; //no list in [starttime-endtime]
            }
            else if(ret == -2){
                Log.d("ddraw", "createLocatoinGrouppingUpperSide -2 ERROR");
            }
        }
        else{
            //현재 시간(쿼리 대상)이 중간 분기점 이후인 경우 [alpha ~ weekFirstTimeLong + 3 * LONG_DAY_MILLIS)
            ret = createLocatoinGrouppingUpperSide(weekFirstTimeLong + 3 * LONG_DAY_MILLIS, listLocationGroup);
            Log.d("ddraw", "doCalcTempHistoryData createLocatoinGrouppingUpperSide(weekFirstTimeLong + 3 * LONG_DAY_MILLIS, listLocationGroup);");
            if(ret == -2){
                Log.d("ddraw", "createLocatoinGrouppingUpperSide -2 ERROR");
            }

            if(curTimeLong < weekFirstTimeLong + 7 * LONG_DAY_MILLIS) {  // [weekFirstTimeLong + 3 * LONG_DAY_MILLIS ~ curTimeLong)
                ret = createLocatoinGrouppingDownSide(curTimeLong, listLocationGroup);
                Log.d("ddraw", "doCalcTempHistoryData createLocatoinGrouppingDownSide(curTimeLong, listLocationGroup);");
                if(ret == -2){
                    Log.d("calender", "createLocatoinGrouppingUpperSide -2 ERROR");
                }
            }
            else{   // [weekFirstTimeLong + 3 * LONG_DAY_MILLIS ~ weekFirstTimeLong + 7 * LONG_DAY_MILLIS) & recursively
                ret = createLocatoinGrouppingDownSide(weekFirstTimeLong + 7 * LONG_DAY_MILLIS, listLocationGroup);
                Log.d("ddraw", "doCalcTempHistoryData createLocatoinGrouppingDownSide(weekFirstTimeLong + 7 * LONG_DAY_MILLIS, listLocationGroup);");
                if(ret == -2){
                    Log.d("calender", "createLocatoinGrouppingUpperSide -2 ERROR");
                }
            }

        }
        // 마지막 group 제외
        //listLocationGroup setting targetMarkerData
        Log.d("ddraw", "---locationGroup size : " + listLocationGroup.size());
        for(int i = 0; i < listLocationGroup.size(); i++){
            double minDist = 100000.0;
            boolean thereIsInCricle = false;
            for(int k=0; k<listMarkerData.size(); k++){
                double dist = getPowRadius(listLocationGroup.get(i).getCenterLat(), listLocationGroup.get(i).getCenterLng(), listMarkerData.get(k).getLat(), listMarkerData.get(k).getLng());
                if(thereIsInCricle == true){
                    if(dist <= listMarkerData.get(k).getDInnerRadius()){
                        listLocationGroup.get(i).getListInnerMarkerData().add(listMarkerData.get(k));   //inner markerData add
                        if(minDist > dist){
                            listLocationGroup.get(i).setTargetMarkerData(listMarkerData.get(k));    //set target markerData with min value
                            minDist = dist;
                        }
                        //minDist = minDist
                    }
                }
                else{
                    if(dist <= listMarkerData.get(k).getDInnerRadius()){
                        thereIsInCricle = true;
                        listLocationGroup.get(i).getListInnerMarkerData().add(listMarkerData.get(k));   //inner markerData add
                        listLocationGroup.get(i).setTargetMarkerData(listMarkerData.get(k));        //set target markerData
                        minDist = dist;
                    }
                    else{   //not inner radius
                        if(minDist > dist - listMarkerData.get(k).getDInnerRadius()){
                            listLocationGroup.get(i).setTargetMarkerData(listMarkerData.get(k));        //set target markerData
                            minDist = dist - listMarkerData.get(k).getDInnerRadius();
                        }
                        //minDist = minDist;
                    }
                }
            }
            listLocationGroup.get(i).setMinDIst(minDist);
            //TODO : mindist is too large & no inner circle marker, then targetMarkerData => null;

        }

        /*
        변경점 : LocationGroup을 targetMarkerData만으로 하지 않음.
        List<LocationGroup> tempListLocationGroup = new ArrayList<>();
        tempListLocationGroup.add(listLocationGroup.get(0));
        int cntSameMarkerData= 0;
        int k = 0;
        LocationGroup templg = tempListLocationGroup.get(k);    //이전 group
        k++;
        for(int i=1; i<listLocationGroup.size(); i++){
            LocationGroup curLocationGroup = listLocationGroup.get(i);
            //같은 marker, 가까운 시간 내인 경우 같은 group
            if(curLocationGroup.getTargetMarkerData().equals(templg.getTargetMarkerData()) && (templg.getListLMD().get(templg.getListLMD().size()-1).getlMillisTimeWritten() - curLocationGroup.getListLMD().get(0).getlMillisTimeWritten() < LONG_GROUP_INTERVAL)){
                //combine
                cntSameMarkerData++;
                //LocationGroup templg = tempListLocationGroup.get(k);    //이전 group
                templg.setCenterLat(templg.getCenterLat()*cntSameMarkerData/(cntSameMarkerData+1) + curLocationGroup.getCenterLat()/(cntSameMarkerData+1));
                templg.setCenterLng(templg.getCenterLng()*cntSameMarkerData/(cntSameMarkerData+1) + curLocationGroup.getCenterLng()/(cntSameMarkerData+1));
                templg.getListInnerMarkerData().addAll(curLocationGroup.getListInnerMarkerData());
                templg.getListLMD().addAll(curLocationGroup.getListLMD());
            }
            else{       //이전과 다른 그룹
            */
                //k index에 대한 in out 판단. out 이면 ListInnerMarkerData -> empty  :: 변경- 하지않음.
                /*if(!isInnerRangeDot(templg.getCenterLat(), templg.getCenterLng(), templg.getTargetMarkerData().getLat(),templg.getTargetMarkerData().getLng(), templg.getTargetMarkerData().getDInnerRadius())){
                    tempListLocationGroup.get(k).getListInnerMarkerData().clear();
                }*/
                /*
                tempListLocationGroup.add(listLocationGroup.get(i));
                templg = tempListLocationGroup.get(k);    //이전 group
                k++;
            }
        }
        listLocationGroup = tempListLocationGroup;  //바뀐 group으로 전환.
        */

        //listLocationGroup -> fixedTimeTable 판단(분해 판단 포함) 및 tempHistoryData 생성

        List<TempHistoryData> listNewTempHistoryData = new ArrayList<TempHistoryData>();
        TempHistoryData lastTempHistoryData = null;

        List<List<LocationMemoryData>> locationMemoryDataListList = new ArrayList<List<LocationMemoryData>>();
        List<LocationMemoryData> lastLocationMemoryDataList = null;

        List<Set<MarkerData>> markerDataSetList = new ArrayList<Set<MarkerData>>();
        Set<MarkerData> lastMarkerDataSet = null;


        Log.d("ddraw", "-----locationGroup size : " + listLocationGroup.size());
        for(int i=0, ii = listLocationGroup.size(); i<ii; i++){
            LocationGroup curLocationGroup = listLocationGroup.get(i);
            long firstTimeOfGroup = curLocationGroup.getFirstTimeOfGroup();
            long lastTimeOfGroup = curLocationGroup.getLastTimeOfGroup();
            // Use (times+LONG_DAY_MILLIS*3)%LONG_WEEK_MILLIS
            firstTimeOfGroup = (firstTimeOfGroup+LONG_DAY_MILLIS*3)%LONG_WEEK_MILLIS;
            lastTimeOfGroup = (lastTimeOfGroup+LONG_DAY_MILLIS*3)%LONG_WEEK_MILLIS;
            if(lastTimeOfGroup < firstTimeOfGroup){
                lastTimeOfGroup += LONG_DAY_MILLIS*7;
            }

            MarkerData targetMarkerData = curLocationGroup.getTargetMarkerData();

            List<FixedTimeTableData> listFixedTimeTableDataOfTargetMarker = null;
            FixedTimeTableData targetFixedTimeTableData = null;

            //targetMarkerData가 존재 한다면
            if(targetMarkerData != null){
                try{
                    //targetMarker에 해당하는 timetable 가져오기
                    listFixedTimeTableDataOfTargetMarker = getListLocationMemForInnerTimeQuery(targetMarkerData);
                }
                catch(SQLException e){
                    Log.d("calender", "getListLocationMemForInnerTimeQuery exception");
                }
                //targetMarker에 해당하는 timetable이 존재한다면,
                if(listFixedTimeTableDataOfTargetMarker != null) {
                    for (int p = 0, pp = listFixedTimeTableDataOfTargetMarker.size(); p < pp; p++) {
                        FixedTimeTableData tempFTTD = listFixedTimeTableData.get(p);
                        //TODO : 더 넓은 범위의 matching 필요.. 현재 : fixed timetable에 걸치는 경우
                        if ((tempFTTD.getlInnerBoundStartTime() <= firstTimeOfGroup && firstTimeOfGroup <= tempFTTD.getlInnerBoundEndTime()) || (tempFTTD.getlInnerBoundStartTime() <= lastTimeOfGroup && lastTimeOfGroup <= tempFTTD.getlInnerBoundEndTime())) {
                            targetFixedTimeTableData = tempFTTD;
                            break;
                        }
                    }
                }
            }
            //이전 tempHistoryData가 존재하면
            if(lastTempHistoryData != null){
                //이전 temp와 비교하여 둘다 null이 아니고 같다면 기존 tempHistoryData에 추가.
                if(targetFixedTimeTableData != null && lastTempHistoryData.getForeFixedTimeTable() != null && lastTempHistoryData.getForeFixedTimeTable().equals(targetFixedTimeTableData)){
                    lastTempHistoryData.setlEndTime(curLocationGroup.getLastTimeOfGroup()); // endtime으로 연장
                    for(int loop = 0, lloop = curLocationGroup.getListLMD().size(); loop < lloop; loop++){
                        lastLocationMemoryDataList.add(curLocationGroup.getListLMD().get(loop));
                    }

                    for(int loop = 0, lloop = curLocationGroup.getListInnerMarkerData().size(); loop < lloop; loop++){
                        lastMarkerDataSet.add(curLocationGroup.getListInnerMarkerData().get(loop));
                    }
                }
                else{
                    //같지 않으면 새로운 tempHistoryData 추가
                    lastTempHistoryData = new TempHistoryData(targetFixedTimeTableData, targetMarkerData, curLocationGroup.getFirstTimeOfGroup(), curLocationGroup.getLastTimeOfGroup(), "");
                    lastLocationMemoryDataList = new ArrayList<LocationMemoryData>();
                    lastMarkerDataSet = new HashSet<MarkerData>();

                    listNewTempHistoryData.add(lastTempHistoryData);
                    locationMemoryDataListList.add(lastLocationMemoryDataList);
                    markerDataSetList.add(lastMarkerDataSet);   //inner markerData

                    for(int loop = 0, lloop = curLocationGroup.getListLMD().size(); loop < lloop; loop++){
                        lastLocationMemoryDataList.add(curLocationGroup.getListLMD().get(loop));
                    }

                    for(int loop = 0, lloop = curLocationGroup.getListInnerMarkerData().size(); loop < lloop; loop++){
                        lastMarkerDataSet.add(curLocationGroup.getListInnerMarkerData().get(loop));
                    }
                }
            }
            else{
                //이전 tempHistoryData가 없으면 새로운 tempHistoryData 추가
                lastTempHistoryData = new TempHistoryData(targetFixedTimeTableData, targetMarkerData, curLocationGroup.getFirstTimeOfGroup(), curLocationGroup.getLastTimeOfGroup(), "");
                lastLocationMemoryDataList = new ArrayList<LocationMemoryData>();
                lastMarkerDataSet = new HashSet<MarkerData>();

                listNewTempHistoryData.add(lastTempHistoryData);
                locationMemoryDataListList.add(lastLocationMemoryDataList);
                markerDataSetList.add(lastMarkerDataSet);   //inner markerData

                for(int loop = 0, lloop = curLocationGroup.getListLMD().size(); loop < lloop; loop++){
                    lastLocationMemoryDataList.add(curLocationGroup.getListLMD().get(loop));
                }

                for(int loop = 0, lloop = curLocationGroup.getListInnerMarkerData().size(); loop < lloop; loop++){
                    lastMarkerDataSet.add(curLocationGroup.getListInnerMarkerData().get(loop));
                }
            }



        }

        //기존의 tempHistoryData를 지우며 그와 관련된 data도 지움. ( == refresh)
        long delStartTime = 0;
        long delEndTime = 0;

        if(locationMemoryDataListList == null || locationMemoryDataListList.size() == 0){
            delStartTime = weekFirstTimeLong;
            delEndTime = weekFirstTimeLong + LONG_WEEK_MILLIS;
        }
        else{
            delStartTime = locationMemoryDataListList.get(0).get(0).getlMillisTimeWritten();
            delStartTime = minL(delStartTime, weekFirstTimeLong);

            List<LocationMemoryData> tempLastList = locationMemoryDataListList.get(locationMemoryDataListList.size()-1);
            if(tempLastList.size() == 0){
                delStartTime = minL(delStartTime, weekFirstTimeLong);
            }
            else{
                delEndTime = tempLastList.get(tempLastList.size()-1).getlMillisTimeWritten();
                delEndTime = maxL(delEndTime, weekFirstTimeLong + LONG_WEEK_MILLIS);
            }
        }

        List<TempHistoryData> delTargetTempHistoryDataList = null;
        //get tempHistoryData list from query
        try{
            delTargetTempHistoryDataList = getListTempHistoryDatacContainStartEndTimeQuery(delStartTime, delEndTime);

            //del tempHIstoryLMData, tempHistoryMarkerData with tempHistoryData
            //deleteMarkersForMarkerType deleteMarkersForMarkerType
            //del tempHIstoryData
            for(int i=0, ii = delTargetTempHistoryDataList.size(); i<ii; i++){
                TempHistoryData delTempHistoryData = delTargetTempHistoryDataList.get(i);
                deleteLMDatasForTempHistoryLMData(delTempHistoryData);
                deleteMarkerDatasForTempHistoryLMData(delTempHistoryData);
                daoTempHistoryDataInteger.delete(delTempHistoryData);
            }
        }
        catch(SQLException e){

            Log.d("calender", "delTargetTempHistoryDataList sqlException : " + e.toString());
        }

        //tempHistoryData 새로 추가
        Log.d("ddraw", "listNewTempHistoryData size : " + listNewTempHistoryData.size());
        for(int i=0, ii = listNewTempHistoryData.size()-1; i<ii; i++){
            try{
                Log.d("ddraw", "listNewTempHistoryData size : " + listNewTempHistoryData.get(i).toString());
                //Log.d("ddraw", "listNewTempHistoryData info : " + listNewTempHistoryData.get(i).toString());
                daoTempHistoryDataInteger.create(listNewTempHistoryData.get(i));

                List<LocationMemoryData> tempLocationMemList = locationMemoryDataListList.get(i);
                for(int loop=0, lloop = tempLocationMemList.size(); loop<lloop; loop++){
                    //temphistory에 해당하는 locationMemoryData들
                    daoTempHistoryLMDataInteger.create(new TempHistoryLMData(listNewTempHistoryData.get(i), tempLocationMemList.get(loop)));
                }

                List<MarkerData> tempMarkerDataList = new ArrayList<MarkerData>(markerDataSetList.get(i));
                for(int loop=0, lloop = tempMarkerDataList.size(); loop<lloop; loop++){
                    //temphistory에 해당하는 Inner MarkerData들
                    daoTempHistoryMarkerDataInteger.create(new TempHistoryMarkerData(listNewTempHistoryData.get(i), tempMarkerDataList.get(loop)));
                }

            }
            catch(SQLException e){
                Log.d("calender", "daoTempHistoryDataInteger.create(listNewTempHistoryData.get(i)) error");
            }
        }
        listTempHistoryData = listNewTempHistoryData;   //listTempHistoryData 교체

    }

    int createLocatoinGrouppingUpperSide(long endTime, List<LocationGroup> listLocationGroup){
        Log.d("ddraw", "createLocatoinGrouppingUpperSide() : " + endTime);
        //grouping & check before part recursively
        long alpha = weekFirstTimeLong;
        double groupCenterLat = 0.0, groupCenterLng = 0.0;
        boolean extensionStart = false;
        List<LocationMemoryData> listLocationMemWithTime = null;
        //int cnt = 0;
        try{
            listLocationMemWithTime = getListLocationMemForInnerTimeQuery(alpha, endTime);
        }
        catch(SQLException e){
            Log.d("calender", "createLocatoinGrouppingUpperSide sql error : " + e.toString());
        }
        if(listLocationMemWithTime == null || listLocationMemWithTime.size() == 0){
            return -1;
        }
        Collections.reverse(listLocationMemWithTime);

        int i = 0;   //반복문 시작 index (getBindedHistoryData() != null인 경우 제외를 위해)
        int iLoopEnd = listLocationMemWithTime.size();
        int startGroupIdx = 0;  //group에서 시작 index

        while(i< iLoopEnd) {
            if(listLocationMemWithTime.get(i).getBindedHistoryData() == null &&listLocationMemWithTime.get(i).getbDummy() != -1){   //HistoryData에 binding 안되있고 사용 가능한(not deep deleted) location mem인 경우
                groupCenterLat = listLocationMemWithTime.get(i).getLat();
                groupCenterLng = listLocationMemWithTime.get(i).getLng();
                startGroupIdx = i;
                i++;
                break;
            }
            i++;
        }

        boolean doGrouping = false;

        for(; i< iLoopEnd; i++){
            if(listLocationMemWithTime.get(i).getBindedHistoryData() != null || listLocationMemWithTime.get(i).getbDummy() == -1){  //이미 HistoryData에 binding 되어있거나 사용 불가능한 경우
                doGrouping = true;
            }
            else{
                groupCenterLat = groupCenterLat*i/(i+1) + listLocationMemWithTime.get(i).getLat()/(i+1);
                groupCenterLng = groupCenterLng*i/(i+1) + listLocationMemWithTime.get(i).getLng()/(i+1);
                for(int k = startGroupIdx; k <= i; k++){
                    if(!isInnerRangeDot(groupCenterLat, groupCenterLng, listLocationMemWithTime.get(k).getLat(),listLocationMemWithTime.get(k).getLng(), DOUBLE_GROUPING_2POW_RADIUS)){
                        //do Grouping
                        doGrouping = true;
                        break;
                    }
                    //check isInnerRangeDot continue
                }
            }

            if(doGrouping == true){
                if(startGroupIdx - (i-1) + 1 != 1){ //group 개수가 1이 아닌 경우 (2개 이상일 시 grouoping)
                    // group startGroupIdx ~ (i-1) with reverse
                    List<LocationMemoryData> locationMemGroup = new ArrayList<>();
                    for(int idx = i-1; idx >= startGroupIdx; idx--){
                        locationMemGroup.add(listLocationMemWithTime.get(idx));
                        //Log.d("ddraw", "reverse time:" + listLocationMemWithTime.get(idx).getlMillisTimeWritten());
                    }

                    LocationGroup lg = new LocationGroup(locationMemGroup, groupCenterLat, groupCenterLng, null, null, new ArrayList<MarkerData>());
                    listLocationGroup.add(lg);
                }
                while(i< iLoopEnd) {
                    if(listLocationMemWithTime.get(i).getBindedHistoryData() == null &&listLocationMemWithTime.get(i).getbDummy() != -1){   //HistoryData에 binding 안되있고 사용 가능한(not deep deleted) location mem인 경우
                        groupCenterLat = listLocationMemWithTime.get(i).getLat();
                        groupCenterLng = listLocationMemWithTime.get(i).getLng();
                        startGroupIdx = i;
                        //i++;  increment at for statement
                        break;
                    }
                    i++;
                }

                doGrouping = false;
                if(extensionStart == true){
                    Collections.reverse(listLocationGroup);
                    return 0;
                }
            }

            if(i == listLocationMemWithTime.size()-1){
                List<LocationMemoryData> listMoreLocationMemWithTime = null;
                try{
                    listMoreLocationMemWithTime = getListLocationMemForInnerTimeQuery(alpha-LONG_DAY_MILLIS, alpha-1);
                }
                catch(SQLException e){
                    Log.d("calender", "createLocatoinGrouppingUpperSide sql error : " + e.toString());
                }
                if(listMoreLocationMemWithTime == null || listMoreLocationMemWithTime.size() == 0){
                    Collections.reverse(listLocationGroup);
                    return 0;
                }
                alpha = alpha-LONG_DAY_MILLIS;
                Collections.reverse(listMoreLocationMemWithTime);
                listLocationMemWithTime.addAll(listMoreLocationMemWithTime);
                iLoopEnd += listMoreLocationMemWithTime.size();
                extensionStart = true;
            }
        }
        //reverse group
        return -2;
    }

    int createLocatoinGrouppingDownSide(long endTime, List<LocationGroup> listLocationGroup){
        Log.d("ddraw", "createLocatoinGrouppingDownSide() : " + endTime);
        //grouping & check before part recursively
        long alpha = weekFirstTimeLong + 3 * LONG_DAY_MILLIS;
        double groupCenterLat = 0.0, groupCenterLng = 0.0;

        boolean extensionStart = false;
        //int cnt = 0;
        List<LocationMemoryData> listLocationMemWithTime = null;
        try{
            listLocationMemWithTime = getListLocationMemForInnerTimeQuery(alpha, endTime);
        }
        catch(SQLException e){
            Log.d("calender", "createLocatoinGrouppingUpperSide sql error : " + e.toString());
        }
        //Collections.reverse(listLocationMemWithTime);
        if(listLocationMemWithTime == null || listLocationMemWithTime.size() == 0){
            return -1;
        }

        int startGroupIdx = 0;  //group의 start
        int i = 0;  //반복문의 start index
        int iLoopEnd = listLocationMemWithTime.size();
        while(i< iLoopEnd) {
            if(listLocationMemWithTime.get(i).getBindedHistoryData() == null &&listLocationMemWithTime.get(i).getbDummy() != -1){   //HistoryData에 binding 안되있고 사용 가능한(not deep deleted) location mem인 경우
                groupCenterLat = listLocationMemWithTime.get(i).getLat();
                groupCenterLng = listLocationMemWithTime.get(i).getLng();
                startGroupIdx = i;
                i++;
                break;
            }
            i++;
        }



        boolean doGrouping = false;

        for(; i<iLoopEnd; i++){
            if(listLocationMemWithTime.get(i).getBindedHistoryData() != null || listLocationMemWithTime.get(i).getbDummy() == -1){  //이미 HistoryData에 binding 되어있거나 사용 불가능한 경우
                doGrouping = true;
            }
            else{
                groupCenterLat = groupCenterLat*i/(i+1) + listLocationMemWithTime.get(i).getLat()/(i+1);
                groupCenterLng = groupCenterLng*i/(i+1) + listLocationMemWithTime.get(i).getLng()/(i+1);
                for(int k = startGroupIdx; k <= i; k++){
                    if(!isInnerRangeDot(groupCenterLat, groupCenterLng, listLocationMemWithTime.get(k).getLat(),listLocationMemWithTime.get(k).getLng(), DOUBLE_GROUPING_2POW_RADIUS)){
                        doGrouping = true;
                        break;
                    }
                    //check isInnerRangeDot continue
                }
            }

            if(doGrouping == true){
                if(startGroupIdx - (i-1) + 1 != 1){ //group 개수가 1이 아닌 경우 (2개 이상일 시 grouoping)
                    // group startGroupIdx ~ (i-1) with No reverse
                    List<LocationMemoryData> locationMemGroup = new ArrayList<>();
                    for(int idx = startGroupIdx ; idx < i; idx++){
                        locationMemGroup.add(listLocationMemWithTime.get(idx));
                    }
                    LocationGroup lg = new LocationGroup(locationMemGroup, groupCenterLat, groupCenterLng, null, null, new ArrayList<MarkerData>());
                    listLocationGroup.add(lg);
                }

                while(i< iLoopEnd) {
                    if(listLocationMemWithTime.get(i).getBindedHistoryData() == null &&listLocationMemWithTime.get(i).getbDummy() != -1){   //HistoryData에 binding 안되있고 사용 가능한(not deep deleted) location mem인 경우
                        groupCenterLat = listLocationMemWithTime.get(i).getLat();
                        groupCenterLng = listLocationMemWithTime.get(i).getLng();
                        startGroupIdx = i;
                        //i++;  increment at for statement
                        break;
                    }
                    i++;
                }

                doGrouping = false;
                if(extensionStart == true){
                    return 0;
                }
            }

            if(i == listLocationMemWithTime.size()-1){
                List<LocationMemoryData> listMoreLocationMemWithTime = null;
                try{
                    listMoreLocationMemWithTime = getListLocationMemForInnerTimeQuery(endTime + 1, endTime+LONG_DAY_MILLIS);
                }
                catch(SQLException e){
                    Log.d("calender", "createLocatoinGrouppingUpperSide sql error : " + e.toString());
                }
                if(listMoreLocationMemWithTime == null || listMoreLocationMemWithTime.size() == 0){
                    return 0;
                }
                endTime = endTime+LONG_DAY_MILLIS;
                //Collections.reverse(listMoreLocationMemWithTime);
                listLocationMemWithTime.addAll(listMoreLocationMemWithTime);
                iLoopEnd += listMoreLocationMemWithTime.size();
                extensionStart = true;
            }
        }
        //No reverse group
        return -2;  //error
    }

    boolean isInnerRangeDot(double cx, double cy, double d1, double d2, double range){
        return (cx - d1)*(cx - d1) + (cy - d2)*(cy - d2) < range;
    }

    double getPowRadius(double cx, double cy, double d1, double d2 ){
        return (cx - d1)*(cx - d1) + (cy - d2)*(cy - d2);
    }

    double minD(double d1, double d2){
        if(d1 < d2){
            return d1;
        }
        return d2;
    }

    long minL(long d1, long d2){
        if(d1 < d2){
            return d1;
        }
        return d2;
    }

    long maxL(long d1, long d2){
        if(d1 > d2){
            return d1;
        }
        return d2;
    }

    public void changeWeekWithFirstWeekLong(){
        if(calenderWeekAdapter != null){
            calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
        }
        if(isAlreadyCalculated()){
            doCalcTempHistoryData();   //create tempHistoryData from locationMem
        }
        //listHistoryData = daoHistoryDataInteger.query with weekFirstTimeLong

    }


    //Week first time check query
    private PreparedQuery<DateForTempHisoryData> dateForTempHisoryDataQuery = null;
    //param longTime에 해당하는 DateForTempHistoryData 가져오는 쿼리 실행문.
    private List<DateForTempHisoryData> lookupDateForTempHistoryData(long inTime) throws SQLException {
        if (dateForTempHisoryDataQuery == null) {
            dateForTempHisoryDataQuery = makeDateForTempHisoryDataQuery();
        }
        dateForTempHisoryDataQuery.setArgumentHolderValue(0, inTime);    //아래의 selectArg에 markerData 해당됌.
        return daoDateForTempHistoryData.query(dateForTempHisoryDataQuery);
    }
    //longtime에 해당하는 DateForTempHisoryData 가져오는 쿼리문.
    private PreparedQuery<DateForTempHisoryData> makeDateForTempHisoryDataQuery() throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<DateForTempHisoryData, Integer> markerMarkerTypeQb = daoDateForTempHistoryData.queryBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        //markerMarkerTypeQb.selectColumns(DateForTempHisoryData.DATEFORTEMPHISTORYDATA_ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        markerMarkerTypeQb.where().eq(DateForTempHisoryData.DATEFORTEMPHISTORYDATA_LONGWEEK_FIELD_NAME, selectArg);
        return markerMarkerTypeQb.prepare();
    }


    //history
    @Override
    public void doSlightDelete() {
        HistoryData delHD = listHistoryData.get(idxWithIsHistoryData.getIdx());
        try{
            slightDeleteWithHistoryData(delHD);
            listHistoryData.remove(delHD);
            daoHistoryDataInteger.delete(delHD);
        }
        catch(SQLException e){
            Log.d("calender", "doSlightDelete SQLException");
        }
    }

    //tempHistory
    @Override
    public void doSave(FixedTimeTableData selectedFttd, long startTime, long endTime, MarkerData selectedMd, String memo) {
        //TODO : no size, no bind case
        //originFixedTimeTableData originMarkerData -> selectedFttd, selectedMd
        TempHistoryData tempTHD = listTempHistoryData.get(idxWithIsHistoryData.getIdx());
        MarkerData minDistMarkerData = tempTHD.getForeMarkerData();
        FixedTimeTableData minDIstFixedTimeTableData = tempTHD.getForeFixedTimeTable();
        List<MarkerData> innerMarkerDataList = null;

        //add historyData
        HistoryData retHD = new HistoryData(selectedFttd, selectedMd, startTime, endTime, memo, minDistMarkerData);

        try{
            daoHistoryDataInteger.create(retHD);
            listHistoryData.add(retHD);

            /*
            Set<LocationMemoryData> targetLocSet = new HashSet<LocationMemoryData>();
            List <LocationMemoryData> targetLocList = queryLMDatasForTempHistoryLMData(tempTHD);
            for(LocationMemoryData lmd : targetLocList){
                targetLocSet.add(lmd);
            }
            */

            //locationMemData : tempHistoryData->null,-1
            // time range : historyData->retHD, dummy->true/false
            //makeNullTempHistoryDataInLocationMem(tempTHD);  //locationMemData's tempHistoryData -> null, dummy to -1
            List<LocationMemoryData> locList = getListLocationMemForInnerTimeQuery(startTime, endTime); //단순히 시간 내의 LM을 모두 구한다.
            for(LocationMemoryData lmd : locList){
                lmd.setBindedHistoryData(retHD);
                lmd.setBindedTempHistoryData(null);
                lmd.setbDummy(0);   //target
                daoLocationMemoryDataInteger.update(lmd);
                /*
                if(targetLocSet.contains(lmd)){
                    lmd.setbDummy(0);   //target
                }
                else{
                    lmd.setbDummy(1);  //not target. tempHistoryData 생성에 제외시킨다. slightDelete인 경우 HistoryData에 해당하는 LM을 구하는 query를 통해 0으로 복구한다.
                }
                */

                /*
                if(lmd.getlMillisTimeWritten() < originFixedTimeTableData.getlStartTime()){
                    lmd.setbDummy(-1);
                }
                else if(lmd.getlMillisTimeWritten() >= originFixedTimeTableData.getlEndTime()){
                    lmd.setbDummy(1);
                }
                else{
                    lmd.setbDummy(0);
                }
                */
            }

            //Register InnerMarkerDataList
            innerMarkerDataList = queryMarkerDatasForTempHistoryLMData(tempTHD);
            for(MarkerData innerMD : innerMarkerDataList){
                daoHistoryDataInnerMarkerDataInteger.create(new HistoryDataInnerMarkerData(tempTHD, innerMD));
            }

            //TODO : 보정 with minDistMarkerData, innerMarkerDataList, selectedMd /// 선택된 selectedFttd, 선택한 loc time range{originStartTime, originEndTime}, 가장 근접하여 예측한 minDIstFixedTimeTableData => startTimeInc, endTimeInc
            if(){
                //markerdatas inner range +-
            }


            //Register original LocationMem timeRange Inc Dec(보정값 저장)
            List<LocationMemoryData> targetLMList = queryLMDatasForTempHistoryLMData(tempTHD);
            long originStartTime = targetLMList.get(0).getlMillisTimeWritten();
            long originEndTime = targetLMList.get(targetLMList.size()-1).getlMillisTimeWritten();
            long startTimeInc, endTimeInc;


            daoHistoryDataLocTimeRangeIncDecInteger.create(new HistoryDataLocTimeRangeIncDec(startTimeInc, endTimeInc));



            //delete with tempTHD
            deleteLMDatasForTempHistoryLMData(tempTHD);         // temphistory-lmData
            deleteMarkerDatasForTempHistoryLMData(tempTHD);     // temphistory-inner marker
            daoTempHistoryDataInteger.delete(tempTHD);      // temphistory

        }
        catch(SQLException e){
            Log.d("calender", "SQLEXCEPTION in doSave");
        }
    }
    @Override
    public void doDeepDelete() {
        //tempHistoryData -> null, dummy -> true
        TempHistoryData delTHD = listTempHistoryData.get(idxWithIsHistoryData.getIdx());
        try{
            deepDeleteWithTempHistoryData(delTHD);
            daoTempHistoryDataInteger.delete(delTHD);
            listTempHistoryData.remove(delTHD);
        }
        catch(SQLException e){
            Log.d("calender", "doDeepDelete SQLException");
        }

    }
    @Override
    public List<MarkerData> getListMarkerData() {
        return listMarkerData;
    }
    @Override
    public List<FixedTimeTableData> getListFixedTimeTableData() {
        return listFixedTimeTableData;
    }

    private PreparedUpdate<LocationMemoryData>locationMemForHistoryDataUpdateQuery = null;

    //HistoryData에 해당하는 locationMem의 HistoryDatabind = null로 만듬.
    private PreparedUpdate<LocationMemoryData> makeLocationMemForHistoryDataUpdateQuery() throws SQLException {
        // build our inner query for UserPost objects
        UpdateBuilder<LocationMemoryData, Integer> locationMemQb = daoLocationMemoryDataInteger.updateBuilder();

        SelectArg userSelectArg = new SelectArg();
        locationMemQb.where().eq(LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME, userSelectArg);
        locationMemQb.updateColumnValue(LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME, null);
        locationMemQb.updateColumnValue(LOCATIONMEMORY_BDUMMY_FIELD_NAME, 0);

        return locationMemQb.prepare();
    }

    private void slightDeleteWithHistoryData(HistoryData hd) throws SQLException{
        if(hd == null){
            return;
        }
        if(locationMemForHistoryDataUpdateQuery == null){
            locationMemForHistoryDataUpdateQuery = makeLocationMemForHistoryDataUpdateQuery();
        }

        locationMemForHistoryDataUpdateQuery.setArgumentHolderValue(0, hd);
        daoLocationMemoryDataInteger.update(locationMemForHistoryDataUpdateQuery);

    }

    private PreparedUpdate<LocationMemoryData>locationMemForTempHistoryDataUpdateQuery = null;
    private SelectArg boolSelectArg = new SelectArg();

    //TempHistoryData에 해당하는 locationMem의 TempHistoryDatabind = null로 만듬.
    private PreparedUpdate<LocationMemoryData> makeLocationMemForTempHistoryDataUpdateQuery() throws SQLException {
        // build our inner query for UserPost objects
        UpdateBuilder<LocationMemoryData, Integer> locationMemQb = daoLocationMemoryDataInteger.updateBuilder();

        SelectArg userSelectArg = new SelectArg();
        locationMemQb.where().eq(LOCATIONMEMORY_BINDEDHISTORYDATA_FIELD_NAME, userSelectArg);
        locationMemQb.updateColumnValue(LOCATIONMEMORY_BINDEDTEMPHISTORYDATA_FIELD_NAME, null);
        locationMemQb.updateColumnValue(LOCATIONMEMORY_BDUMMY_FIELD_NAME, boolSelectArg);

        return locationMemQb.prepare();
    }

    private void deepDeleteWithTempHistoryData(TempHistoryData thd) throws SQLException{
        if(thd == null){
            return;
        }
        if(locationMemForTempHistoryDataUpdateQuery == null){
            locationMemForTempHistoryDataUpdateQuery = makeLocationMemForTempHistoryDataUpdateQuery();
        }

        locationMemForTempHistoryDataUpdateQuery.setArgumentHolderValue(0, thd);
        boolSelectArg.setValue(true);
        daoLocationMemoryDataInteger.update(locationMemForTempHistoryDataUpdateQuery);

    }

    private void makeNullTempHistoryDataInLocationMem(TempHistoryData thd) throws SQLException{
        if(thd == null){
            return;
        }
        if(locationMemForTempHistoryDataUpdateQuery == null){
            locationMemForTempHistoryDataUpdateQuery = makeLocationMemForTempHistoryDataUpdateQuery();
        }

        locationMemForTempHistoryDataUpdateQuery.setArgumentHolderValue(0, thd);
        boolSelectArg.setValue(-1);
        daoLocationMemoryDataInteger.update(locationMemForTempHistoryDataUpdateQuery);

    }



    private PreparedQuery<LocationMemoryData>locationMemForInnerTimeQuery = null;
    SelectArg argStartTimeWithLoc = new SelectArg();
    SelectArg argEndTimeWithLoc = new SelectArg();

    //HistoryData에 해당하는 locationMem의 HistoryDatabind = null로 만듬.
    private PreparedQuery<LocationMemoryData> makeLocationMemForInnerTimeQueryUpdateQuery() throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<LocationMemoryData, Integer> locationMemQb = daoLocationMemoryDataInteger.queryBuilder();

        locationMemQb.where().ge(LOCATIONMEMORY_TIMEWRITTEN_FIELD_NAME, argStartTimeWithLoc).and().lt(LOCATIONMEMORY_TIMEWRITTEN_FIELD_NAME, argEndTimeWithLoc);

        return locationMemQb.prepare();
    }

    private List<LocationMemoryData> getListLocationMemForInnerTimeQuery(long sTime, long eTime) throws SQLException{
        if(locationMemForInnerTimeQuery == null){
            locationMemForInnerTimeQuery = makeLocationMemForInnerTimeQueryUpdateQuery();
        }
        argStartTimeWithLoc.setValue(sTime);
        argEndTimeWithLoc.setValue(eTime);

        return daoLocationMemoryDataInteger.query(locationMemForInnerTimeQuery);
    }


    private PreparedQuery<FixedTimeTableData> fixedTimeTableForTargetMarkerQuery = null;
    SelectArg argTargetMarkerData = new SelectArg();

    //targetMarkerData를 가지고있는 FixedTimeTable을 모두 가져온다.
    private PreparedQuery<FixedTimeTableData> makeFixedTimeTableForTargetMarkerQuery() throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<FixedTimeTableData, Integer> fixedTimeTableQb = daoFixedTimeTableDataInteger.queryBuilder();

        fixedTimeTableQb.where().eq(FIXEDTIMETABLE_MARKERDATA_FIELD_NAME, argTargetMarkerData);

        return fixedTimeTableQb.prepare();
    }

    private List<FixedTimeTableData> getListLocationMemForInnerTimeQuery(MarkerData markerData) throws SQLException{
        if(fixedTimeTableForTargetMarkerQuery == null){
            fixedTimeTableForTargetMarkerQuery = makeFixedTimeTableForTargetMarkerQuery();
        }
        argTargetMarkerData.setValue(markerData);

        return daoFixedTimeTableDataInteger.query(fixedTimeTableForTargetMarkerQuery);
    }


    private PreparedQuery<TempHistoryData>tempHistoryDatacContainStartEndTime = null;
    SelectArg argTempHistoryDatacContainStartTime = new SelectArg();
    SelectArg argTempHistoryDatacContainEndTime = new SelectArg();

    //startTime - endTime을 포함하는 모든 tempHistoryData를 반환
    private PreparedQuery<TempHistoryData> makeTempHistoryDataContainStartEndTimeQuery() throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<TempHistoryData, Integer> tempHistoryDataQb = daoTempHistoryDataInteger.queryBuilder();
        argTempHistoryDatacContainStartTime = new SelectArg();
        argTempHistoryDatacContainEndTime = new SelectArg();

        tempHistoryDataQb.where().le(TH_TEMPHISTORYDATA_STARTTIME_FIELD_NAME, argTempHistoryDatacContainEndTime).and().ge(TH_TEMPHISTORYDATA_ENDTIME_FIELD_NAME, argTempHistoryDatacContainStartTime);

        return tempHistoryDataQb.prepare();
    }

    private List<TempHistoryData> getListTempHistoryDatacContainStartEndTimeQuery(long sTime, long eTime) throws SQLException{
        if(tempHistoryDatacContainStartEndTime == null){
            tempHistoryDatacContainStartEndTime = makeTempHistoryDataContainStartEndTimeQuery();
        }
        argTempHistoryDatacContainStartTime.setValue(sTime);
        argTempHistoryDatacContainEndTime.setValue(eTime);

        return daoTempHistoryDataInteger.query(tempHistoryDatacContainStartEndTime);
    }

    //tempHistoryMarkerData table에서 param tempHistoryData에 해당하는 markerData들을 모두 삭제하는 쿼리 실행문.
    private PreparedDelete<TempHistoryMarkerData> tempHistoryMarkerDataForMarkerDeleteQuery = null;

    private void deleteMarkerDatasForTempHistoryLMData(TempHistoryData tempHistoryData) throws SQLException {
        if (tempHistoryMarkerDataForMarkerDeleteQuery == null) {
            tempHistoryMarkerDataForMarkerDeleteQuery = makeTempHistoryMarkerDataForMarkerDeleteQuery();
        }
        tempHistoryMarkerDataForMarkerDeleteQuery.setArgumentHolderValue(0, tempHistoryData);    //아래의 selectArg에 markerData 해당됌.
        daoTempHistoryMarkerDataInteger = getDatabaseHelperMain().getDaoTempHistoryMarkerData();
        daoTempHistoryMarkerDataInteger.delete(tempHistoryMarkerDataForMarkerDeleteQuery);
        return;
    }

    private PreparedDelete<TempHistoryMarkerData> makeTempHistoryMarkerDataForMarkerDeleteQuery() throws SQLException {
        // build our inner query for UserPost objects
        DeleteBuilder<TempHistoryMarkerData, Integer> tempHistoryMarkerDataQb = daoTempHistoryMarkerDataInteger.deleteBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        //markerMarkerTypeQb.selectColumns(MarkerTypeData.ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 markerType를 setting 할 수 있다.
        tempHistoryMarkerDataQb.where().eq(TempHistoryMarkerData.THMM_TEMPHISTORYDATA_ID_FIELD_NAME, selectArg);
        return tempHistoryMarkerDataQb.prepare();
    }


    //tempHistoryMarkerData table에서 param tempHistoryData에 해당하는 markerData들을 모두 가져오는 쿼리 실행문.
    private PreparedQuery<MarkerData> tempHistoryMarkerDataForMarkerSelectQuery = null;

    private List<MarkerData> queryMarkerDatasForTempHistoryLMData(TempHistoryData tempHistoryData) throws SQLException {
        if (tempHistoryMarkerDataForMarkerSelectQuery == null) {
            tempHistoryMarkerDataForMarkerSelectQuery = makeTempHistoryMarkerDataForMarkerSelectQuery();
        }
        tempHistoryMarkerDataForMarkerSelectQuery.setArgumentHolderValue(0, tempHistoryData);    //아래의 selectArg에 markerData 해당됌.
        //daoTempHistoryMarkerDataInteger = getDatabaseHelperMain().getDaoTempHistoryMarkerData();
        return daoMarkerDataInteger.query(tempHistoryMarkerDataForMarkerSelectQuery);
    }

    private PreparedQuery<MarkerData> makeTempHistoryMarkerDataForMarkerSelectQuery() throws SQLException {
        QueryBuilder<TempHistoryMarkerData, Integer> tempHistoryMarkerDataQb = daoTempHistoryMarkerDataInteger.queryBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        tempHistoryMarkerDataQb.selectColumns(TempHistoryMarkerData.THMM_MARKERDATA_ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        tempHistoryMarkerDataQb.where().eq(TempHistoryMarkerData.THMM_TEMPHISTORYDATA_ID_FIELD_NAME, selectArg);

        // MarkerTypeData dao에서 해당되는 id의 markerTypeData를 모두 가져온다.
        QueryBuilder<MarkerData, Integer> markerDataQb = daoMarkerDataInteger.queryBuilder();
        // where the id matches in the post-id from the inner query
        markerDataQb.where().in(MarkerData.ID_FIELD_NAME, tempHistoryMarkerDataQb);
        return markerDataQb.prepare();
    }

    // TempHistoryLMData테이블에서 param tempHistoryData에 해당하는 LocationMemoryData들을 모두 삭제하는 쿼리 실행문.
    private PreparedDelete<TempHistoryLMData> tempHistoryLMDataForLocationMemoryDeleteQuery = null;

    private void deleteLMDatasForTempHistoryLMData(TempHistoryData tempHistoryData) throws SQLException {
        if (tempHistoryLMDataForLocationMemoryDeleteQuery == null) {
            tempHistoryLMDataForLocationMemoryDeleteQuery = makeTempHistoryLMDataForLocationMemoryDeleteQuery();
        }
        tempHistoryLMDataForLocationMemoryDeleteQuery.setArgumentHolderValue(0, tempHistoryData);    //아래의 selectArg에 markerData 해당됌.
        daoTempHistoryLMDataInteger = getDatabaseHelperMain().getDaoTempHistoryLMData();
        daoTempHistoryLMDataInteger.delete(tempHistoryLMDataForLocationMemoryDeleteQuery);
        return;
    }

    private PreparedDelete<TempHistoryLMData> makeTempHistoryLMDataForLocationMemoryDeleteQuery() throws SQLException {
        // build our inner query for UserPost objects
        DeleteBuilder<TempHistoryLMData, Integer> tempHistoryLMDataQb = daoTempHistoryLMDataInteger.deleteBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        //markerMarkerTypeQb.selectColumns(MarkerTypeData.ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 markerType를 setting 할 수 있다.
        tempHistoryLMDataQb.where().eq(TempHistoryLMData.THLM_TEMPHISTORYDATA_ID_FIELD_NAME, selectArg);
        return tempHistoryLMDataQb.prepare();
    }

    //TempHistoryLMData 테이블에서 param tempHistoryData에 해당하는 LocationMemoryData들을 모두 가져오는 쿼리 실행문.
    private PreparedQuery<LocationMemoryData> tempHistoryMarkerDataForLocationMemorySelectQuery = null;

    private List<LocationMemoryData> queryLMDatasForTempHistoryLMData(TempHistoryData tempHistoryData) throws SQLException {
        if (tempHistoryMarkerDataForLocationMemorySelectQuery == null) {
            tempHistoryMarkerDataForLocationMemorySelectQuery = makeTempHistoryLMDataForLocationMemorySelectQuery();
        }
        tempHistoryMarkerDataForLocationMemorySelectQuery.setArgumentHolderValue(0, tempHistoryData);    //아래의 selectArg에 markerData 해당됌.
        //daoTempHistoryMarkerDataInteger = getDatabaseHelperMain().getDaoTempHistoryMarkerData();
        return daoLocationMemoryDataInteger.query(tempHistoryMarkerDataForLocationMemorySelectQuery);
    }

    private PreparedQuery<LocationMemoryData> makeTempHistoryLMDataForLocationMemorySelectQuery() throws SQLException {
        QueryBuilder<TempHistoryLMData, Integer> tempHistoryLMDataQb = daoTempHistoryLMDataInteger.queryBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        tempHistoryLMDataQb.selectColumns(TempHistoryLMData.THLM_LOCATIONMEMDATA_ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        tempHistoryLMDataQb.where().eq(TempHistoryLMData.THLM_TEMPHISTORYDATA_ID_FIELD_NAME, selectArg);

        // MarkerTypeData dao에서 해당되는 id의 markerTypeData를 모두 가져온다.
        QueryBuilder<LocationMemoryData, Integer> lmDataQb = daoLocationMemoryDataInteger.queryBuilder();
        // where the id matches in the post-id from the inner query
        lmDataQb.where().in(LocationMemoryData.LOCATIONMEMORY_ID_FIELD_NAME, tempHistoryLMDataQb);
        return lmDataQb.prepare();
    }
}

