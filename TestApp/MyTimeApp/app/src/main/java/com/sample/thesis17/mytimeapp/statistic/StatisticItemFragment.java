package com.sample.thesis17.mytimeapp.statistic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.HistoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_DAY_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_HOUR_MILLIS;
import static com.sample.thesis17.mytimeapp.Static.MyMath.LONG_MIN_MILLIS;


public class StatisticItemFragment extends Fragment {

    public static List<ImageNameIntItem> IMAGE_NAME_INT_ITEM_LIST = new ArrayList<ImageNameIntItem>();
    public static int IMAGE_NAME_INT_ITEM_LIST_SIZE = 7;
    static{
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_action_sleep, "sleep icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_school, "school icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_breifcase, "breifcase icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_home, "home icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_music, "music icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_note, "note icon"));
        IMAGE_NAME_INT_ITEM_LIST.add(new ImageNameIntItem(R.drawable.ic_type_run, "run icon"));
    }

    Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = null;

    Dao<MarkerMarkerTypeData, Integer> daoMarkerMarkerTypeInteger = null;
    Dao<HistoryData, Integer> daoHistoryDataInteger = null;


    int mColumnCount = 1;
    Context curContext = null;

    //private OnListFragmentInteractionListener mListener;

    List<MarkerTypeData> markerTypeDataList = null;
    List<DummyItem> listDummyContentItem = null;

    public StatisticItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();
            daoMarkerMarkerTypeInteger = getDatabaseHelperMain().getDaoMarkerMarkerTypeData();
            daoHistoryDataInteger = getDatabaseHelperMain().getDaoHistoryData();
            markerTypeDataList = daoMarkerTypeDataInteger.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statisticitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            mColumnCount = 1;
            //get dao & create items
            listDummyContentItem = new ArrayList<>();
            for(MarkerTypeData mtd : markerTypeDataList){
                List<HistoryData> listHDTotalTime = null;
                List<HistoryData> listHDLimitedTime = null;
                try {
                    listHDTotalTime =getListHistoryDataWithMarkerType(mtd);
                    listHDLimitedTime = getListHistoryDataWithMarkerTypeWithTime(mtd, System.currentTimeMillis() + 9 * LONG_HOUR_MILLIS - 7 * LONG_DAY_MILLIS, System.currentTimeMillis()  + 9 * LONG_HOUR_MILLIS);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                long totalTime = 0, limitedTime = 0;
                for(HistoryData hd : listHDTotalTime){

                    totalTime += hd.getlEndTime() - hd.getlStartTime();
                    Log.d("statistic" , "total add : " + totalTime);
                }
                for(HistoryData hd : listHDLimitedTime){
                    limitedTime += hd.getlEndTime() - hd.getlStartTime();
                }
                String totalTimeString = "총 " + totalTime/LONG_HOUR_MILLIS + "시간 " + (totalTime%LONG_HOUR_MILLIS/LONG_MIN_MILLIS) + "분";
                String weekTimeStr = "" + limitedTime/LONG_HOUR_MILLIS + "시간 " + (limitedTime%LONG_HOUR_MILLIS/LONG_MIN_MILLIS) + "분";

                listDummyContentItem.add(new DummyItem(mtd.getStrTypeName(), totalTimeString, weekTimeStr, IMAGE_NAME_INT_ITEM_LIST.get(mtd.getImageIdx()).getImageInt()));
            }

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new MystatisticItemRecyclerViewAdapter(listDummyContentItem));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        curContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface OnListFragmentInteractionListener {
    }

    private DatabaseHelperMain databaseHelperMain = null;

    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(curContext);
        }
        return databaseHelperMain;
    }


    private PreparedQuery<HistoryData> markerTypesForTotalTimeQuery = null;

    private List<HistoryData> getListHistoryDataWithMarkerType(MarkerTypeData mtd) throws SQLException {
        if (markerTypesForTotalTimeQuery == null) {
            markerTypesForTotalTimeQuery = makeMarkerTypesForMarkerQuery();
        }
        markerTypeData.setValue(mtd);
        return daoHistoryDataInteger.query(markerTypesForTotalTimeQuery);
    }

    SelectArg markerTypeData = new SelectArg();

    private PreparedQuery<HistoryData> makeMarkerTypesForMarkerQuery() throws SQLException {

        QueryBuilder<MarkerMarkerTypeData, Integer> markerMarkerTypeQb = daoMarkerMarkerTypeInteger.queryBuilder();

        markerMarkerTypeQb.selectColumns(MarkerMarkerTypeData.MARKERDATA_ID_FIELD_NAME);

        markerMarkerTypeQb.where().eq(MarkerMarkerTypeData.MARKERTYPEDATA_ID_FIELD_NAME, markerTypeData);   //markers

        QueryBuilder<HistoryData, Integer> historyDataQb = daoHistoryDataInteger.queryBuilder();

        historyDataQb.where().in(HistoryData.HD_MD_ID_FIELD_NAME, markerMarkerTypeQb);
        return historyDataQb.prepare();
    }

    private PreparedQuery<HistoryData> markerTypesForLimitedTimeQuery = null;

    SelectArg markerTypeData2 = new SelectArg();
    SelectArg startTimeArg = new SelectArg();
    SelectArg endTimeArg = new SelectArg();

    private List<HistoryData> getListHistoryDataWithMarkerTypeWithTime(MarkerTypeData mtd, long startTime, long endTime) throws SQLException {
        if (markerTypesForLimitedTimeQuery == null) {
            markerTypesForLimitedTimeQuery = makeMarkerTypesForMarkerWithTimeQuery();
        }
        markerTypeData2.setValue(mtd);
        startTimeArg.setValue(startTime);
        endTimeArg.setValue(endTime);
        return daoHistoryDataInteger.query(markerTypesForLimitedTimeQuery);
    }

    private PreparedQuery<HistoryData> makeMarkerTypesForMarkerWithTimeQuery() throws SQLException {

        QueryBuilder<MarkerMarkerTypeData, Integer> markerMarkerTypeQb = daoMarkerMarkerTypeInteger.queryBuilder();

        markerMarkerTypeQb.selectColumns(MarkerMarkerTypeData.MARKERDATA_ID_FIELD_NAME);

        markerMarkerTypeQb.where().eq(MarkerMarkerTypeData.MARKERTYPEDATA_ID_FIELD_NAME, markerTypeData2);   //markers

        QueryBuilder<HistoryData, Integer> historyDataQb = daoHistoryDataInteger.queryBuilder();

        historyDataQb.where().in(HistoryData.HD_MD_ID_FIELD_NAME, markerMarkerTypeQb).and().gt(HistoryData.HD_MD_STARTTIME_FIELD_NAME, startTimeArg).and().lt(HistoryData.HD_MD_ENDTIME_FIELD_NAME, endTimeArg);
        return historyDataQb.prepare();
    }

}
