package com.sukhajata.everydayenglish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.sukhajata.everydayenglish.model.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHomeFragmentInteractionListener}
 * interface.
 */
public class HomeFragment extends Fragment implements
        SkuDetailsResponseListener,
        PurchasesUpdatedListener,
        BillingClientStateListener {

    private OnHomeFragmentInteractionListener mListener;
    private static final int RC_SIGN_IN = 123;
    private BillingClient mBillingClient;
    private String skuSpecial;
    private ProgressDialog progressDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {

    }


    @SuppressWarnings("unused")
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        Context context = view.getContext();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String purchasedString = sharedPref.getString(getString(R.string.purchased), "0");
        boolean purchased = purchasedString.equals("1") ? true : false;

        ArrayList<Lesson> lessons = DbHelper
                .getInstance(getActivity())
                .getLessons(purchased);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.home_list);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyLessonRecyclerViewAdapter(getActivity(), lessons, mListener));

        //in app purchase
        if (!purchased) {
            mBillingClient = BillingClient
                    .newBuilder(getActivity())
                    .setListener(this)
                    .build();
            mBillingClient.startConnection(this);
        }

        LinearLayout lytPurchase = (LinearLayout) view.findViewById(R.id.home_purchase);
        lytPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("PURCHASE", "clicked");
                purchase();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //billing
    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
            //check if purchased already
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> purchaseList = purchasesResult.getPurchasesList();
            if (purchaseList.size() > 0) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.purchased), "1");
                editor.apply();

                HomeFragment lessonFragment = HomeFragment.newInstance();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, lessonFragment)
                        .commitAllowingStateLoss();
            } else {
                List skuList = new ArrayList<>();
                skuList.add("ลดราคา");
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList)
                        .setType(BillingClient.SkuType.INAPP);
                mBillingClient.querySkuDetailsAsync(params.build(), this);
            }
        }
    }
    @Override
    public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    private void purchase(){
        showProgressDialog();
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(skuSpecial)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        int responseCode = mBillingClient.launchBillingFlow(getActivity(), flowParams);
    }

    @Override
    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
            for (SkuDetails skuDetails : skuDetailsList) {
                if (skuDetails.getTitle().equals("ลดราคา")) {
                    skuSpecial = skuDetails.getSku();
                    String price = skuDetails.getPrice();
                }
            }
        }
    }


    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List<Purchase> purchases) {
        hideProgressDialog();
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.purchased), "1");
            editor.apply();

            HomeFragment lessonFragment = HomeFragment.newInstance();
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, lessonFragment)
                    .commitAllowingStateLoss();

        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            Log.d("PURCHASE", "show");
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHomeFragmentInteractionListener {

        void onHomeFragmentInteraction(Lesson item);
    }
}
