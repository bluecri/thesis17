package com.sample.thesis17.mytimeapp.map;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.DB.tables.MarkerTypeData;
import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.map.dummy.DummyContent.DummyItem;

import java.util.List;

public class MyMarkerTypeDataRecyclerViewAdapter extends RecyclerView.Adapter<MyMarkerTypeDataRecyclerViewAdapter.ViewHolder> {

    public interface OnListFragmentInteractionClickListener {
        void onListFragmentInteraction(MarkerTypeData item, int pos);
    }

    private final List<MarkerTypeData> mValues;
    private final OnListFragmentInteractionClickListener mListener;

    public MyMarkerTypeDataRecyclerViewAdapter(List<MarkerTypeData> items, OnListFragmentInteractionClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_markertypedata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getStrTypeName());
        holder.mContentView.setText(mValues.get(position).getStrMemo());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public MarkerTypeData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
