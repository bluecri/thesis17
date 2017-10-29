package com.sample.thesis17.mytimeapp.map;


import android.app.Activity;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMarkerModifyFragment extends DialogFragment {

    private Context curContext = null;
    private DialogMarkerModifyListener dialogMarkerModifyListener = null;
    //private ArrayList<MarkerTypeData> markerTypeList = null;

    TextView textViewMarkerName = null;
    TextView textViewMarkerMemo = null;
    Button buttonSelectType = null;


    public interface DialogMarkerModifyListener{
        //method
        public void modifyMarkerComplete(String title, String memo);
        //public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList();
    }

    public DialogMarkerModifyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DialogMarkerModifyFra", "DialogMarkerModifyFragment onCreate");
        //markerTypeList = dialogMarkerModifyListener.getMarkerTypeModifiedDataList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("DialogMarkerModifyFr", "DialogMarkerModifyFragment onattach");
        curContext = context;
        try{
            dialogMarkerModifyListener = (DialogMarkerModifyListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogNewmarkerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("DialogMarkerFragment", "DialogMarkerFragment onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("마커 정보 수정 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_marker_modify, null);
        textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_marker_modify_marker_name);
        textViewMarkerMemo = (TextView)retView.findViewById(R.id.fragment_dialog_marker_modify_marker_memo);
        buttonSelectType = (Button)retView.findViewById(R.id.fragment_dialog_marker_modify_marker_button_selectType);

        Bundle tempArg = getArguments();
        Log.d("debugs", "1"+tempArg.getString("title"));
        textViewMarkerName.setText(tempArg.getString("title"));
        textViewMarkerMemo.setText(tempArg.getString("memo"));

        buttonSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog
                DialogMarkerModifyMarkerTypeFragment dig = new DialogMarkerModifyMarkerTypeFragment();
                dig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogMarkerModifyMarkerTypeFragment");
            }
        });

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogMarkerModifyListener.modifyMarkerComplete(textViewMarkerName.getText().toString(), textViewMarkerMemo.getText().toString());
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerModifyFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

}
