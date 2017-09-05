package com.sample.thesis17.mytimeapp.map;

import android.app.Activity;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.map.dummy.DummyContent;
import com.sample.thesis17.mytimeapp.map.dummy.DummyContent.DummyItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MarkerTypeDataFragment extends Fragment implements MyMarkerTypeDataRecyclerViewAdapter.OnListFragmentInteractionClickListener, DialogMarkerTypeFragment.DialogMarkerTypeFragmentListener, DialogMarkerTypeModifyFragment.DialogMarkerTypeModifyFragmentListener
{

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private DatabaseHelperMain databaseHelperMain = null;
    private Context curContext = null;
    private List<MarkerTypeData> markerTypeDataList = null;

    private MyMarkerTypeDataRecyclerViewAdapter markerTypeDataFragmentAdapter = null;

    private MarkerTypeData selectedMarkerTypeData = null;
    private int selectedMarkerTypeDataPos = 0;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MarkerTypeDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_markertypedata_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if(markerTypeDataList == null){
                Log.d("MarkerTypeDataFragment", "markerTypeDataList is null..");
            }
            markerTypeDataFragmentAdapter = new MyMarkerTypeDataRecyclerViewAdapter(markerTypeDataList, this);
            //arraylist to first param
            recyclerView.setAdapter(markerTypeDataFragmentAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        curContext = context;

        Log.d("MarkerTypeDataFragment", "onAttach");


        try{
            Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();	//get
            if(daoMarkerTypeDataInteger != null){
                markerTypeDataList = daoMarkerTypeDataInteger.queryForAll();
            }
            else{
                Log.d("MarkerTypeDataFragment", "cannot open daoMarkerTypeDataInteger");
            }
        }
        catch(SQLException e){
            Log.d("MarkerTypeDataFragment", "debugPrintDAOInfo SQL exception");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }






    public void updateViewWithAdd(MarkerTypeData mtd){
        markerTypeDataList.add(mtd);
        //update list view
        markerTypeDataFragmentAdapter.notifyDataSetChanged();
    }



    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(curContext);
        }

        return databaseHelperMain;
    }


    //adapter list중 하나가 클릭되었을 때 동작 method
    @Override
    public void onListFragmentInteraction(MarkerTypeData item, int pos) {
        Log.d("MarkerTypeDataFragment", "data : " + item.getStrTypeName() + " / " + item.getStrMemo());
        DialogMarkerTypeFragment dig = new DialogMarkerTypeFragment();
        Bundle bundleArg = new Bundle();
        bundleArg.putString("title", item.getStrTypeName());
        bundleArg.putString("memo", item.getStrMemo());
        dig.setArguments(bundleArg);
        dig.setTargetFragment(this, 0);
        selectedMarkerTypeData = item;      //set selected data for delete, modify
        selectedMarkerTypeDataPos = pos;
        Log.d("MarkerTypeDataF", "data, pos : " + selectedMarkerTypeDataPos + " " + selectedMarkerTypeData.getStrTypeName());
        dig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogMarkerTypeFragment");

    }

    // listeners from DialogMarkerTypeFragment
    @Override
    public void modifyMarkerType() {
        if(selectedMarkerTypeData != null){
            DialogMarkerTypeModifyFragment dig = new DialogMarkerTypeModifyFragment();
            Bundle bundleArg = new Bundle();
            bundleArg.putString("title", selectedMarkerTypeData.getStrTypeName());
            bundleArg.putString("memo", selectedMarkerTypeData.getStrMemo());
            dig.setArguments(bundleArg);
            dig.setTargetFragment(this, 0);
            dig.show(((FragmentActivity)curContext).getSupportFragmentManager(), "DialogMarkerTypeModifyFragment");
            //modify 창 open
        }
    }
    @Override
    public void deleteMarkerType() {
        if(selectedMarkerTypeData != null){
            try{
                Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();	//get
                if(daoMarkerTypeDataInteger != null){
                    daoMarkerTypeDataInteger.delete(selectedMarkerTypeData);
                    markerTypeDataList.remove(selectedMarkerTypeDataPos);
                    markerTypeDataFragmentAdapter.notifyItemRemoved(selectedMarkerTypeDataPos);
                    //markerTypeDataFragmentAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("MarkerTypeDataFragment", "cannot open daoMarkerTypeDataInteger");
                }
            }
            catch(SQLException e){
                Log.d("MarkerTypeDataFragment", "debugPrintDAOInfo SQL exception");
            }

        }
    }

    //listener from DialogMarkerTypeModifyFragment
    @Override
    public void modifyMarkerTypeData(MarkerTypeData mtd) {
        //modify 창 open 후 save시
        try{
            Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();	//get
            if(daoMarkerTypeDataInteger != null){
                //dialog에 전달된 정보는 markerTypeData id가 없음. 현재 선택된 data에 id가 존재하므로 그대로 selectedMarkerTypeData를 갱신하여 Dao에 update.
                selectedMarkerTypeData.setStrTypeName(mtd.getStrTypeName());
                selectedMarkerTypeData.setStrMemo(mtd.getStrMemo());
                daoMarkerTypeDataInteger.update(selectedMarkerTypeData);
                markerTypeDataFragmentAdapter.notifyDataSetChanged();
            }
            else{
                Log.d("MarkerTypeDataFragment", "cannot open daoMarkerTypeDataInteger");
            }
        }
        catch(SQLException e){
            Log.d("MarkerTypeDataFragment", "debugPrintDAOInfo SQL exception");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(databaseHelperMain != null){
            databaseHelperMain.close();
            databaseHelperMain = null;
        }
    }
}

