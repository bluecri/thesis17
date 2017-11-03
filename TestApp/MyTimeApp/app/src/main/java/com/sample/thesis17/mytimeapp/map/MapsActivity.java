package com.sample.thesis17.mytimeapp.map;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperLocationMemory;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.LocationMemoryData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerMarkerTypeData;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.sample.thesis17.mytimeapp.Static.MyMath.CUSTOM_DRADIUS;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, DialogMarkerModifyMarkerTypeFragment.DialogMarkerModifyMarkerTypeListener,
        DialogMarkerTypeSelectFragment.DialogMarkerTypeSelectListener, DialogNewmarkerFragment.DialogNewmarkerListener, DialogNewMarkerTypeFragment.DialogNewMarkerTypeFragmentListener, DialogMarkerFragment.DialogMarkerListener, DialogMarkerModifyFragment.DialogMarkerModifyListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap; //mMap 객체
    SupportMapFragment mMapFragment = null;    //singleton mMapFragment instance
    MarkerTypeDataFragment markerTypeListFragment = null;   //교체할 markerType Fragment

    final static double DEFAULT_LAT = 36.604228;
    final static double DEFAULT_LNG = 127.897339;

    // side tab에 따른 mode
    final private String MARKER_MODE = "markermode";
    final private String MOVEMENT_MODE = "movementmode";
    final private String MARKERTYPE_MODE = "markertypemode";
    private static String STR_MODE = "markermode";  //marker, movement

    //dbHelperMain
    private DatabaseHelperMain databaseHelperMain = null;
    private DatabaseHelperLocationMemory databaseHelperLocationMemory = null;
    private Dao<MarkerData, Integer> daoMarkerDataInteger = null;
    private Dao<MarkerMarkerTypeData, Integer> daoMarkerMarkerTypeDataInteger = null;
    private Dao<MarkerTypeData, Integer> daoMarkerTypeDataInteger = null;
    private Dao<LocationMemoryData, Integer> daoLocationMemoryDataInteger = null;

    //새로 생성된 Marker. null이 아닌경우 생성중인 상태.
    private Marker newMarker = null;
    //선택된 (클릭된) 마커
    private Marker clickedMarker = null;

    //map 상에 올려진 marker들
    private List<Marker> listMarkerOnMap = new ArrayList<Marker>();

    double recentLat, recentLng;
    CameraPosition recentCameraPosition;

    //state
    final String STATE_NONE = "state_none";
    final String STATE_CREATE_MARKER_BEFORE_ONMAP = "state_create_marker_before_onmap";
    final String STATE_CREATE_MARKER_AFTER_ONMAP = "state_create_marker_after_onmap";
    final String STATE_MODIFY_MARKER_POSITION = "state_modify_marker_positoin";
    private String STR_STATE = STATE_NONE;


    //fab button
    FloatingActionButton fabAdd = null,
            fabCancel = null,
            fabList = null;


    public ArrayList<MarkerTypeData> markerTypeDataList = null;  //new marker의 marker type.

    public ArrayList<String> spinnerMarkerTypeDataStringList = null;  //DialogMarkerFragment의 spinner에서 보여질  marker type list
    public ArrayList<MarkerTypeData> markerTypeModifiedDataList = null;  //DialogMarkerModifyFragment에서 수정되는 marker type list



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getDat
        try{
            daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            daoMarkerMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerMarkerTypeData();	//get
            daoMarkerTypeDataInteger = getDatabaseHelperMain().getDaoMarkerTypeData();	//get dao
            daoLocationMemoryDataInteger = getDatabaseHelperLocationMemory().getDaoLocationMemoryData();
        }
        catch(SQLException e){
            Log.d("MapsActivity", "getDao error");
        }

        int lastId = getMaxIdOfLocationMemoryDataTable();
        if(lastId != 0){
            try{
                LocationMemoryData lastLMData = daoLocationMemoryDataInteger.queryForId(lastId);
                recentCameraPosition = new CameraPosition.Builder().target(new LatLng(lastLMData.getLat(), lastLMData.getLng())).zoom((float)10.0).bearing(0).tilt(0).build();

            }
            catch(SQLException e){
                Log.d("MapsActivity", "getDao error");
            }
        }
        else{
            recentCameraPosition = new CameraPosition.Builder().target(new LatLng(DEFAULT_LAT, DEFAULT_LNG)).zoom((float)10.0).bearing(0).tilt(0).build();
        }

        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //map fragment
        mMapFragment = getMMapFragment();

        STR_MODE = "markermode";  //init mode
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_map_framelayout, mMapFragment, "mmap_fragment");
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);     //map fragemnt와 activity sync.

        //list button
        fabList = (FloatingActionButton) findViewById(R.id.fabList);

        fabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //add button
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //menu별 add button 처리
                if(STR_MODE.equals(MARKER_MODE)){
                    if(STR_STATE.equals(STATE_NONE)) {
                        Snackbar.make(view, "Click position where new marker will be created", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        changeState(STATE_CREATE_MARKER_BEFORE_ONMAP);
                    }
                    else if(STR_STATE.equals(STATE_CREATE_MARKER_AFTER_ONMAP)){
                        //register info window
                        DialogNewmarkerFragment dig = new DialogNewmarkerFragment();
                        //TODO : modify tag info(MarkerData's lat, lng) with newMarker position
                        dig.show(((FragmentActivity)MapsActivity.this).getSupportFragmentManager(), "DialogNewmarkerFragment");
                    }
                    else if(STR_STATE.equals(STATE_MODIFY_MARKER_POSITION)){
                        //open DialogMarkerModifyFragment
                        DialogMarkerModifyFragment dig = new DialogMarkerModifyFragment();
                        Bundle arg = new Bundle();
                        MarkerData tempMd = (MarkerData)clickedMarker.getTag();
                        arg.putString("title", tempMd.getStrMarkerName());
                        //TODO : modify tempMd, tag info(MarkerData's lat, lng) with clickedMarker position
                        arg.putString("memo", tempMd.getStrMemo());
                        dig.setArguments(arg);
                        //lat, lng은 나중에 처리
                        dig.show(getSupportFragmentManager(), "DialogMarkerModifyFragment");
                    }

                }
                else if(STR_MODE.equals(MOVEMENT_MODE)){

                }
                else if(STR_MODE.equals(MARKERTYPE_MODE)){
                    //add new markerType with dialog
                    DialogNewMarkerTypeFragment dig = new DialogNewMarkerTypeFragment();
                    dig.show(getSupportFragmentManager(), "DialogNewMarkerTypeFragment");
                }
            }
        });

        //cancel button
        fabCancel = (FloatingActionButton) findViewById(R.id.fabCancel);

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(STR_MODE.equals(MARKER_MODE)){
                    //new marker cancel
                    if(STR_STATE.equals(STATE_CREATE_MARKER_BEFORE_ONMAP) || STR_STATE.equals(STATE_CREATE_MARKER_AFTER_ONMAP)  ){
                        if(newMarker != null){
                            newMarker.remove();
                            newMarker = null;
                        }
                        markerTypeDataList = new ArrayList<>();
                        changeState(STATE_NONE);

                        Snackbar.make(view, "cancel creating new marker", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    //modify marker cancel
                    if(STR_STATE.equals(STATE_MODIFY_MARKER_POSITION)){
                        if(clickedMarker != null){
                            clickedMarker.setDraggable(false);
                            setVisibleAllMarkerOnMap();
                            changeState(STATE_NONE);
                        }
                        Snackbar.make(view, "cancel modify marker", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else if(STR_MODE.equals(MOVEMENT_MODE)){

                }

            }
        });
        hideFabCancel();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get map fragment Handle to MapsActivity
        /*MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);*/
    }

    //nav bar methods

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_marker) {
            changeMode(MARKER_MODE);
        } else if (id == R.id.nav_movement) {
            //movement
            changeMode(MOVEMENT_MODE);

        } else if (id == R.id.nav_markerType) {
            changeMode(MARKERTYPE_MODE);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //map methods

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("MapActivity", "onmapready");
        mMap = map;

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        try{
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e){
            Log.d("mapsActivity", "SecurityException setMyLocationEnabled()");
        }

        FrameLayout curFrameLayoutView = (FrameLayout)findViewById(R.id.content_map_framelayout);
        curFrameLayoutView.measure(View.MeasureSpec.UNSPECIFIED , View.MeasureSpec.UNSPECIFIED);

        Log.d("mmap", "height : " + curFrameLayoutView.getMeasuredHeight() + "/width : " +curFrameLayoutView.getMeasuredWidth() );
        mMap.setPadding(0,0, curFrameLayoutView.getMeasuredHeight()  - 100, curFrameLayoutView.getMeasuredWidth());

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(recentCameraPosition));
        //load data from markers
        try{
            if(daoMarkerDataInteger != null){
                List<MarkerData> listMarkerData = daoMarkerDataInteger.queryForAll();
                //StringBuilder strbList- = new StringBuilder();
                for (MarkerData data : listMarkerData) {
                    Marker mark = null;
                    if(data.isCache() == true){
                        if(data.isInvisible() == false){
                            mark = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(data.getLat(), data.getLng()))
                                            .title(data.getStrMarkerName())
                                    //.draggable(true)  //load markers cannot be draggable marker
                            );
                        }
                        else{
                            mark = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(data.getLat(), data.getLng()))
                                            .title(data.getStrMarkerName())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    //.draggable(true)  //load markers cannot be draggable marker
                            );
                        }

                        mark.setTag(data);
                        listMarkerOnMap.add(mark);  //register on listMarkerOnMap
                    }
                    else{
                        //isCache == false => deleted marker
                    }
                }
            }
        }
        catch(SQLException e){
            Log.d("mapsActivity", "onMapReady MarkerData sql Exception");
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(STR_MODE.equals(MARKER_MODE)) {
            //생성 버튼 누른 뒤
            if(STR_STATE.equals(STATE_CREATE_MARKER_BEFORE_ONMAP)){
                if(newMarker != null) {
                    //remove & create marker again
                    newMarker.remove();
                    newMarker = null;
                }
                //MarkerData(double lat, double lng, String strMarkerName, double dRadius, double dInnerRadius, int iMarkerTypeBit, String strMemo, boolean isCache)
                //TODO : unvisible
                unVisibleAllMarkerOnMap(null);
                MarkerData newMarkerData = new MarkerData(latLng.latitude, latLng.longitude, "name", CUSTOM_DRADIUS, CUSTOM_DRADIUS, "memo", true, false);
                newMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        //.title(newMarkerData.getStrMarkerName())
                        .title("새로운 마커 추가")
                        .draggable(true)
                        .snippet("마커를 오래 누른 뒤 드래그하여 '+' 버튼으로 마커를 추가하세요.")
                );

                newMarker.setTag(newMarkerData);    //MarkerData -> Marker private object
                newMarker.showInfoWindow();
                changeState(STATE_CREATE_MARKER_AFTER_ONMAP);
            }
        }
        else if(STR_MODE.equals(MOVEMENT_MODE)){

        }
    }

    //side nav mode change function
    public void changeMode(String inStr){
        if(STR_MODE.equals(inStr)){
            //same mode
        }
        else{
            if(inStr.equals(MARKERTYPE_MODE)){
                hideFabList();
                hideFabCancel();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_map_framelayout, getMarkerTypeListFragment(), "marker_type_list_fragment");
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else if(inStr.equals(MARKER_MODE)){
                if(STR_MODE.equals(MOVEMENT_MODE)){
                    STR_MODE = inStr;
                    return;
                }
                showFabList();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_map_framelayout, getMMapFragment(), "mmap_fragment");
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mMapFragment.getMapAsync(this);     //map fragemnt와 activity sync.
            }
            else if(inStr.equals(MOVEMENT_MODE)){
                if(STR_MODE.equals(MARKER_MODE)){
                    STR_MODE = inStr;
                    return;
                }
                showFabList();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_map_framelayout, getMMapFragment(), "mmap_fragment");
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mMapFragment.getMapAsync(this);     //map fragemnt와 activity sync.


            }
            STR_MODE = inStr;
        }
        return;
    }

    //side nav mode change function
    public void changeState(String inStr){
        if(STR_STATE.equals(inStr)){
            //same mode
        }
        else{
            if(inStr.equals(STATE_NONE)) {
                //fabCancel.hide();
                hideFabCancel();
                //fabList.show();
                showFabList();
                //TODO : view
                setVisibleAllMarkerOnMap();
            }
            else if(inStr.equals(STATE_CREATE_MARKER_BEFORE_ONMAP)){
                //fabList.hide();
                hideFabList();
                //fabCancel.show();
                showFabCancel();

            }
            else if(inStr.equals(STATE_CREATE_MARKER_AFTER_ONMAP)){
                //fabList.hide();
                hideFabList();
                //fabCancel.show();
                showFabCancel();
            }
            else if(inStr.equals(STATE_MODIFY_MARKER_POSITION)){
                hideFabList();
                showFabCancel();
            }
            STR_STATE = inStr;
        }
        return;
    }



    //info window click listener
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(STR_STATE.equals(STATE_MODIFY_MARKER_POSITION)){
            //마커 수정중에는 마커 정보창을 띄우지 않는다.
            return;
        }
        if(newMarker != null){
            if(marker.equals(newMarker)){
                marker.hideInfoWindow();
                return;
            }
        }

        clickedMarker = marker; //선택된 마커 setting
        //init
        List<MarkerTypeData> tempDataList = null;
        spinnerMarkerTypeDataStringList = null;
        markerTypeModifiedDataList = null;

        spinnerMarkerTypeDataStringList = new ArrayList<String>();
        markerTypeModifiedDataList = new ArrayList<MarkerTypeData>();
        spinnerMarkerTypeDataStringList.add("선택된 마커타입 리스트");

        MarkerData tagMarkerData = (MarkerData)marker.getTag();

        //show marker info dialog
        try{
            tempDataList = lookupMarkerTypeForMarker(tagMarkerData);  //DialogMarkerFragment의 spinner에서 보여질  marker type list
            Log.d("MapsActivity", "count.." + tempDataList.size());
            for(MarkerTypeData mtd : tempDataList){

                markerTypeModifiedDataList.add((MarkerTypeData)mtd);    //DialogMarkerModifyFragment에서 수정되는 marker type list
                spinnerMarkerTypeDataStringList.add(mtd.getStrTypeName());
/*
                try{
                    markerTypeModifiedDataList.add((MarkerTypeData)mtd.clone());    //DialogMarkerModifyFragment에서 수정되는 marker type list
                    spinnerMarkerTypeDataStringList.add(mtd.getStrTypeName());
                }
                catch(CloneNotSupportedException e){
                    Log.d("MapsActivity", "markerTypeModifiedDataList clone error");
                }
                */
            }
            //spinnerMarkerTypeDataList = new ArrayList<>(markerTypeModifiedDataList);  //not copy. only string copy
        }
        catch(SQLException e){
            Log.d("MapsActivity", "spinnerMarkerTypeDataList, markerTypeModifiedDataList SQL EXCEPTION");
        }

        //open dialog
        DialogMarkerFragment dig = new DialogMarkerFragment();
        Bundle arg = new Bundle();
        arg.putString("title", tagMarkerData.getStrMarkerName());
        arg.putString("memo", tagMarkerData.getStrMemo());
        arg.putDouble("lat", tagMarkerData.getLat());
        arg.putDouble("lng", tagMarkerData.getLng());
        dig.setArguments(arg);
        dig.show(getSupportFragmentManager(), "DialogMarkerFragment");
    }

    //marker click listener
    @Override
    public boolean onMarkerClick(Marker marker) {
        //show window
        return false;
    }

    //fab button hide & show
    public void showFabCancel(){
        CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        //p.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT;
        p.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        p.bottomMargin = 16;
        p.rightMargin = 16;
        p.leftMargin = 16;
        p.topMargin = 16;
        //p.setAnchorId(R.id.include);
        //p.anchorGravity = Gravity.BOTTOM | Gravity.END;

        fabCancel.setLayoutParams(p);
        fabCancel.setVisibility(View.VISIBLE);
    }
    public void hideFabCancel(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabCancel.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fabCancel.setLayoutParams(p);
        fabCancel.setVisibility(View.GONE);
    }
    public void showFabList(){
        CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        p.bottomMargin = 16;
        p.rightMargin = 16;
        p.leftMargin = 16;
        p.topMargin = 16;
        //p.anchorGravity = Gravity.BOTTOM | Gravity.LEFT;
        //p.setAnchorId(R.id.include);
        //p.anchorGravity = Gravity.BOTTOM | Gravity.END;
        //anchorGravity 사용시 snackbar에 따라 위치가 변경되므로 규칙적이지 않게 움직임.

        fabList.setLayoutParams(p);
        fabList.setVisibility(View.VISIBLE);
    }
    public void hideFabList(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabList.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fabList.setLayoutParams(p);
        fabList.setVisibility(View.GONE);
    }

    //get MapFragment with singleton
    private SupportMapFragment getMMapFragment(){
        if( mMapFragment == null){
            mMapFragment = SupportMapFragment.newInstance();
        }
        return mMapFragment;
    }
    //get MarkerTypeListFragment with singleton
    private MarkerTypeDataFragment getMarkerTypeListFragment(){
        if( markerTypeListFragment == null){
            markerTypeListFragment = new MarkerTypeDataFragment();
        }
        return markerTypeListFragment;
    }

    //DialogMarkerTypeSelectFragment.DialogMarkerTypeSelectListener listener
    @Override
    public void setNewMarkerTypeList(ArrayList<MarkerTypeData> selectedMarkerTypeDataList){
        markerTypeDataList = selectedMarkerTypeDataList;
        Log.d("MapsActivity", "len : " + markerTypeDataList.size());
        //iNewMarkerType -> DialogNewmarkerFragment's iNewMarkerMarkerType
        //DialogNewmarkerFragment dialogNewmarkerFragment = (DialogNewmarkerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_dialog_newmarker);
        //dialogNewmarkerFragment.setNewMarkerMarkerTypeList(markerTypeDataList);
    }

    //get current new marker type from
    public ArrayList<MarkerTypeData> getNewMarkerTypeList() {
        if(markerTypeDataList == null){
            markerTypeDataList = new ArrayList<>();
        }
        return markerTypeDataList;
    }

    //DialogNewmarkerFragment.DialogNewmarkerListener listener
    @Override
    public void registerNewMarker(MarkerData data) {
        //setting current lat lng to newMarker
        data.setLat(newMarker.getPosition().latitude);
        data.setLng(newMarker.getPosition().longitude);

        //TODO : data를 DB에 저장한다. markerTypeDataList를 이용하여 MarkerMarkerTypeData에 등록하는 과정 포함.
        try{
            if(daoMarkerDataInteger != null){
                daoMarkerDataInteger.create(data);	//save data
                ArrayList<MarkerTypeData> tempMarkerTypeList = getNewMarkerTypeList();
                Log.d("MapsActivity", "lenss" + tempMarkerTypeList.size());
                for(MarkerTypeData mtd : tempMarkerTypeList){
                    MarkerMarkerTypeData tempMarkerMarkerTypeData = new MarkerMarkerTypeData(data, mtd);
                    daoMarkerMarkerTypeDataInteger.create(tempMarkerMarkerTypeData);
                }
            }
        }
        catch(SQLException e){
            Log.d("MapsActivity", "debugPrintDAOInfo SQL exception");
        }

        newMarker.remove(); //remove new temp marker from map

        Marker mark = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(data.getLat(), data.getLng()))
                        .title(data.getStrMarkerName())
                //.draggable(true)  //load markers cannot be draggable marker
        );
        mark.setTag(data);
        listMarkerOnMap.add(mark);
        newMarker = null;   //remove new marker
        changeState(STATE_NONE);
    }

    //button -> MarkerTypeCreate -> DialogNewMarkerTypeFragemnt
    @Override
    public void createNewMarkerType(MarkerTypeData mtd) {
        try{
            daoMarkerTypeDataInteger.create(mtd);
            //MarkerTypeDataFragment dialogMarkerTypeDataFragment = (MarkerTypeDataFragment)getFragmentManager().findFragmentById(R.id.fragment_markertypedata_list);
            MarkerTypeDataFragment dialogMarkerTypeDataFragment = (MarkerTypeDataFragment)getSupportFragmentManager().findFragmentByTag("marker_type_list_fragment");
            dialogMarkerTypeDataFragment.updateViewWithAdd(mtd);
        }
        catch(SQLException e){
            Log.d("MarkerTypeDataFragment", "createNewMarkerType SQL Exception");
        }

    }

    public void unVisibleAllMarkerOnMap(Marker exceptMarker){
        if(exceptMarker != null){
            for(Marker marker : listMarkerOnMap){
                if(exceptMarker.equals(marker)){
                    continue;
                }
                marker.setVisible(false);
            }
        }
        else{
            for(Marker marker : listMarkerOnMap){
                marker.setVisible(false);
            }
        }
    }
    public void setVisibleAllMarkerOnMap(){
        for(Marker marker : listMarkerOnMap){
            marker.setVisible(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(databaseHelperMain != null){
            databaseHelperMain.close();
            databaseHelperMain = null;
        }
        if(databaseHelperLocationMemory != null){
            databaseHelperLocationMemory.close();
            databaseHelperLocationMemory = null;
        }
    }

    //DialogMarkerFragment listener
    @Override
    public void modifyMarker() {
        unVisibleAllMarkerOnMap(clickedMarker);
        Snackbar.make(findViewById(android.R.id.content), "변경하고자하는 위치에 마커를 놓고 '+' 버튼을 누르세요.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        changeState(STATE_MODIFY_MARKER_POSITION);
        clickedMarker.setDraggable(true);
        clickedMarker.setVisible(true);
    }
    @Override
    public void deleteMarker() {
        clickedMarker.remove();
        if(clickedMarker != null){
            MarkerData tagMarkerData = (MarkerData)clickedMarker.getTag();
            try{
                //deleteMarkerTypesForMarker(tagMarkerData);      //type delete
                if(tagMarkerData != null){
                    tagMarkerData.setCache(false);
                    daoMarkerDataInteger.update(tagMarkerData); //marker delete
                }
            }
            catch(SQLException e){
                Log.d("MapsActivity", "deleteMarker SQL Exception");
            }
            listMarkerOnMap.remove(clickedMarker);
            clickedMarker = null;
        }
    }
    @Override
    public ArrayList<String> getSpinnerMarkerTypeDataStringList() {
        return spinnerMarkerTypeDataStringList;
    }


    //수정 완료된 marker update. DialogMarkerModifyListener listener
    @Override
    public void modifyMarkerComplete(String title, String memo) {
        //with clickedMarker.getPosition & markerTypeModifiedDataList & String 2
        MarkerData modifiedMarkerData = (MarkerData)clickedMarker.getTag();
        modifiedMarkerData.setStrMarkerName(title);
        modifiedMarkerData.setStrMemo(memo);
        modifiedMarkerData.setLat(clickedMarker.getPosition().latitude);
        modifiedMarkerData.setLng(clickedMarker.getPosition().longitude);
        clickedMarker.setTag(modifiedMarkerData);       //NEED
        clickedMarker.setSnippet("다음 이름으로 변경됌 : " + title);    //name
        try{
            daoMarkerDataInteger.update(modifiedMarkerData);
            //delete markerType with modifiedMarkerData
            deleteMarkerTypesForMarker(modifiedMarkerData);

            //add markerTypeModifiedDataList to modifiedMarkerData
            for(MarkerTypeData mtd : markerTypeModifiedDataList){
                MarkerMarkerTypeData tempMarkerMarkerTypeData = new MarkerMarkerTypeData(modifiedMarkerData, mtd);
                daoMarkerMarkerTypeDataInteger.create(tempMarkerMarkerTypeData);
            }
        }
        catch(SQLException e){
            Log.d("MapsActivity", "modifyMarkerComplete SQL EXCEPTION");
        }
        changeState(STATE_NONE);
        Snackbar.make(findViewById(android.R.id.content), "마커 정보 수정 완료", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    //DialogMarkerModifyMarkerTypeFragment listener
    @Override
    public ArrayList<MarkerTypeData> getMarkerTypeModifiedDataList() {
        return markerTypeModifiedDataList;
    }
    @Override
    public void setMarkerTypeModifiedDataList(ArrayList<MarkerTypeData> markerTypeList) {
        markerTypeModifiedDataList = markerTypeList;
    }



    //get DB helper singleton
    private DatabaseHelperMain getDatabaseHelperMain() {
        if (databaseHelperMain == null) {
            databaseHelperMain = DatabaseHelperMain.getHelper(this);
        }
        return databaseHelperMain;
    }


    private DatabaseHelperLocationMemory getDatabaseHelperLocationMemory(){
        if(databaseHelperLocationMemory == null){
            databaseHelperLocationMemory = DatabaseHelperLocationMemory.getHelper(this);
        }
        return databaseHelperLocationMemory;
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
        markerMarkerTypeQb.selectColumns(MarkerMarkerTypeData.MARKERTYPEDATA_ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        markerMarkerTypeQb.where().eq(MarkerMarkerTypeData.MARKERDATA_ID_FIELD_NAME, selectArg);

        // MarkerTypeData dao에서 해당되는 id의 markerTypeData를 모두 가져온다.
        QueryBuilder<MarkerTypeData, Integer> markerTypeDataQb = daoMarkerTypeDataInteger.queryBuilder();
        // where the id matches in the post-id from the inner query
        markerTypeDataQb.where().in(MarkerTypeData.ID_FIELD_NAME, markerMarkerTypeQb);
        return markerTypeDataQb.prepare();
    }


    //준비된 쿼리문 (singleton)
    private PreparedDelete<MarkerMarkerTypeData> markerMarkerTypeDatasForMarkerDeleteQuery = null;

    //param marker에 해당하는 markerTypeData들을 모두 삭제하는 쿼리 실행문.
    private void deleteMarkerTypesForMarker(MarkerData markerData) throws SQLException {
        if (markerMarkerTypeDatasForMarkerDeleteQuery == null) {
            markerMarkerTypeDatasForMarkerDeleteQuery = makeMarkerMarkerTypeDatasForMarkerDeleteQuery();
        }
        markerMarkerTypeDatasForMarkerDeleteQuery.setArgumentHolderValue(0, markerData);    //아래의 selectArg에 markerData 해당됌.
        daoMarkerMarkerTypeDataInteger.delete(markerMarkerTypeDatasForMarkerDeleteQuery);
        return;
    }

    private PreparedDelete<MarkerMarkerTypeData> makeMarkerMarkerTypeDatasForMarkerDeleteQuery() throws SQLException {
        // build our inner query for UserPost objects
        DeleteBuilder<MarkerMarkerTypeData, Integer> markerMarkerTypeQb = daoMarkerMarkerTypeDataInteger.deleteBuilder();

        // marker에 해당하는 markerType.id를 선택한다.
        //markerMarkerTypeQb.selectColumns(MarkerTypeData.ID_FIELD_NAME);
        SelectArg selectArg = new SelectArg();
        // 검색 조건으로 바로 marker를 setting 할 수 있다.
        markerMarkerTypeQb.where().eq(MarkerMarkerTypeData.MARKERDATA_ID_FIELD_NAME, selectArg);
        return markerMarkerTypeQb.prepare();
    }

    private int getMaxIdOfLocationMemoryDataTable(){
        //referemce " https://stackoverflow.com/questions/15876408/query-for-last-id-in-table : how to get last record in ormlite
        QueryBuilder<LocationMemoryData, Integer> qb = daoLocationMemoryDataInteger.queryBuilder().selectRaw("max("+LocationMemoryData.LOCATIONMEMORY_ID_FIELD_NAME+")");
        String[] columns = new String[0];
        try {
            GenericRawResults<String[]> results = daoLocationMemoryDataInteger.queryRaw(qb.prepareStatementString());
            columns = results.getFirstResult();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (columns.length == 0) {
            // NOTE: there are not any rows in table
            return 0;
        }
        return Integer.parseInt(columns[0]);
    }

    @Override
    public void onCameraIdle() {
        if(mMap != null){
            CameraPosition tempCurCameraPosition = mMap.getCameraPosition();
            recentCameraPosition = tempCurCameraPosition;
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(STR_STATE.equals(STATE_MODIFY_MARKER_POSITION)){
            if(clickedMarker != null){
                Log.d("clickedMarker", "click : " + clickedMarker.getPosition().toString() + "/" + marker.getPosition());
                clickedMarker.setPosition(marker.getPosition());
            }
        }
        else if(STR_STATE.equals(STATE_CREATE_MARKER_BEFORE_ONMAP)){
                if(newMarker != null){
                    Log.d("newMarker", "click : " + newMarker.getPosition().toString() + "/" + marker.getPosition());
                    newMarker.setPosition(marker.getPosition());
                }
            }
    }
}
