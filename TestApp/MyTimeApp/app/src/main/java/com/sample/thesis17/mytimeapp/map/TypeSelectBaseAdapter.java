package com.sample.thesis17.mytimeapp.map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;

import java.util.ArrayList;

/**
 * Created by kimz on 2017-08-31.
 */

//TypeSelectBaseAdapter에서 사용하는 data list class는 MarkerTypeDataWithBool class이므로 이를 생성하여 여기 adapter에 전달해야함.

public class TypeSelectBaseAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater = null;
    private ArrayList<MarkerTypeDataWithBool> dataList = null;
    //private Context mContext = null;
    private CustomViewHolderTypeSelect viewHolder = null;

    class CustomViewHolderTypeSelect{
        public CheckedTextView ctv;
        public MarkerTypeDataWithBool mtdb;
    }

    TypeSelectBaseAdapter(ArrayList<MarkerTypeDataWithBool> arrList){
        //mContext = context;
        dataList = arrList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public MarkerTypeDataWithBool getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolderTypeSelect holder = null;   //holder
        CheckedTextView ctv = null;     //holder element
        MarkerTypeDataWithBool mtdb = null;     //holder element
        Context mContext = parent.getContext();
        if(convertView == null){
            //convertView를 새로 생성하며 findViewById 호출을 줄이기 위해 custom View Holder class를 생성하여 view를 담고 convertView의 tag를 이용하여 holder를 저장한다.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_checkedtextview, parent, false);
            ctv = (CheckedTextView) convertView.findViewById(R.id.view_checkedtextview_single);
            holder = new CustomViewHolderTypeSelect();
            holder.ctv = ctv;
            holder.mtdb = dataList.get(position);
            convertView.setTag(holder);
        }
        else{
            //이미 convertView가 있는 경우 findViewById를 호출하지 않고 이전에 저장되어 있는 View Holder에서 view를 가져와 속도를 향상시킨다.
            holder = (CustomViewHolderTypeSelect) convertView.getTag();
            ctv = holder.ctv;
            mtdb = holder.mtdb;
        }
        MarkerTypeDataWithBool markerTypeDataWithBoolWithPosition = dataList.get(position);
        ctv.setText(markerTypeDataWithBoolWithPosition.getMarkerTypeData().getStrTypeName());
        ctv.setChecked(markerTypeDataWithBoolWithPosition.isbSelected());
        if(markerTypeDataWithBoolWithPosition.isbSelected()){
            ctv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        }
        else{
            ctv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        }

        //TODO : button listner here
        convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckedTextView tempCtv = ((CustomViewHolderTypeSelect)v.getTag()).ctv;
                        MarkerTypeDataWithBool tempMtdb = ((CustomViewHolderTypeSelect)v.getTag()).mtdb;
                        if(tempCtv.isChecked()){
                            tempCtv.setChecked(false);          //checkedtextView의 check 속성 false
                            tempCtv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);   //drawable false
                            tempMtdb.setbSelected(false);       //실제 바인딩된 데이터 false

                        }
                        else{
                            tempCtv.setChecked(true);
                            tempCtv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                            tempMtdb.setbSelected(true);
                        }
                    }
                }
        );

        return convertView;
    }
}
