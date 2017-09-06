package com.sample.thesis17.mytimeapp.map;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;

/**
 * Created by kimz on 2017-09-05.
 */

public class MarkerTypeDataWithBool{
    private MarkerTypeData markerTypeData;
    private boolean bSelected;

    public MarkerTypeDataWithBool(MarkerTypeData markerTypeData, boolean bSelected) {
        this.markerTypeData = markerTypeData;
        this.bSelected = bSelected;
    }

    public MarkerTypeData getMarkerTypeData() {
        return markerTypeData;
    }

    public boolean isbSelected() {
        return bSelected;
    }

    public void setMarkerTypeData(MarkerTypeData markerTypeData) {
        this.markerTypeData = markerTypeData;
    }

    public void setbSelected(boolean bSelected) {
        this.bSelected = bSelected;
    }
}