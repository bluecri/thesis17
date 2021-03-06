package com.sample.thesis17.mytimeapp.baseTimeTable.day;


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
import android.widget.ImageButton;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemCreateFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemModifyViewFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemViewFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;

public class TimetableDayFragment extends Fragment implements DialogWeekItemViewFragment.DialogWeekItemViewFragmentListener, DialogWeekItemModifyViewFragment.DialogWeekItemModifyViewFragmentListener, DialogWeekItemCreateFragment.DialogWeekItemCreateFragmentListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View dayGridview;
    CustomDayView customDayView;
    TextView centerText;
    ImageButton leftButton;
    Button addButton;
    Context curContext;

    Long longStartTime = 0L;

    //DB
    List<FixedTimeTableData> listFixedTimeTableData = null;
    List<MarkerData> listMarkerData = null;
    Dao<FixedTimeTableData, Integer> daoFixedTimeTableDataInteger = null;
    Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    CustomDayAdapter customDayAdapter = null;


    //dialog
    DialogWeekItemCreateFragment dialogWeekItemCreateFragment = null;
    DialogWeekItemViewFragment dialogWeekItemViewFragment = null;
    DialogWeekItemModifyViewFragment dialogWeekItemModifyViewFragment = null;
    Bundle bundleArg = null;

    int selectedIdx = 0;        //listFixedTimeTableData(시간표box list)에서 선택된 index


    //int curYear;        //현재 달력의 년, 월.
    //int curMonth;

    public TimetableDayFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TimetableDayFragment newInstance(long longStartTime) {
        TimetableDayFragment fragment = new TimetableDayFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, longStartTime);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        longStartTime = getArguments().getLong(ARG_PARAM1);

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            if(daoFixedTimeTableDataInteger != null) {
                List<FixedTimeTableData> tempList = daoFixedTimeTableDataInteger.queryForAll();
                listFixedTimeTableData = new ArrayList<>();
                for(FixedTimeTableData fttd : tempList){
                    if(fttd.isCache() && fttd.isInvisible() == false && (fttd.getlEndTime() > fttd.getlStartTime() && (longStartTime < fttd.getlEndTime() && fttd.getlStartTime() < longStartTime + LONG_DAY_MILLIS) ||
                            (fttd.getlEndTime() < fttd.getlStartTime() && (longStartTime < fttd.getlEndTime() || fttd.getlStartTime() < longStartTime + LONG_DAY_MILLIS)))){
                        listFixedTimeTableData.add(fttd);
                    }

                }
                customDayAdapter = new CustomDayAdapter(curContext, listFixedTimeTableData);  //adapter create
                customDayAdapter.setLongStartDate(longStartTime);
            }
            //fixedTimeTableData와 연결된 markerData를 정하기 위한 list
            if(daoMarkerDataInteger != null) {
                listMarkerData = daoMarkerDataInteger.queryForAll();
            }
        }
        catch(SQLException e){
            Log.d("TimetableDayF", "getDaoFixedTimeTableData SQL Exception");
        }
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_timetable_day, container, false);        //fragment에 해당하는 retView

        //button, text
        leftButton = (ImageButton)retView.findViewById(R.id.fragment_timetable_day_buttonRefresh);
        addButton = (Button)retView.findViewById(R.id.fragment_timetable_day_buttonAdd);
        centerText = (TextView)retView.findViewById(R.id.fragment_timetable_day_textMonth);

        dayGridview = (View)(retView.findViewById(R.id.customDayView));
        customDayView = (CustomDayView) dayGridview;

        //adapter View에 등록
        customDayView.setCustomDayAdapter(customDayAdapter);




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
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDayView.invalidate();
                /*timetableWeekAdapter.setPreviousMonth();;
                timetableWeekAdapter.notifyDataSetChanged();;
                setCenterText();*/
            }
        });

        //add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWeekItemCreateFragment = new DialogWeekItemCreateFragment();
                dialogWeekItemCreateFragment.setTargetFragment(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("timetable_day_fragment"), 0);
                Log.d("timetabledayF", "onClickAdd()");
                dialogWeekItemCreateFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogDayItemViewFragment");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //db
    /*List<FixedTimeTableData> getFixedTimeTableDataList(){
        return listFixedTimeTableData;
    }*/

    //DB
    private DatabaseHelperMain databaseHelperMain = null;


    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(super.getContext());
        }
        return databaseHelperMain;
    }


    void openDayFragment(long longStartTime){

    }


    //open dialogWeekItemViewFragment
    void openDialogWithIdx(int idx){
        selectedIdx = idx;
        //listFixedTimeTableData use
        //dialog call
        dialogWeekItemViewFragment = new DialogWeekItemViewFragment();
        bundleArg = new Bundle();
        bundleArg.putString("title", listFixedTimeTableData.get(selectedIdx).getStrFixedTimeTableName());
        bundleArg.putLong("starttime", listFixedTimeTableData.get(selectedIdx).getlStartTime());
        bundleArg.putLong("endtime", listFixedTimeTableData.get(selectedIdx).getlEndTime());
        bundleArg.putString("marker", listFixedTimeTableData.get(selectedIdx).getForeMarkerData().getStrMarkerName());
        bundleArg.putString("memo", listFixedTimeTableData.get(selectedIdx).getStrMemo());

        dialogWeekItemViewFragment.setArguments(bundleArg);
        dialogWeekItemViewFragment.setTargetFragment(this, 0);
        //dialogWeekItemViewFragment.dismiss();

        Log.d("timetabledayF", "opendialogwithidx()");
        dialogWeekItemViewFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogDayItemViewFragment");
    }


    //DialogWeekItemViewFragment LIstener
    @Override
    public void openModifyDialogWithIdx(){
        //open dialogWeekItemModifyViewFragment using selectedIdx
        bundleArg = new Bundle();
        bundleArg.putString("title", listFixedTimeTableData.get(selectedIdx).getStrFixedTimeTableName());
        bundleArg.putLong("starttime", listFixedTimeTableData.get(selectedIdx).getlStartTime());
        bundleArg.putLong("endtime", listFixedTimeTableData.get(selectedIdx).getlEndTime());
        //가져온 listFixedTimeTableData에서 selectedIdx에 해당되는 MarkerData를 사용하여, listMarkerData의 어느 index에 존재하는지 확인.
        bundleArg.putInt("markerIdx", listMarkerData.indexOf(listFixedTimeTableData.get(selectedIdx).getForeMarkerData()));
        bundleArg.putString("memo", listFixedTimeTableData.get(selectedIdx).getStrMemo());


        dialogWeekItemModifyViewFragment = new DialogWeekItemModifyViewFragment();

        dialogWeekItemModifyViewFragment.setArguments(bundleArg);
        dialogWeekItemModifyViewFragment.setTargetFragment(this, 0);

        Log.d("timetabledayF", "openModifyDialogWithIdx()");
        //close dialogWeekItemViewFragment
        if(dialogWeekItemViewFragment != null){
            dialogWeekItemViewFragment.dismiss();
            dialogWeekItemViewFragment = null;
        }

        dialogWeekItemModifyViewFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogDayItemModifyViewFragment");
    }
    @Override
    public void doDelete() {
        //listFixedTimeTableData, selectedIndex
        FixedTimeTableData delData = listFixedTimeTableData.get(selectedIdx);
        delData.setCache(false);
        listFixedTimeTableData.remove(selectedIdx);

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoFixedTimeTableDataInteger.update(delData);
            //  daoFixedTimeTableDataInteger.delete(delData);   : delete 대신 cache -> false
        }
        catch(SQLException e){
            Log.d("timetableday", "doDelete sql");
        }

        customDayView.invalidate();


        //finally close dialogWeekItemViewFragment
        if(dialogWeekItemViewFragment != null){
            dialogWeekItemViewFragment.dismiss();
            dialogWeekItemViewFragment = null;
        }
    }



    //DialogWeekItemModifyViewFragment LIstener
    @Override
    public void doModify(String title, long startTime, long endTime, int markerIdx, String memo) {
        FixedTimeTableData modifyData = listFixedTimeTableData.get(selectedIdx);
        modifyData.setStrFixedTimeTableName(title);
        modifyData.setStrMemo(memo);
        modifyData.setlStartTime(startTime);
        modifyData.setlEndTime(endTime);
        //TODO : inner bound fix
        modifyData.setlInnerBoundStartTime(startTime);
        modifyData.setlInnerBoundStartTime(endTime);
        modifyData.setlBoundStartTime(startTime);
        modifyData.setlBoundEndTime(endTime);
        modifyData.setForeMarkerData(listMarkerData.get(markerIdx));

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoFixedTimeTableDataInteger.update(modifyData);
        }
        catch(SQLException e){
            Log.d("timetableDay", "doDelete sql");
        }

        customDayView.invalidate();

        //finally close dialogWeekItemModifyViewFragment
        if(dialogWeekItemModifyViewFragment != null){
            dialogWeekItemModifyViewFragment.dismiss();
            dialogWeekItemModifyViewFragment = null;
        }
    }
    //DialogWeekItemCreateFragment LIstener
    @Override
    public void doCreate(String title, long startTime, long endTime, int markerIdx, String memo) {
        FixedTimeTableData createData = new FixedTimeTableData(listMarkerData.get(markerIdx), title, startTime, endTime, startTime, endTime, startTime, endTime, memo, true, false);
        if(longStartTime < createData.getlEndTime() && createData.getlStartTime() < longStartTime + LONG_DAY_MILLIS){
            listFixedTimeTableData.add(createData);
        }

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoFixedTimeTableDataInteger.create(createData);
        }
        catch(SQLException e){
            Log.d("timetableDay", "doDelete sql");
        }

        customDayView.invalidate();
        //finally close DialogWeekItemCreateFragment
        if(dialogWeekItemCreateFragment != null){
            dialogWeekItemCreateFragment.dismiss();
            dialogWeekItemCreateFragment = null;
        }
    }

    //DialogWeekItemCreateFragment LIstener & DialogWeekItemModifyViewFragment LIstener
    @Override
    public List<MarkerData> getArrayListMarkerData() {
        /*ArrayList<String> retArrayLIst = new ArrayList<String>();
        if(listMarkerData != null){
            for(MarkerData mtd : listMarkerData)
                retArrayLIst.add(mtd.getStrMarkerName());
        }
        return retArrayLIst;*/
        if(listMarkerData == null){
            return new ArrayList<MarkerData>();
        }
        else{
            return listMarkerData;
        }
    }



}
