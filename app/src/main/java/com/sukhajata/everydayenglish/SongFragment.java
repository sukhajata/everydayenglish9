package com.sukhajata.everydayenglish;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sukhajata.everydayenglish.dummy.DummyContent;
import com.sukhajata.everydayenglish.dummy.DummyContent.DummyItem;
import com.sukhajata.everydayenglish.interfaces.SlideCompletedListener;
import com.sukhajata.everydayenglish.model.Slide;

import java.util.List;

import static com.sukhajata.everydayenglish.LessonActivity.ARG_NAME_SLIDE;


public class SongFragment extends Fragment {

    private Slide mSlide;
    private SlideCompletedListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongFragment() {
    }

    public static SongFragment newInstance(Slide slide) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NAME_SLIDE, slide);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSlide = getArguments().getParcelable(ARG_NAME_SLIDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.song_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new MySongRecyclerViewAdapter(context, mSlide.MediaList, mListener));

        //next button
        Button button = (Button) view.findViewById(R.id.song_btn_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSlideCompleted(mSlide.Id, 0);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SlideCompletedListener) {
            mListener = (SlideCompletedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SlideCompletedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
