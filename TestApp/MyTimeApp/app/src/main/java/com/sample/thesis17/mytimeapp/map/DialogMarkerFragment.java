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
import android.widget.Spinner;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMarkerFragment extends DialogFragment {

    public interface DialogMarkerListener{
        //method
        public void modifyMarker();
        public void deleteMarker();
    }

    DialogMarkerListener dialogMarkerListener = null;

    TextView textViewMarkerName = null;
    TextView textViewMarkerMemo = null;
    Spinner spinnerSelectType = null;

    ArrayList<MarkerTypeData> newMarkerMarkerTypeList = null;   //deprecated

    Context curContext = null;


    private DatabaseHelperMain databaseHelperMain = null;


    Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = null;
    Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    Dao<MarkerMarkerTypeData, Integer> daoMarkerMarkerTypeDataInteger = null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //하단 확인, 취소 버튼
        View retView = inflater.inflate(R.layout.fragment_dialog_newmarker, null);
        textViewMarkerName = (TextView)retView.findViewById(R.id.fragment_dialog_marker_marker_name);
        textViewMarkerMemo = (TextView)retView.findViewById(R.id.fragment_dialog_marker_marker_memo);
        spinnerSelectType = (Spinner)retView.findViewById(R.id.fragment_dialog_marker_spinner_selectType);


//print
        try{
            //get DB DAO
            daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();
            daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            daoMarkerMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerMarkerTypeData();


            if(daoMarkerTypeDataInteger != null){
                List<MarkerTypeData> list- = dao-Integer.queryForAll();
                StringBuilder strbList- = new StringBuilder();
                for (- data : listLMData){
                    //Date date = new Date(data.getLillisTimeWritten());
                    //DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                    //String strDateFormatted = formatter.format(date);
                    //strbListLmData.append("lat : ").append(data.getLat()).append("..lng : ").append(data.getLng()).append("..time : ").append(strDateFormatted).append("..Accur : ").append(data.getfAccur());
                }

            }
        }
        catch(SQLException e){
            Log.d("locationService", "debugPrintDAOInfo SQL exception");
        }
        /*spinnerSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMarkerTypeSelectFragment dig = new DialogMarkerTypeSelectFragment();
                dig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogMarkerTypeSelectFragment");
            }
        });
        */

        builder.setView(retView)
                // Add action buttons
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialogMarkerListener.modifyMarker();
                    }
                })
                .setNeutralButton("삭제", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogMarkerListener.deleteMarker();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogMarkerFragment.this.getDialog().cancel();  //cancel dialog
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
        try{
            dialogMarkerListener = (DialogMarkerListener) context;
        }
        catch(ClassCastException e){
            throw new ClassCastException(((Activity)context).toString() + "must implement DialogNewmarkerListener");
        }
    }

    //DialogMarkerTypeSelectFragment -> MapsActivity -> DialogNewmarkerFaragment
    /*public void setNewMarkerMarkerTypeList(ArrayList<MarkerTypeData> markerTypeDataList){
        newMarkerMarkerTypeList = markerTypeDataList;   //deprecated
    }
    */

    @Override
    public void dismiss() {
        super.dismiss();
        databaseHelperMain.close();
    }

    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(curContext);
        }
        return databaseHelperMain;
    }

    //준비된 쿼리문 (singleton)
    private PreparedQuery<MarkerTypeData> markerTypesForMarkerQuery = null;

    //param marker에 해당하는 markerTypeData들을 모두 가져오는 쿼리 실행문. return값은 List
    private List<MarkerTypeData> lookupMarkerTypeForMarker(MarkerData markerData) throws SQLException {
        if (markerTypesForMarkerQuery == null) {
            markerTypesForMarkerQuery = makeMarkerTypesForMarkerQuery();
        }
        markerTypesForMarkerQuery.setArgumentHolderValue(0, markerData);    //아래의 selectArg에 markerData 해당됌.
        return daoMarkerTypeDataInteger.query(markerTypesForMarkerQuery);
    }

    //marker에 해당하는 markerTypes를 가져오는 쿼리문.
    private PreparedQuery<MarkerTypeData> makeMarkerTypesForMarkerQuery() throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<MarkerMarkerTypeData, Integer> markerMarkerTypeQb = daoMarkerMarkerTypeDataInteger.queryBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        markerMarkerTypeQb.selectColumns(MarkerTypeData.ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        markerMarkerTypeQb.where().eq(MarkerMarkerTypeData.MARKERDATA_ID_FIELD_NAME, selectArg);

        // MarkerTypeData dao에서 해당되는 id의 markerTypeData를 모두 가져온다.
        QueryBuilder<MarkerTypeData, Integer> markerTypeDataQb = daoMarkerTypeDataInteger.queryBuilder();
        // where the id matches in the post-id from the inner query
        markerTypeDataQb.where().in(MarkerTypeData.ID_FIELD_NAME, markerMarkerTypeQb);
        return markerTypeDataQb.prepare();
    }


}