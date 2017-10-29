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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMarkerTypeFragment  extends DialogFragment {

    public interface DialogMarkerTypeFragmentListener{
        public void modifyMarkerType();
        public void deleteMarkerType();
    }

    DialogMarkerTypeFragmentListener dialogMarkerTypeListener = null;

    TextView textViewMarkerTypeTitle = null;
    TextView textViewMarkerTypeMemo = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            dialogMarkerTypeListener = (DialogMarkerTypeFragmentListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException("must implement DialogMarkerTypeFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("마커 타입 정보 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View retView = inflater.inflate(R.layout.fragment_dialog_marker_type, null);
        textViewMarkerTypeTitle = (TextView)retView.findViewById(R.id.fragment_dialog_marker_type_title);
        textViewMarkerTypeMemo = (TextView)retView.findViewById(R.id.fragment_dialog_marker_type_memo);

        //Log.d("DialogMarkerTypeFra", "d" + getArguments().getString("title") + "/" + getArguments().getString("memo"));
        //setting textView
        textViewMarkerTypeTitle.setText(getArguments().getString("title"));
        textViewMarkerTypeMemo.setText(getArguments().getString("memo"));



        builder.setView(retView)
                // Add action button
                .setPositiveButton("수정", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogMarkerTypeListener.modifyMarkerType();
                    }
                })
                .setNeutralButton("삭제", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogMarkerTypeListener.deleteMarkerType();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogMarkerTypeFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            dialogMarkerTypeListener = (DialogMarkerTypeFragmentListener) getTargetFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogMarkerTypeFragmentListener");
        }
    }
    */
}
