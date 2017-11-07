package com.sample.thesis17.mytimeapp.baseTimeTable.week;

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
import com.sample.thesis17.mytimeapp.MainActivity;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemCreateFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemModifyViewFragment;
import com.sample.thesis17.mytimeapp.baseTimeTable.DialogWeekItemViewFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;


public class TimetableWeekFragment extends Fragment implements DialogWeekItemViewFragment.DialogWeekItemViewFragmentListener, DialogWeekItemModifyViewFragment.DialogWeekItemModifyViewFragmentListener, DialogWeekItemCreateFragment.DialogWeekItemCreateFragmentListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View weekGridview;
    CustomWeekView customWeekView;
    TextView centerText;
    ImageButton leftButton;
    Button addButton;
    Context curContext;

    //DB
    List<FixedTimeTableData> listFixedTimeTableData = null;
    List<MarkerData> listMarkerData = null;
    Dao<FixedTimeTableData, Integer> daoFixedTimeTableDataInteger = null;
    Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    CustomWeekAdapter customWeekAdapter = null;

    //dialog
    DialogWeekItemCreateFragment dialogWeekItemCreateFragment = null;
    DialogWeekItemViewFragment dialogWeekItemViewFragment = null;
    DialogWeekItemModifyViewFragment dialogWeekItemModifyViewFragment = null;
    Bundle bundleArg = null;

    int selectedIdx = 0;        //listFixedTimeTableData(시간표box list)에서 선택된 index


    public interface TimetableWeekFragmentListener{
        void replaceDayFragmentWithTime(long longStartTime);
    }

    //int curYear;        //현재 달력의 년, 월.
    //int curMonth;
    public void openDayFragment(long longStartTime){
        ((MainActivity)curContext).replaceDayFragmentWithTime(longStartTime);
    }

    public TimetableWeekFragment() {
        // Required empty public constructor
    }

    public static TimetableWeekFragment newInstance(String param1, String param2) {
        TimetableWeekFragment fragment = new TimetableWeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            if(daoFixedTimeTableDataInteger != null) {
                List<FixedTimeTableData> tempList = daoFixedTimeTableDataInteger.queryForAll();
                listFixedTimeTableData = new ArrayList<>();
                for(FixedTimeTableData fttd : tempList){
                    if(fttd.isCache() && fttd.isInvisible() == false){
                        listFixedTimeTableData.add(fttd);
                    }
                }
                customWeekAdapter = new CustomWeekAdapter(curContext, listFixedTimeTableData);  //adapter create
            }
            //fixedTimeTableData와 연결된 markerData를 정하기 위한 list
            if(daoMarkerDataInteger != null) {
                listMarkerData = daoMarkerDataInteger.queryForAll();
            }
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
        View retView = inflater.inflate(R.layout.fragment_timetable_week, container, false);        //fragment에 해당하는 retView

        //button, text
        leftButton = (ImageButton)retView.findViewById(R.id.fragment_timetable_week_buttonRefresh);
        addButton = (Button)retView.findViewById(R.id.fragment_timetable_week_buttonAdd);
        centerText = (TextView)retView.findViewById(R.id.fragment_timetable_week_textMonth);

        weekGridview = (View)(retView.findViewById(R.id.customWeekView));
        customWeekView = (CustomWeekView) weekGridview;

        //adapter View에 등록
        customWeekView.setCustomWeekAdapter(customWeekAdapter);

        //Move Month button
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customWeekView.invalidate();
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
                dialogWeekItemCreateFragment.setTargetFragment(((FragmentActivity)curContext).getSupportFragmentManager().findFragmentByTag("timetable_week_fragment"), 0);
                Log.d("timetableweekF", "onClickAdd()");
                dialogWeekItemCreateFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogWeekItemViewFragment");
            }
        });

        return retView;
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

        Log.d("timetableweekF", "opendialogwithidx()");
        dialogWeekItemViewFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogWeekItemViewFragment");
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

        Log.d("timetableweekF", "openModifyDialogWithIdx()");
        //close dialogWeekItemViewFragment
        if(dialogWeekItemViewFragment != null){
            dialogWeekItemViewFragment.dismiss();
            dialogWeekItemViewFragment = null;
        }

        dialogWeekItemModifyViewFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogWeekItemModifyViewFragment");
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
            Log.d("timetableweek", "doDelete sql");
        }

        customWeekView.invalidate();


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
            Log.d("timetableweek", "doDelete sql");
        }

        customWeekView.invalidate();

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
        listFixedTimeTableData.add(createData);

        try{
            daoFixedTimeTableDataInteger = getDatabaseHelperMain().getDaoFixedTimeTableData();
            daoFixedTimeTableDataInteger.create(createData);
        }
        catch(SQLException e){
            Log.d("timetableweek", "doDelete sql");
        }

        customWeekView.invalidate();
        //finally close DialogWeekItemCreateFragment
        if(dialogWeekItemCreateFragment != null){
            dialogWeekItemCreateFragment.dismiss();
            dialogWeekItemCreateFragment = null;
        }
    }

    //DialogWeekItemCreateFragment LIstener & DialogWeekItemModifyViewFragment LIstener
    @Override
    public List<MarkerData> getArrayListMarkerData() {
        if(listMarkerData == null){
            return new ArrayList<MarkerData>();
        }
        else{
            return listMarkerData;
        }
    }



}
