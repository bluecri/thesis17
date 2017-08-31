package com.sample.thesis17.mytimeapp.map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

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
    }

    class MarkerTypeDataWithBool{
        MarkerTypeData markerTypeData;
        boolean bSelected;
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
        CheckedTextView ctv = null;
        CustomViewHolderTypeSelect holder = null;
        Context mContext = parent.getContext();
        if(convertView == null){
            //convertView를 새로 생성하며 findViewById 호출을 줄이기 위해 custom View Holder class를 생성하여 view를 담고 convertView의 tag를 이용하여 holder를 저장한다.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_checkedtextview, parent, false);
            ctv = (CheckedTextView) convertView.findViewById(R.id.view_checkedtextview_single);
            holder = new CustomViewHolderTypeSelect();
            holder.ctv = ctv;
            convertView.setTag(holder);
        }
        else{
            //이미 convertView가 있는 경우 findViewById를 호출하지 않고 이전에 저장되어 있는 View Holder에서 view를 가져와 속도를 향상시킨다.
            holder = (CustomViewHolderTypeSelect) convertView.getTag();
            ctv = holder.ctv;
        }
        MarkerTypeDataWithBool markerTypeDataWithBoolWithPosition = dataList.get(position);
        ctv.setText(markerTypeDataWithBoolWithPosition.markerTypeData.getStrTypeName());
        ctv.setChecked(markerTypeDataWithBoolWithPosition.bSelected);

        //TODO : button listner here ?

        return convertView;
    }
}
