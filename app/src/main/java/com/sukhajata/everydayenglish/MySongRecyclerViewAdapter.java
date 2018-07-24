package com.sukhajata.everydayenglish;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sukhajata.everydayenglish.interfaces.SlideCompletedListener;
import com.sukhajata.everydayenglish.model.SlideMedia;

import java.util.List;


public class MySongRecyclerViewAdapter extends RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private final List<SlideMedia> mMedias;
    private final SlideCompletedListener mListener;
    private Context mContext;

    public MySongRecyclerViewAdapter(Context context, List<SlideMedia> items, SlideCompletedListener listener) {
        mContext = context;
        mMedias = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MySongRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mMedias.get(position);
        holder.mEnglishView.setText(mMedias.get(position).English);
        holder.mThaiView.setText(mMedias.get(position).Thai);
        if (mMedias.get(position).Notes.length() > 0) {
            holder.mNotesView.setText(mMedias.get(position).Notes);
        } else {
            holder.mNotesView.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onSlideCompleted(holder.mItem.SlideId);
                    ((MyApplication)mContext.getApplicationContext()).playAudio(holder.mItem.English, holder.mItem.AudioFileName);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMedias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mEnglishView;
        public final TextView mThaiView;
        public final TextView mNotesView;
        public SlideMedia mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEnglishView = (TextView) view.findViewById(R.id.song_item_english);
            mThaiView = (TextView) view.findViewById(R.id.song_item_thai);
            mNotesView = (TextView) view.findViewById(R.id.song_item_notes);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mEnglishView.getText() + "'";
        }
    }
}
