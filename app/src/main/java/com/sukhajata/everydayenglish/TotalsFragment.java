package com.sukhajata.everydayenglish;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.sukhajata.everydayenglish.model.Word;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTotalsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TotalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TotalsFragment extends Fragment {

    public static final String ARG_NAME_CORRECT = "Correct";
    public static final String ARG_NAME_ERRORS = "Errors";
    public static final String ARG_NAME_WORD_TOTAL = "WordTotal";
    public static final String ARG_NAME_PERCENTAGE = "Percentage";

    private int mCorrect;
    private int mErrors;
    private int mWordTotal;
    private double mPercentage;
    private ArrayList<FrameLayout> mFrames;

    private OnTotalsFragmentInteractionListener mListener;

    public TotalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param wordTotal Parameter 1.
     * @param percentage Parameter 2.
     * @return A new instance of fragment TotalsFragment.
     */
    public static TotalsFragment newInstance(int correct, int errors, int wordTotal, double percentage){
        TotalsFragment fragment = new TotalsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NAME_CORRECT, correct);
        args.putInt(ARG_NAME_ERRORS, errors);
        args.putInt(ARG_NAME_WORD_TOTAL, wordTotal);
        args.putDouble(ARG_NAME_PERCENTAGE, percentage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCorrect = getArguments().getInt(ARG_NAME_CORRECT);
            mErrors = getArguments().getInt(ARG_NAME_ERRORS);
            mWordTotal = getArguments().getInt(ARG_NAME_WORD_TOTAL);
            mPercentage = getArguments().getDouble(ARG_NAME_PERCENTAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_totals, container, false);

        TextView txtCorrect = (TextView)view.findViewById(R.id.totals_txtCorrect);
        txtCorrect.setText(String.valueOf(mCorrect));

        TextView txtErrors = (TextView)view.findViewById(R.id.totals_txtErrors);
        txtErrors.setText(String.valueOf(mErrors));

        TextView txtWordTotal = (TextView)view.findViewById(R.id.totals_txtTotalWords);
        if (mWordTotal > 0) {
            txtWordTotal.setText(String.valueOf(mWordTotal));
        } else {
            TextView lblWordTotal = (TextView)view.findViewById(R.id.totals_lblWordTotal);
            lblWordTotal.setVisibility(View.GONE);
            txtWordTotal.setVisibility(View.GONE);
        }

        TextView txtPercentage = (TextView)view.findViewById(R.id.totals_txtPercentage);
        if (mPercentage > 0) {
            txtPercentage.setText(String.valueOf(mPercentage));
        } else {
            TextView lblPercentage = (TextView)view.findViewById(R.id.totals_lblPercentage);
            lblPercentage.setVisibility(View.GONE);
            txtPercentage.setVisibility(View.GONE);
        }

        //words
        FlexboxLayout flexboxLayout = (FlexboxLayout) view.findViewById(R.id.totals_flexbox);

        ArrayList<Word> words = DbHelper.getInstance(getContext()).getWords();
        mFrames = new ArrayList<>();
        for (Word word : words) {
            FrameLayout frame = new FrameLayout(getActivity());
            frame.setPadding(7,7,7,7);
            ViewGroup.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            frame.setLayoutParams(frameLayoutParams);
            mFrames.add(frame);

            Button btn = new Button(getActivity(), null, R.style.SelectableButton);
            setupButton(frame, frameLayoutParams, btn, word);

            frame.addView(btn);
            flexboxLayout.addView(frame);
        }


        Button btnNext = (Button)view.findViewById(R.id.totals_btnContinue);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onTotalsFragmentInteraction();
            }
        });

        return view;
    }

    private void setupButton(final FrameLayout frame, ViewGroup.LayoutParams layoutParams, Button button, final Word word) {
        button.setLayoutParams(layoutParams);
        button.setPadding(14,14,14,14);
        button.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
        button.setTextSize(20);
        button.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorLabelBackground, null));
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String txt = word.Word;
        if (word.Word2.length() > 0) {
            txt += " " + word.Word2;
        }

        if (word.Word3.length() > 0) {
            txt += " " + word.Word3;
        }

        if (word.Word4.length() > 0) {
            txt += " " + word.Word4;
        }

        txt += "\n" + word.Thai;
        final String all = txt;
        button.setText(txt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)getActivity().getApplication()).playAudio(all, null);
                for (FrameLayout fl : mFrames) {
                    fl.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorLabelBackground, null));
                }
                frame.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBorderSelected, null));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTotalsFragmentInteractionListener) {
            mListener = (OnTotalsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTotalsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTotalsFragmentInteractionListener {
        void onTotalsFragmentInteraction();
    }
}
