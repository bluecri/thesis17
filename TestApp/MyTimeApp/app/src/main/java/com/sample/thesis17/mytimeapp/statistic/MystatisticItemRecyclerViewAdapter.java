package com.sample.thesis17.mytimeapp.statistic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.thesis17.mytimeapp.R;
import com.sample.thesis17.mytimeapp.statistic.StatisticItemFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MystatisticItemRecyclerViewAdapter extends RecyclerView.Adapter<MystatisticItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;

    public MystatisticItemRecyclerViewAdapter(List<DummyItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_statisticitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(mValues.get(position).title);
        holder.mImageView.setImageResource(mValues.get(position).iRes);
        holder.mtotalTime.setText(mValues.get(position).totalTimeStr);
        holder.mWeekTime.setText(mValues.get(position).weekTimeStr);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mtotalTime;
        public final TextView mWeekTime;
        public final ImageView mImageView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.fragment_statistic_title);
            mImageView = (ImageView) view.findViewById(R.id.fragment_statistic_imageView);
            mtotalTime = (TextView) view.findViewById(R.id.fragment_statistic_totaltime);
            mWeekTime = (TextView) view.findViewById(R.id.fragment_statistic_weektime);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mTitle=" + mTitle +
                    ", mtotalTime=" + mtotalTime +
                    ", mWeekTime=" + mWeekTime +
                    ", mImageView=" + mImageView +
                    ", mItem=" + mItem +
                    '}';
        }
    }
}
