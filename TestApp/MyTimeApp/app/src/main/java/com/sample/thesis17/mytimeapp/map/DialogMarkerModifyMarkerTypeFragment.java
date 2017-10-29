package com.sample.thesis17.mytimeapp.map;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */


public class DialogMarkerModifyMarkerTypeFragment extends DialogFragment {
    public interface DialogMarkerModifyMarkerTypeListener{
        //method
        public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList();
        public void setMarkerTypeModifiedDataList(ArrayList<MarkerTypeData> markerTypeList);
        //public void setNewMarkerTypeList(ArrayList<MarkerTypeData> selectedMarkerTypeDataList);
        //public void getNewMarkerTypeList(ArrayList<MarkerTypeData> selectedMarkerTypeDataList);
    }

    private ArrayList<MarkerTypeData> selectedMarkerTypeList = null;

    DialogMarkerModifyMarkerTypeListener dialogMarkerModifyMarkerTypeListener = null;

    private DatabaseHelperMain databaseHelperMain = null;
    private TypeSelectBaseAdapter typeSelectBaseAdapter = null;
    private Context curContext = null;

    private List<MarkerTypeData> markerTypeDataList = null;
    private ArrayList<MarkerTypeDataWithBool> markerTypeDataWithBoolList = null;

    //dao
    Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedMarkerTypeList = dialogMarkerModifyMarkerTypeListener.getMarkerTypeModifiedDataList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        try{
            daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();	//get
            if(daoMarkerTypeDataInteger != null){
                markerTypeDataList = daoMarkerTypeDataInteger.queryForAll();
            }
            else{
                Log.d("DialogMTypeSelectFrag", "cannot open daoMarkerTypeDataInteger");
            }
        }
        catch(SQLException e){
            Log.d("MarkerTypeDataFragment", "debugPrintDAOInfo SQL exception");
        }

        try {
            dialogMarkerModifyMarkerTypeListener = (DialogMarkerModifyMarkerTypeListener) context;
            /*Activity tempActivity = getActivity();
            if(tempActivity != null && tempActivity instanceof MapsActivity){
                selectedMarkerTypeDataList = ((MapsActivity) tempActivity).getNewMarkerTypeList();    //다시 수정하는 경우 기존의 type bit를 MapsActivity에서 가져온다.
            }*/
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).toString() + "must implement DialogMarkerTypeSelectListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogStyle);
        builder.setTitle("마커 타입 선택 변경 Dialog");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼

        View retView = inflater.inflate(R.layout.fragment_dialog_marker_type_select, null);
        // textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_newmarker_marker_name);

        //get argument with MarkerTypeData and check
        Boolean isAdded = false;
        MarkerTypeDataWithBool tempMarkerTypeDataWithBool = null;
        markerTypeDataWithBoolList = new ArrayList<>();
        for(MarkerTypeData data : markerTypeDataList){
            for(MarkerTypeData compData : selectedMarkerTypeList){
                if(data.equals(compData)){
                    tempMarkerTypeDataWithBool = new MarkerTypeDataWithBool(data, true);
                    markerTypeDataWithBoolList.add(tempMarkerTypeDataWithBool);
                    isAdded = true;
                    Log.d("DialogMarkerTypeSelect", "added in equals " + tempMarkerTypeDataWithBool.getMarkerTypeData().getStrTypeName());
                    break;
                }
            }
            if(!isAdded){
                tempMarkerTypeDataWithBool = new MarkerTypeDataWithBool(data, false);
                markerTypeDataWithBoolList.add(tempMarkerTypeDataWithBool);
                Log.d("DialogMarkerTypeSelect", "added out " + tempMarkerTypeDataWithBool.getMarkerTypeData().getStrTypeName());
            }
            isAdded = false;
        }

        Log.d("DialogMarkerTypeSelect", "len" + markerTypeDataList.size() + "/" + selectedMarkerTypeList.size() + "/" + markerTypeDataWithBoolList.size() + "/" );

        typeSelectBaseAdapter = new TypeSelectBaseAdapter(markerTypeDataWithBoolList);

        ListView listView = (ListView)retView.findViewById(R.id.fragment_dialog_marker_type_listView);
        if(listView == null){
            Log.d("DialogMarkerTypeSelect", "no listview");
        }
        listView.setAdapter(typeSelectBaseAdapter);

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<MarkerTypeData> tempMarkerTypeDataList = new ArrayList<>();
                        for(MarkerTypeDataWithBool data :markerTypeDataWithBoolList){
                            if(data.isbSelected()){
                                tempMarkerTypeDataList.add(data.getMarkerTypeData());
                            }
                        }
                        dialogMarkerModifyMarkerTypeListener.setMarkerTypeModifiedDataList(tempMarkerTypeDataList);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerModifyMarkerTypeFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }




    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(curContext);
        }

        return databaseHelperMain;
    }

}

