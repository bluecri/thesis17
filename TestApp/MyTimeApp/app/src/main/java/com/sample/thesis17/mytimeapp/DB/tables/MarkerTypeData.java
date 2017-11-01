package com.sample.thesis17.mytimeapp.DB.tables;

import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kimz on 2017-07-26.
 */


@DatabaseTable(tableName = "markerTypeData")
public class MarkerTypeData {

    public final static String ID_FIELD_NAME = "mtd_id";

    public MarkerTypeData(String strTypeName, String strMemo, int imageIdx) {
            this.strTypeName = strTypeName;
            this.strMemo = strMemo;
            this.imageIdx = imageIdx;
    }

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private String strTypeName;

    @DatabaseField
    private String strMemo;

    @DatabaseField
    private int imageIdx;

    public MarkerTypeData(){
        //empty
    }

    public int getId() {
        return id;
    }


    public void setStrTypeName(String strTypeName){
        this.strTypeName = strTypeName;
    }
    public String getStrTypeName(){
        return strTypeName;
    }

    public void setStrMemo(String strMemo){
        this.strMemo = strMemo;
    }
    public String getStrMemo(){
        return strMemo;
    }

    public int getImageIdx() {
        return imageIdx;
    }

    public void setImageIdx(int imageIdx) {
        this.imageIdx = imageIdx;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof MarkerTypeData)){
            return false;
        }
        MarkerTypeData tempMarkerTypeData = (MarkerTypeData)obj;
        return (this.getId() == tempMarkerTypeData.getId());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MarkerTypeData ret = (MarkerTypeData)super.clone();
        return ret;
    }
}