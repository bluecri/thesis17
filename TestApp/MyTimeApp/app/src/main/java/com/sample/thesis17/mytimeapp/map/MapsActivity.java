package com.sample.thesis17.mytimeapp.map;

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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.Dao;
import com.sample.thesis17.mytimeapp.DB.baseClass.DatabaseHelperMain;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerData;
import com.sample.thesis17.mytimeapp.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        DialogMarkerTypeSelectFragment.DialogMarkerTypeSelectListener, DialogNewmarkerFragment.DialogNewmarkerListener {

    private GoogleMap mMap; //mMap 객체

    // side tab에 따른 mode
    private String MARKER_MODE = "markermode";
    private String MOVEMENT_MODE = "movementmode";

    private static String STR_MODE = "markermode";  //marker, movement

    //dbHelperMain
    private DatabaseHelperMain databaseHelperMain = null;

    //새로 생성된 Marker. null이 아닌경우 생성중인 상태.
    private Marker newMarker = null;


    public double CUSTOM_DRADIUS = 30;

    //map 상에 올려진 marker들
    private List<Marker> listMarkerOnMap = new ArrayList<Marker>();

    //state
    String STATE_NONE = "state_none";
    String STATE_CREATE_MARKER_BEFORE_ONMAP = "state_create_marker_before_onmap";
    String STATE_CREATE_MARKER_AFTER_ONMAP = "state_create_marker_after_onmap";
    private String STR_STATE = STATE_NONE;


    //fab button
    FloatingActionButton fabAdd = null,
            fabCancel = null,
            fabList = null;


    public int iNewMarkerType = 0;  //new marker의 marker type.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                if(STR_STATE.equals(STATE_NONE)) {
                    changeState(STATE_CREATE_MARKER_BEFORE_ONMAP);
                }
                else if(STR_STATE.equals(STATE_CREATE_MARKER_AFTER_ONMAP)){
                    //register info window
                }

                Snackbar.make(view, "Click position where new marker will be created", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //cancel button
        fabCancel = (FloatingActionButton) findViewById(R.id.fabCancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(newMarker != null){
                    newMarker.remove();
                    newMarker = null;
                }
                changeState(STATE_NONE);

                Snackbar.make(view, "cancel creating new marker", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);



    }

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
        if (id == R.id.action_settings) {
            return true;
        }

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

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("MapActivity", "onmapready");
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        //load data from markers
        try{
            Dao<MarkerData, Integer> daoMarkerDataInteger = getDatabaseHelperMain().getDaoMarkerData();
            if(daoMarkerDataInteger != null){
                List<MarkerData> listMarkerData = daoMarkerDataInteger.queryForAll();
                //StringBuilder strbList- = new StringBuilder();
                for (MarkerData data : listMarkerData) {
                    Marker mark = map.addMarker(new MarkerOptions()
                            .position(new LatLng(data.getLat(), data.getLng()))
                            .title(data.getStrMarkerName())
                            //.draggable(true)  //load markers cannot be draggable marker
                    );
                    mark.setTag(data);
                    listMarkerOnMap.add(mark);  //register on listMarkerOnMap
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
                MarkerData newMarkerData = new MarkerData(latLng.latitude, latLng.longitude, "name", CUSTOM_DRADIUS, CUSTOM_DRADIUS, 0, "memo", true);
                newMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(newMarkerData.getStrMarkerName())
                        .draggable(true)
                        .snippet("드래그하여 원하는 위치에 마커를 놓은 뒤 '+' 버튼 또는 마커를 누르세요. 이 window를 누르면 window가 사라집니다.")
                );
                newMarker.setTag(newMarkerData);    //MarkerData -> Marker private object
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
            STR_STATE = inStr;
        }
        return;
    }


    //get DB helper singleton
    private DatabaseHelperMain getDatabaseHelperMain(){
        if(databaseHelperMain == null){
            databaseHelperMain = DatabaseHelperMain.getHelper(this);
        }
        return databaseHelperMain;
    }

    //info window click listener
    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
    }

    //marker click listener
    @Override
    public boolean onMarkerClick(Marker marker) {
        //register info window
        return true;
    }


    //fab button hide & show
    public void showFabCancel(){
        CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        //p.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT;
        p.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        p.bottomMargin = 16;
        p.rightMargin = 16;
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

    //DialogMarkerTypeSelectFragment.DialogMarkerTypeSelectListener listener
    @Override
    public void setINewMarkerType(int iMarkerTypesBit) {
        iNewMarkerType = iMarkerTypesBit;
        //iNewMarkerType -> DialogNewmarkerFragment's iNewMarkerMarkerType
        DialogNewmarkerFragment dialogNewmarkerFragment = (DialogNewmarkerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_dialog_newmarker);
        dialogNewmarkerFragment.setINewMarkerMarkerType(iNewMarkerType);
    }

    //get current new marker type from
    public int getINewMarkerType() {
        return iNewMarkerType;
    }

    //DialogNewmarkerFragment.DialogNewmarkerListener listener
    @Override
    public void registerNewMarker(MarkerData data) {
        //TODO : data를 DB에 저장한다.
    }
}
