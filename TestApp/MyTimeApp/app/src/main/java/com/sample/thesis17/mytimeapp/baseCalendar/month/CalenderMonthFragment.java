package com.sample.thesis17.mytimeapp.baseCalendar.month;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;

public class CalenderMonthFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    GridView monthGridview;
    LinearLayout topLinearLayout;
    CalenderMonthAdapter calenderMonthAdapter;
    ArrayAdapter weekTopAdapter = null;
    TextView centerText;
    Button leftButton, rightButton;
    List<View> weekTopViewList = null;

    int curYear;        //현재 달력의 년, 월.
    int curMonth;

    private CalenderMonthFragmentListener calenderMonthFragmentListener = null;

    long inParamTimeLong = 0L;

    Context curContext = null;

    public interface CalenderMonthFragmentListener{
        void fragmentChangeToWeekView(long startTime);
    }

    public CalenderMonthFragment() {
        // Required empty public constructor
    }

    public static CalenderMonthFragment newInstance(long inTime) {
        CalenderMonthFragment fragment = new CalenderMonthFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, inTime);
        //.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inParamTimeLong = getArguments().getLong(ARG_PARAM1);

        weekTopViewList = new ArrayList<View>();
        MonthWeekTopView tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("주", Color.MAGENTA);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("일", Color.RED);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("월", Color.BLACK);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("화", Color.BLACK);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("수", Color.BLACK);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("목", Color.BLACK);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("금", Color.BLACK);
        weekTopViewList.add(tempMonthWeekTopView);
        tempMonthWeekTopView = new MonthWeekTopView(curContext);
        tempMonthWeekTopView.setTextAndColor("토", Color.BLUE);
        weekTopViewList.add(tempMonthWeekTopView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_calender_month, container, false);        //fragment에 해당하는 retView

        //button, text
        leftButton = (Button)retView.findViewById(R.id.fragment_calender_month_buttonPrev);
        rightButton = (Button)retView.findViewById(R.id.fragment_calender_month_buttonNext);
        centerText = (TextView)retView.findViewById(R.id.fragment_calender_month_textMonth);
        //weekGridViewTop = (GridView) (retView.findViewById(R.id.fragment_calender_month_top_GridView));
        topLinearLayout = (LinearLayout)retView.findViewById(R.id.fragment_calender_month_top_linearLayout);
        monthGridview = (GridView)(retView.findViewById(R.id.fragment_calender_month_GridView));    //retView에서 gridview 찾아 할당

        //상위 top view 직접 추가
        topLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //topLinearLayout.getHeight(); //height is ready
                for(View topView : weekTopViewList){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(topLinearLayout.getWidth()/8, 90);
                    topView.setLayoutParams(params);
                    topLinearLayout.addView(topView);
                }
            }
        });

        // get param
        if(savedInstanceState != null && savedInstanceState.getLong("savedTimeLong") != 0L){
            inParamTimeLong = savedInstanceState.getLong("savedTimeLong");
        }

        calenderMonthAdapter = new CalenderMonthAdapter(getActivity(), inParamTimeLong);
        monthGridview.setAdapter(calenderMonthAdapter); //adpater 설정

        setCenterText();

        // GridView의 click 리스너 설정
        monthGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 현재 선택한 일자 정보
                calenderMonthAdapter.setSelectedPosition(position); //adapter의 position set
                MonthItem curItem = (MonthItem) calenderMonthAdapter.getItem(position); //adpater에서 click position에 해당하는 item get
                if((curItem.isWeek() == false) && (curItem.getiDayValue() == 0)) {
                    //빈 공간을 클릭했으므로 무시
                }
                else{
                    int day = curItem.getiDayValue();   //click position에 해당하는 item의 day획득
                    if(curItem.isWeek() == true){
                        calenderMonthFragmentListener.fragmentChangeToWeekView(curItem.getlWeekValue() + LONG_HOUR_MILLIS * 9); //for LOCALE_US
                    }
                    calenderMonthAdapter.notifyDataSetChanged();    //adapter에 data가 변경되었음을 알려 view를 바꾸는 동작을 하는 함수
                }
                }
        });

        //Move Month button
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inParamTimeLong = calenderMonthAdapter.setPreviousMonth();
                getArguments().putLong(ARG_PARAM1, inParamTimeLong);
                calenderMonthAdapter.notifyDataSetChanged();
                setCenterText();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inParamTimeLong = calenderMonthAdapter.setNextMonth();;
                getArguments().putLong(ARG_PARAM1, inParamTimeLong);
                calenderMonthAdapter.notifyDataSetChanged();
                setCenterText();
            }
        });

        return retView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("savedTimeLong",inParamTimeLong );
    }

    private void setCenterText(){
        curMonth = calenderMonthAdapter.getCurMonth();
        curYear = calenderMonthAdapter.getCurYear();
        centerText.setText(curYear + "년 " + (curMonth+1) +"월");
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        if (context instanceof CalenderMonthFragmentListener) {
            calenderMonthFragmentListener = (CalenderMonthFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CalenderMonthFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
