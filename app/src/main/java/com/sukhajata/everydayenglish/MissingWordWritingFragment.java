package com.sukhajata.everydayenglish;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sukhajata.everydayenglish.interfaces.SlideCompletedListener;
import com.sukhajata.everydayenglish.model.Slide;
import com.sukhajata.everydayenglish.model.SlideMedia;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SlideCompletedListener} interface
 * to handle interaction events.
 * Use the {@link MissingWordWritingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissingWordWritingFragment extends Fragment {

    private Slide mSlide;
    private String mImageUrl;
    private SlideMedia mTarget;
    private FrameLayout mSelectedTextFrame;
    private View mLayout;
    private int errorCount;
    private SlideCompletedListener mListener;

    public MissingWordWritingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param slide Parameter 1.
     * @param imageUrl Parameter 2.
     * @return A new instance of fragment MissingWordWritingFragment.
     */
    public static MissingWordWritingFragment newInstance(Slide slide, String imageUrl) {
        MissingWordWritingFragment fragment = new MissingWordWritingFragment();
        Bundle args = new Bundle();
        args.putParcelable(LessonActivity.ARG_NAME_SLIDE, slide);
        args.putString(LessonActivity.ARG_NAME_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSlide = getArguments().getParcelable(LessonActivity.ARG_NAME_SLIDE);
            mImageUrl = getArguments().getString(LessonActivity.ARG_NAME_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_missing_word_writing, container, false);

        ((TextView)view.findViewById(R.id.missingWordWriting_txtThai)).setText(mSlide.ContentThai);

        final TextView txtEnglish = ((TextView)view.findViewById(R.id.missingWordWriting_txtTarget));
        txtEnglish.setText(mSlide.ContentEnglish);

        final Button btnNext = (Button)view.findViewById(R.id.missingWordWriting_btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSlideCompleted(mSlide.Id, errorCount);
            }
        });

        final String answer = mSlide.MediaList.get(0).English;
        final String sentence = mSlide.ContentEnglish.substring(0, mSlide.ContentEnglish.indexOf('_')) +
                answer + mSlide.ContentEnglish.substring(mSlide.ContentEnglish.lastIndexOf('_') + 1);

        ((MyApplication)getActivity().getApplication()).playAudio(sentence, mSlide.AudioFileName);

        Button btnPlay = (Button)view.findViewById(R.id.missingWordWriting_speaker);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyApplication)getActivity().getApplication()).playAudio(sentence, mSlide.AudioFileName);
            }
        });

        final Button btnSkip = (Button)view.findViewById(R.id.missingWordWriting_btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEnglish.setText(sentence);
                btnNext.setEnabled(true);
                btnSkip.setVisibility(View.GONE);
                errorCount++;

            }
        });


        EditText edit = ((EditText)view.findViewById(R.id.missingWordWriting_txtWriting));
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().toLowerCase().trim().equals(answer.toLowerCase())) {
                    //correct answer
                    InputMethodManager inputManager = (InputMethodManager)getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    txtEnglish.setText(sentence);

                    btnNext.setEnabled(true);
                    btnSkip.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(edit.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


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
