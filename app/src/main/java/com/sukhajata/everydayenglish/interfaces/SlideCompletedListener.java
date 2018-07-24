package com.sukhajata.everydayenglish.interfaces;

/**
 * Created by Tim on 4/03/2017.
 */

public interface SlideCompletedListener {
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

        void onSlideCompleted(int slideId, int errorCount);

}
