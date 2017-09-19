package com.sample.thesis17.mytimeapp.baseTimeTable.week;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.FixedTimeTableData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.baseCalendar.month.CalenderMonthAdapter;
import com.sample.thesis17.mytimeapp.baseCalendar.month.MonthItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimetableWeekFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimetableWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableWeekFragment extends Fragment implements DialogWeekItemViewFragment.DialogWeekItemViewFragmentListener, DialogWeekItemModifyViewFragment.DialogWeekItemModifyViewFragmentListener, DialogWeekItemCreateFragment.DialogWeekItemCreateFragmentListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View weekGridview;
    CustomWeekView customWeekView;
    TextView centerText;
    Button leftButton, addButton;
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


    //int curYear;        //현재 달력의 년, 월.
    //int curMonth;

    public TimetableWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimetableWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
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
                listFixedTimeTableData = daoFixedTimeTableDataInteger.queryForAll();
                customWeekAdapter = new CustomWeekAdapter(listFixedTimeTableData);  //adapter create
            }
            //fixedTimeTableData와 연결된 markerData를 정하기 위한 list
            if(daoMarkerDataInteger != null) {
                listMarkerData = daoMarkerDataInteger.queryForAll();
            }
        }
        catch(SQLException e){
            Log.d("TimetableWeekF", "getDaoFixedTimeTableData SQL Exception");
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
        View retView = inflater.inflate(R.layout.fragment_timetable_week, container, false);        //fragment에 해당하는 retView

        //button, text
        leftButton = (Button)retView.findViewById(R.id.fragment_timetable_week_buttonPrev);
        addButton = (Button)retView.findViewById(R.id.fragment_timetable_week_buttonAdd);
        centerText = (TextView)retView.findViewById(R.id.fragment_timetable_week_textMonth);

        weekGridview = (View)(retView.findViewById(R.id.customWeekView));
        customWeekView = (CustomWeekView) weekGridview;

        //adapter View에 등록
        customWeekView.setCustomWeekAdapter(customWeekAdapter);




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
                /*timetableWeekAdapter.setPreviousMonth();;
                timetableWeekAdapter.notifyDataSetChanged();;
                setCenterText();*/
            }
        });

        //add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        curContext);

// 제목셋팅
                alertDialogBuilder.setTitle("프로그램 종료");

// AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("프로그램을 종료할 것입니까?")
                        .setCancelable(false);

                alertDialogBuilder
                        .setMessage("프로그램을 종료할 것입니까?")
                        .setCancelable(false)
                        .setPositiveButton("종료",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 프로그램을 종료한다
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();




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
        // TODO: Update argument type and name
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



    //open dialogWeekItemViewFragment
    void openDialogWithIdx(int idx){
        //TODO: make dialog
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
        if(bundleArg == null){
            bundleArg = new Bundle();
            bundleArg.putString("title", listFixedTimeTableData.get(selectedIdx).getStrFixedTimeTableName());
            bundleArg.putLong("starttime", listFixedTimeTableData.get(selectedIdx).getlStartTime());
            bundleArg.putLong("endtime", listFixedTimeTableData.get(selectedIdx).getlEndTime());
            bundleArg.putInt("markerIdx", listMarkerData.indexOf(listFixedTimeTableData.get(selectedIdx).getForeMarkerData()));
            bundleArg.putString("memo", listFixedTimeTableData.get(selectedIdx).getStrMemo());
        }
        else{
            //가져온 listFixedTimeTableData에서 selectedIdx에 해당되는 MarkerData를 사용하여, listMarkerData의 어느 index에 존재하는지 확인.
            bundleArg.putInt("markerIdx", listMarkerData.indexOf(listFixedTimeTableData.get(selectedIdx).getForeMarkerData()));
        }
        //close dialogWeekItemViewFragment
        if(dialogWeekItemViewFragment != null){
            dialogWeekItemViewFragment.dismiss();
            dialogWeekItemViewFragment = null;
        }

        dialogWeekItemModifyViewFragment.setArguments(bundleArg);
        dialogWeekItemModifyViewFragment.setTargetFragment(this, 0);

        Log.d("timetableweekF", "openModifyDialogWithIdx()");

        dialogWeekItemModifyViewFragment.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogWeekItemModifyViewFragment");
    }
    @Override
    public void doDelete() {
        //TODO:



        //finally close dialogWeekItemViewFragment
        if(dialogWeekItemViewFragment != null){
            dialogWeekItemViewFragment.dismiss();
            dialogWeekItemViewFragment = null;
        }
    }



    //DialogWeekItemModifyViewFragment LIstener
    @Override
    public void doModify(String title, long startTime, long endTime, int markerIdx, String memo) {
        //finally close dialogWeekItemModifyViewFragment
        if(dialogWeekItemModifyViewFragment != null){
            dialogWeekItemModifyViewFragment.dismiss();
            dialogWeekItemModifyViewFragment = null;
        }
        //TODO:
    }
    //DialogWeekItemCreateFragment LIstener
    @Override
    public void doCreate(String title, long startTime, long endTime, int markerIdx, String memo) {
        //finally close DialogWeekItemCreateFragment
        if(dialogWeekItemCreateFragment != null){
            dialogWeekItemCreateFragment.dismiss();
            dialogWeekItemCreateFragment = null;
        }
        //TODO:
    }

    //DialogWeekItemCreateFragment LIstener & DialogWeekItemModifyViewFragment LIstener
    @Override
    public ArrayList<String> getArrayListMarkerDataTitle() {
        ArrayList<String> retArrayLIst = new ArrayList<String>();
        if(listMarkerData != null){
            for(MarkerData mtd : listMarkerData)
            retArrayLIst.add(mtd.getStrMarkerName());
        }
        return retArrayLIst;
    }



}
