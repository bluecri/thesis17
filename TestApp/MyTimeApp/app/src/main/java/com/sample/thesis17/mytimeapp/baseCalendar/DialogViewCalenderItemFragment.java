package com.sample.thesis17.mytimeapp.baseCalendar;

import android.app.Dialog;
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
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;

import java.util.Locale;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_MIN_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_WEEK_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.WEEK_STRING;
import static com.sample.thesis17.mytimeapp.Static.MyMath.WEEK_STRING_REAL;

/**
 * Created by kimz on 2017-09-21.
 */


public class DialogViewCalenderItemFragment extends DialogFragment {

    private Context curContext = null;
    private DialogViewCalenderItemFragmentListener dialogViewCalenderItemFragmentListener = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    TextView textViewTitle = null;
    TextView textViewStartWeek = null;
    TextView textViewStartTime = null;
    TextView textViewEndWeek = null;
    TextView textViewEndTime = null;
    TextView textViewMemo = null;
    TextView textViewMarkerName = null;


    public interface DialogViewCalenderItemFragmentListener{
        //method
        //public void modifyMarkerComplete(String title, String memo);
        public void doSlightDelete();
        //public void openModifyDialogWithIdx();
        //public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList();
    }

    public DialogViewCalenderItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DialogMarkerModifyFra", "DialogWeekItemViewFragment onCreate");
        //markerTypeList = -Listener.getMarkerTypeModifiedDataList();
        try{
            dialogViewCalenderItemFragmentListener = (DialogViewCalenderItemFragmentListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement dialogMarkerTypeModifyFragmentListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogMarkerModifyFr", "DialogWeekItemViewFragment onattach");
        curContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogWeekItemViewFra", "DialogWeekItemViewFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("확인된 일정 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_view_calender_item, null);

        textViewTitle = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_title);
        textViewStartWeek = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_startweek);
        textViewStartTime = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_starttime);
        textViewEndWeek = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_endweek);
        textViewEndTime = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_endtime);
        textViewMemo = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_memo);
        textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_view_calender_item_marker);

        Bundle tempArg = getArguments();

        textViewTitle.setText(tempArg.getString("title"));
        textViewMemo.setText(tempArg.getString("memo"));
        textViewMarkerName.setText(tempArg.getString("marker"));

        long startTimeArg =tempArg.getLong("starttime");
        long endTimeArg =tempArg.getLong("endtime");
        int startWeek = (int)((startTimeArg%LONG_WEEK_MILLIS)/LONG_DAY_MILLIS);
        int endWeek = (int)((endTimeArg%LONG_WEEK_MILLIS)/LONG_DAY_MILLIS);
        textViewStartWeek.setText(WEEK_STRING_REAL[startWeek]);
        textViewEndWeek.setText(WEEK_STRING_REAL[endWeek]);

        textViewStartTime.setText(String.format(Locale.KOREA, "%02d:%02d", (int)(startTimeArg%LONG_DAY_MILLIS/LONG_HOUR_MILLIS), (int)(startTimeArg%LONG_HOUR_MILLIS/LONG_MIN_MILLIS)));
        textViewEndTime.setText(String.format(Locale.KOREA, "%02d:%02d", (int)(endTimeArg%LONG_DAY_MILLIS/LONG_HOUR_MILLIS), (int)(endTimeArg%LONG_HOUR_MILLIS/LONG_MIN_MILLIS)));

        builder.setView(retView)
                // Add action buttons
                /*.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogViewCalenderItemFragmentListener.openModifyDialogWithIdx();
                    }
                })
                */
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogViewCalenderItemFragmentListener.doSlightDelete();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogViewCalenderItemFragment.this.getDialog().cancel();  //cancel dialog

                    }
                });


        return builder.create();
    }
}