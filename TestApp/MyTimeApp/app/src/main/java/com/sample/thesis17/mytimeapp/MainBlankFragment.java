package com.sample.thesis17.mytimeapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperLocationMemory;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;

import java.sql.SQLException;
import java.util.List;

public class MainBlankFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button locationToNullButton = null;

    private String mParam1;
    private String mParam2;

    Context curContext = null;

    private OnFragmentInteractionListener mListener;

    public MainBlankFragment() {
        // Required empty public constructor
    }

    public static MainBlankFragment newInstance(String param1, String param2) {
        MainBlankFragment fragment = new MainBlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelperLocationMemory = getDatabaseHelperLocationMemory();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_main_blank, container, false);
        // Inflate the layout for this fragment
        locationToNullButton = (Button)retView.findViewById(R.id.locationToNull);

        locationToNullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //모든 locationMem bind data null로
                Dao<LocationMemoryData, Integer> locationMemoryDataIntegerDao = null;
                try {
                    locationMemoryDataIntegerDao = databaseHelperLocationMemory.getDaoLocationMemoryData();
                    List<LocationMemoryData> locationMemoryDataList = locationMemoryDataIntegerDao.queryForAll();

                    for(LocationMemoryData fttd : locationMemoryDataList){
                        fttd.setBindedTempHistoryData(null);
                        fttd.setBindedHistoryData(null);
                        fttd.setbDummy(0);
                        locationMemoryDataIntegerDao.update(fttd);
                        Log.d("mainactivity", "LM : " + fttd.toString());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return retView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private DatabaseHelperLocationMemory databaseHelperLocationMemory = null;
    private DatabaseHelperMain databaseHelperMain = null;

    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(curContext);
        }
        return databaseHelperMain;
    }


    private DatabaseHelperLocationMemory getDatabaseHelperLocationMemory(){
        if(databaseHelperLocationMemory == null){
            databaseHelperLocationMemory = DatabaseHelperLocationMemory.getHelper(curContext);
        }
        return databaseHelperLocationMemory;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
