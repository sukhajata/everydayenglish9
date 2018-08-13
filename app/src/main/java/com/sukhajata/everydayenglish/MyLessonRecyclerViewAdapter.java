package com.sukhajata.everydayenglish;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sukhajata.everydayenglish.dummy.DummyContent.DummyItem;
import com.sukhajata.everydayenglish.model.Lesson;
import com.sukhajata.everydayenglish.model.LessonCompleted;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link HomeFragment.OnHomeFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyLessonRecyclerViewAdapter extends RecyclerView.Adapter<MyLessonRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private final List<Lesson> mLessons;
    private final HomeFragment.OnHomeFragmentInteractionListener mListener;

    public MyLessonRecyclerViewAdapter(Context context,
                                       ArrayList<Lesson> lessons,
                                       HomeFragment.OnHomeFragmentInteractionListener listener){

        mContext = context;
        mLessons = lessons;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mLessons.get(position);
        holder.mNameView.setText(mLessons.get(position).Name);
        holder.mDescriptionView.setText(mLessons.get(position).Description);

        LessonCompleted lessonCompleted = DbHelper
                .getInstance(mContext)
                .getLessonCompleted( holder.mItem.Id);

        if (lessonCompleted != null) {
            holder.mCorrectView.setText(String.valueOf(lessonCompleted.Correct));
            holder.mErrorsView.setText(String.valueOf(lessonCompleted.Errors));
            if (lessonCompleted.DateCompleted.indexOf(' ') > 1) {
                String date = lessonCompleted.DateCompleted.substring(0, lessonCompleted.DateCompleted.indexOf(' '));
                holder.mDateCompletedView.setText(date);
            }

            //set duller colors for completed lesson
            holder.mTitleBar.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorLightYellow, null));
            holder.mDescriptionView.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorLightYellow, null));
            holder.mDescriptionView.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorGrey, null));
            holder.mDateCompletedView.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorGrey, null));
            holder.mNameView.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorGrey, null));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHomeFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLessons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RelativeLayout mTitleBar;
        public final TextView mNameView;
        public final TextView mErrorsView;
        public final TextView mCorrectView;
        public final TextView mDateCompletedView;
        public final TextView mDescriptionView;
        public Lesson mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleBar = (RelativeLayout) view.findViewById(R.id.lesson_titleBar);
            mNameView = (TextView)view.findViewById(R.id.lesson_txtName);
            mErrorsView = (TextView)view.findViewById(R.id.lesson_txtErrors);
            mCorrectView = (TextView)view.findViewById(R.id.lesson_txtCorrect);
            mDateCompletedView = (TextView)view.findViewById(R.id.lesson_txtDateCompleted);
            mDescriptionView = (TextView)view.findViewById(R.id.lesson_txtDescription);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
