package com.sample.thesis17.mytimeapp.baseCalendar.month;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;

import java.util.Calendar;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalenderMonthFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalenderMonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderMonthFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GridView monthGridview;
    CalenderMonthAdapter calenderMonthAdapter;
    TextView centerText;
    Button leftButton, rightButton;

    int curYear;        //현재 달력의 년, 월.
    int curMonth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CalenderMonthFragmentListener calenderMonthFragmentListener = null;

    long inParamTimeLong = 0L;

    Context curContext = null;

    public interface CalenderMonthFragmentListener{
        void fragmentChangeToWeekView(long startTime);
    }

    public CalenderMonthFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        inParamTimeLong = getArguments().getLong(ARG_PARAM1);

        //monthGridview = (GridView)getView().findViewById(R.id.fragment_calender_month_GridView);

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

        monthGridview = (GridView)(retView.findViewById(R.id.fragment_calender_month_GridView));    //retView에서 gridview 찾아 할당

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
                        //Todo CalendarWeekFragment로 교체
                        //Calendar tempCalendar = Calendar.getInstance();
                        //tempCalendar.setTimeInMillis(curItem.getlWeekValue());
                        calenderMonthFragmentListener.fragmentChangeToWeekView(curItem.getlWeekValue() - LONG_HOUR_MILLIS * 9); //for LOCALE_US
                    }
                    else{
                        //Todo CalendarDayFragment로 교체
                    }

                    calenderMonthAdapter.notifyDataSetChanged();    //adapter에 data가 변경되었음을 알려 view를 바꾸는 동작을 하는 함수
                }

                //Todo activity에 year, month, day 정보 전달. fragment 교체 요구




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
        centerText.setText(curYear + " " + (curMonth+1));
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
        mListener = null;
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
}
