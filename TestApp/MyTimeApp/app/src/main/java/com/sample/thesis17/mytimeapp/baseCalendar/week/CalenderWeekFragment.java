package com.sample.thesis17.mytimeapp.baseCalendar.week;

import android.content.Context;
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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.DateForTempHisoryData;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.TempHistoryData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.baseCalendar.CalenderWeekItemIdxWithIsHistoryData;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogCreateCalenderItemFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogModifyCalenderItemFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogViewCalenderItemFragment;
import com.sample.thesis17.mytimeapp.baseCalendar.DialogViewCalenderTempItemFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.week.CustomWeekView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CalenderWeekFragment extends Fragment implements DialogViewCalenderItemFragment.DialogViewCalenderItemFragmentListener, DialogViewCalenderTempItemFragment.DialogViewCalenderTempItemFragmentListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View weekGridview;
    CustomWeekView customWeekView;
    TextView centerText;
    Button prevButton, nextButton;
    Context curContext;

    long weekFirstTimeLong = 0;

    //DB
    List<FixedTimeTableData> listFixedTimeTableData = null;
    //List<FixedTimeTableData> listFixedTimeTableDataQueryedByMarkerData = null;
    List<MarkerData> listMarkerData = null;
    List<TempHistoryData> listTempHistoryData = null;
    List<HistoryData> listHistoryData = null;

    //List<DateForTempHisoryData> listDateForTempHisoryData = null;

    Dao<FixedTimeTableData, Integer> daoFixedTimeTableDataInteger = null;
    Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    Dao<TempHistoryData, Integer> daoTempHistoryData = null;
    Dao<DateForTempHisoryData, Integer> daoDateForTempHistoryData = null;
    Dao<HistoryData, Integer> daoHistoryData = null;

    CalenderWeekAdapter calenderWeekAdapter = null;


    //dialog
    DialogCreateCalenderItemFragment dialogCreateCalenderItemFragment = null;
    DialogModifyCalenderItemFragment dialogModifyCalenderItemFragment = null;


    DialogViewCalenderTempItemFragment dialogViewCalenderTempItemFragment = null;
    DialogViewCalenderItemFragment dialogViewCalenderItemFragment = null;
    Bundle bundleArg = null;

    CalenderWeekItemIdxWithIsHistoryData idxWithIsHistoryData = null;        //listFixedTimeTableData(시간표box list)에서 선택된 index

    //origin value
    FixedTimeTableData originFixedTimeTableData = null;
    MarkerData originMarkerData = null;

    //int curYear;        //현재 달력의 년, 월.
    //int curMonth;

    public CalenderWeekFragment() {
        // Required empty public constructor
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
            daoTempHistoryData = getDatabaseHelperMain().getDaoTempHistoryData();
            daoHistoryData = getDatabaseHelperMain().getDaoHistoryData();
            daoDateForTempHistoryData = getDatabaseHelperMain().getDaoDateForTempHisoryData();

            listFixedTimeTableData = daoFixedTimeTableDataInteger.queryForAll();
            listMarkerData = daoMarkerDataInteger.queryForAll();

            //query 조건 필요
            //listTempHistoryData = daoTempHistoryData.queryForAll();
            //listHistoryData = daoHistoryData.queryForAll();

            //DateForTempHisoryData -> 직접 query 사용

            /*
            //TODO : check if this week is already calculated
            if(isAlreadyCalculated()){
                doCalcTempHistoryData();   //create tempHistoryData from locationMem
            }

            //listHistoryData = daoHistoryData.query with weekFirstTimeLong
            */

            changeWeekWithFirstWeekLong();

            calenderWeekAdapter = new CalenderWeekAdapter(listHistoryData, listTempHistoryData);  //adapter create
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
        centerText = (TextView)retView.findViewById(R.id.fragment_timetable_week_textMonth);

        weekGridview = (View)(retView.findViewById(R.id.calenderWeekView));
        customWeekView = (CustomWeekView) weekGridview;

        //adapter View에 등록
        customWeekView.setCustomWeekAdapter(calenderWeekAdapter);




        //weekGridview = (GridView)(retView.findViewById(R.id.fragment_timetable_week_GridView));    //retView에서 gridview 찾아 할당
        /*timetableWeekAdapter = new TimetableWeekAdapter(getActivity());
        weekGridview.setAdapter(timetableWeekAdapter); //adpater 설정
*/
        // GridView의 click 리스너 설정
        /*weekGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 현재 선택한 일자 정보
                timetableWeekAdapter.setSelectedPosition(position); //adapter의 position set
                TimetableWeekItem curItem = (TimetableWeekItem) timetableWeekAdapter.getItem(position); //adpater에서 click position에 해당하는 item get
                if((curItem.isWeek() == false) && (curItem.getiDayValue() == 0)) {
                    //빈 공간을 클릭했으므로 무시
                }
                else{
                    if(curItem.isWeek() == true){
                        //Todo CalendarWeekFragment로 교체
                    }
                    else{
                        //Todo CalendarDayFragment로 교체
                    }
                    int day = curItem.getiDayValue();   //click position에 해당하는 item의 day획득
                    timetableWeekAdapter.notifyDataSetChanged();    //adapter에 data가 변경되었음을 알려 view를 바꾸는 동작을 하는 함수
                }

                //Todo activity에 year, month, day 정보 전달. fragment 교체 요구




            }
        });*/

        //Move Month button
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : change WEEK
            }
        });

        //add button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : change WEEK
            }
        });



        return retView;
    }

    private void setCenterText(){
        /*curMonth = timetableWeekAdapter.getCurMonth();
        curYear = timetableWeekAdapter.getCurYear();
        centerText.setText(curYear + " " + (curMonth+1));*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
        daoDateForTempHistoryData, weekFirstTimeLong
                //계산 뒤 DateForTempHisoryData에 weekFirstTimeLong추가
    }

    public void changeWeekWithFirstWeekLong(){
        if(calenderWeekAdapter != null){
            calenderWeekAdapter.setLongStartDate(weekFirstTimeLong);
        }
        if(isAlreadyCalculated()){
            doCalcTempHistoryData();   //create tempHistoryData from locationMem
        }
        //listHistoryData = daoHistoryData.query with weekFirstTimeLong

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


    @Override
    public void doSlightDelete() {

    }

    @Override
    public void doSave(FixedTimeTableData fttd, long startTime, long endTime, MarkerData md, String memo) {

    }
    @Override
    public void doDeepDelete() {

    }
    @Override
    public List<MarkerData> getListMarkerData() {
        return null;
    }
    @Override
    public List<FixedTimeTableData> getListFixedTimeTableData() {
        return null;
    }
    //TODO : 날짜 바뀔때 adapter. long time 시작지점 change
}

