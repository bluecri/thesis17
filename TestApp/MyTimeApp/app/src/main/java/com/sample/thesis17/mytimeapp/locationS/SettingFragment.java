package com.sample.thesis17.mytimeapp.locationS;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int iIntervalMinValue = 0;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //xml content
    Switch switchLocationInfoService;   //Service On/Off
    Switch switchUseGps;                    //Use GPS or not
    Switch switchUseWifi;
    Switch switchUseData;
    SeekBar seekbarServiceInterval;     //Service interval
    TextView textViewIntervalResult;
    //String strIntervalResult = null;

    //setting save
    boolean isUseService, isUseGps, isUseWifi, isUseData;
    int iInterval;

    //pref save & load
    SharedPreferences serviceFragmentSharedPreference = null;   //pref
    SharedPreferences.Editor serviceFragmentSharedPreferenceEditor = null;

    //xml content listener
    CustomCheckedChangeListener checkedListener = null;
    CustomOnSeekbarChangeListener seekbarListener = null;

    //location Manager for service
    //LocationManager locationManager;


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_setting, container, false);   // Before return View, get sub view.

        switchLocationInfoService = (Switch)retView.findViewById(R.id.fragment_setting_switch_onoff);
        switchUseGps = (Switch)retView.findViewById(R.id.fragment_setting_switch_onoffGps);
        switchUseWifi = (Switch)retView.findViewById(R.id.fragment_setting_switch_onoffWifi);
        switchUseData = (Switch)retView.findViewById(R.id.fragment_setting_switch_useData);
        seekbarServiceInterval = (SeekBar)retView.findViewById(R.id.fragment_setting_seekbar_interval);
        textViewIntervalResult = (TextView)retView.findViewById(R.id.fragment_setting_intervalResultTextView);

        //set readPref class
        serviceFragmentSharedPreference = getActivity().getSharedPreferences("locationServicePref",0); //0 == Read & write

        //load pref from preference
        switchLocationInfoService.setChecked(isLocationServiceRunning());
        isUseService = isLocationServiceRunning();
        isUseGps = serviceFragmentSharedPreference.getBoolean("UseGps", false);
        isUseWifi = serviceFragmentSharedPreference.getBoolean("UseWifi", false);
        isUseData = serviceFragmentSharedPreference.getBoolean("UseData", false);
        iInterval = serviceFragmentSharedPreference.getInt("ServiceInterval", iIntervalMinValue);
        //strIntervalResult = String.valueOf(iInterval + iIntervalMinValue).concat(" min");

        textViewIntervalResult.setText(String.valueOf(iInterval+iIntervalMinValue).concat(" min"));

        // default value of xml content
        switchUseGps.setChecked(isUseGps);
        switchUseWifi.setChecked(isUseWifi);
        switchUseData.setChecked(isUseData);
        seekbarServiceInterval.setProgress(iInterval);

        // service 실행중일때 이외의 옵션 disabled
        if(switchLocationInfoService.isChecked()){
            setAllOtherProfEnabled(false);
        }

        //register checked changed listener for xml content
        checkedListener = new CustomCheckedChangeListener();
        switchLocationInfoService.setOnCheckedChangeListener(checkedListener);
        switchUseWifi.setOnCheckedChangeListener(checkedListener);
        switchUseGps.setOnCheckedChangeListener(checkedListener);
        switchUseData.setOnCheckedChangeListener(checkedListener);

        //register seekbarListener for xml content
        seekbarListener = new CustomOnSeekbarChangeListener();
        seekbarServiceInterval.setOnSeekBarChangeListener(seekbarListener);

        //locaton manager for service setting
        //locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class CustomCheckedChangeListener implements Switch.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int viewId = buttonView.getId();
            serviceFragmentSharedPreferenceEditor = serviceFragmentSharedPreference.edit();
            switch(viewId){
                case R.id.fragment_setting_switch_onoff:
                    serviceFragmentSharedPreferenceEditor.putBoolean("locationServicePref", isChecked);
                    // 다시 한번 service running 확인
                    //if(isChecked != isLocationServiceRunning()){
                        if(serviceFragmentSharedPreferenceEditor.commit()){     //success commit
                            isUseService = isChecked;
                            if(isChecked){  //start service
                                setAllOtherProfEnabled(false);
                                int iFlagUseConfig = getFlagUseConfig();

                                Intent intentLocationService = new Intent(getActivity(), locationService.class);
                                intentLocationService.putExtra("iFlagUseConfig", iFlagUseConfig);
                                Log.d("SettingFragment", String.valueOf(iFlagUseConfig));
                                intentLocationService.putExtra("iInterval", iInterval+iIntervalMinValue);
                                intentLocationService.setAction(AllConstants.START_LOCATION_FOREGROUND_ACTION);
                                getActivity().startService(intentLocationService);
                            }
                            else{
                                setAllOtherProfEnabled(true);
                                Intent intentLocationService = new Intent(getActivity(), locationService.class);
                                intentLocationService.setAction(AllConstants.STOP_LOCATION_FOREGROUND_ACTION);
                                getActivity().stopService(intentLocationService);
                            }
                        }
                        else{
                            switchLocationInfoService.setChecked(!isChecked);
                            isUseService = !isChecked;
                        }
                    //}

                    break;
                case R.id.fragment_setting_switch_onoffGps:
                    serviceFragmentSharedPreferenceEditor.putBoolean("UseGps", isChecked);
                    if(serviceFragmentSharedPreferenceEditor.commit()){ //success commit
                        isUseGps = isChecked;
                    }
                    else{
                        switchUseGps.setChecked(!isChecked);
                        isUseGps = !isChecked;
                    }
                    break;
                case R.id.fragment_setting_switch_onoffWifi:
                    serviceFragmentSharedPreferenceEditor.putBoolean("UseWifi", isChecked);
                    if(isChecked == false){
                        serviceFragmentSharedPreferenceEditor.putBoolean("UseData", false);     //config wifi to false, data should also false
                    }

                    if(serviceFragmentSharedPreferenceEditor.commit()){ //success commit
                        isUseWifi = isChecked;
                        if(isChecked == false){
                            switchUseData.setChecked(false);
                            isUseData = false;
                        }
                    }
                    else{
                        isUseWifi = !isChecked;
                        switchUseWifi.setChecked(!isChecked);
                    }
                    break;
                case R.id.fragment_setting_switch_useData:
                    serviceFragmentSharedPreferenceEditor.putBoolean("UseData", isChecked);
                    if(isChecked == true){
                        serviceFragmentSharedPreferenceEditor.putBoolean("UseWifi", true);  //config data to true, wifi should also true
                    }
                    if(serviceFragmentSharedPreferenceEditor.commit()){ //success commit
                        isUseData = isChecked;
                        if(isChecked == true){
                            switchUseWifi.setChecked(true);
                            isUseWifi = true;
                        }
                    }
                    else{
                        isUseData = isChecked;
                        switchUseData.setChecked(!isChecked);
                    }
                    break;

            }

        }
    }



    private class CustomOnSeekbarChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //update text from user or app
            textViewIntervalResult.setText(String.valueOf(progress+iIntervalMinValue).concat(" min"));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            serviceFragmentSharedPreferenceEditor = serviceFragmentSharedPreference.edit();
            serviceFragmentSharedPreferenceEditor.putInt("ServiceInterval", seekBar.getProgress());
            if(serviceFragmentSharedPreferenceEditor.commit()){ //success commit
                iInterval = seekbarServiceInterval.getProgress();
                //textViewIntervalResult.setText(String.valueOf(iInterval+iIntervalMinValue).concat(" min"));
                //update text

            }
            else{
                iInterval = serviceFragmentSharedPreference.getInt("ServiceInterval", iIntervalMinValue);  //get original value
                seekbarServiceInterval.setProgress(iInterval);  //set original value to seekbar
                //textViewIntervalResult.setText(String.valueOf(iInterval+iIntervalMinValue).concat(" min"));
            }
        }
    }


    /*
    http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
     */
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager)(getActivity().getSystemService(Context.ACTIVITY_SERVICE));  //fragment이므로 getActivity()
        for(ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            //if ("locationService".equals(serviceInfo.service.getClassName())) {
            if(locationService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void setAllOtherProfEnabled(boolean b){
        switchUseGps.setEnabled(b);
        switchUseWifi.setEnabled(b);
        switchUseData.setEnabled(b);
        seekbarServiceInterval.setEnabled(b);
    }

    private int getFlagUseConfig(){
        int iFlagUseConfig = 0x00;
        if(isUseGps){
            iFlagUseConfig |= AllConstants.USEGPS_FLAG;
        }
        if(isUseWifi){
            iFlagUseConfig |= AllConstants.USEWIFI_FLAG;
        }
        if(isUseData){
            iFlagUseConfig |= AllConstants.USEDATA_FLAG;
        }
        return iFlagUseConfig;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
