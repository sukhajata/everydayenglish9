package com.sukhajata.everydayenglish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sukhajata.everydayenglish.interfaces.AudioSetupCallback;
import com.sukhajata.everydayenglish.model.Lesson;
import com.sukhajata.everydayenglish.model.LessonCompleted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnListFragmentInteractionListener,
        AudioSetupCallback{

    public static final int USER_REQUEST_CODE = 1;
    public static final int LESSON_REQUEST_CODE = 2;
    public static final int TOTALS_REQUEST_CODE = 3;

    private boolean mAudioSetupComplete;
    private boolean waiting;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //text to speech
        mAudioSetupComplete = false;
        ((MyApplication)getApplication()).setupAudio(this);

        //check user
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String userId = sharedPref.getString(getString(R.string.stored_user_id), "");

        if (userId.equals("")) {
            mAuth = FirebaseAuth.getInstance();
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                    new AuthUI.IdpConfig.TwitterBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }


        //home screen
        try {
            HomeFragment lessonFragment = HomeFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, lessonFragment)
                    .commitAllowingStateLoss();
        } catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onAudioSetupComplete(int code) {
        if (code == AUDIO_SETUP_MISSING_LANGUAGE) {
            Intent installTTSIntent = new Intent();
            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            ArrayList<String> languages = new ArrayList<String>();
            languages.add("en-US");
            installTTSIntent.putStringArrayListExtra(
                    TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR, languages);
            startActivityForResult(installTTSIntent, LessonActivity.CHECK_DATA_CODE);

        } else if (code == AUDIO_SETUP_FAILURE) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.connect_to_network)
                    .setTitle("Text to speech setup failed.")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (code == AUDIO_SETUP_SUCCESS) {
            mAudioSetupComplete = true;
            if (!waiting) {
                hideProgressDialog();
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchLesson(Lesson lesson) {
        Log.d("SUKH", "launching lesson " + lesson.Id);

        Intent intent = new Intent(this, LessonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(LessonActivity.ARG_NAME_LESSON, lesson);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, LESSON_REQUEST_CODE);
    }

    public void onHomeFragmentInteraction(Lesson lesson) {
        Lesson complete = DbHelper
                .getInstance(this)
                .getLesson(lesson.Id);

        launchLesson(complete);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String name = user.getDisplayName();
                String email = user.getEmail();
                String uid = user.getUid();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.stored_user_id), uid);
                editor.apply();


            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        } else if (requestCode == USER_REQUEST_CODE) {


        } else if (requestCode == TOTALS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //startLessonSync(true);
                //ContentManager.checkNextLessonAvailable(this, this, mCurrentUser.Id, 0, false);

                /*
                mLesson = EverydayLanguageDbHelper
                        .getInstance(getApplicationContext())
                        .getNextLesson(mCurrentUser.Id);
                if (mLesson != null) {
                    launchLesson(mLesson);
                } else {
                    mWaitingForLessons = true;
                    mWaitingForSlides = true;
                    int lastOrder = EverydayLanguageDbHelper
                            .getInstance(this)
                            .getLastLessonOrder(mCurrentUser.Id);

                    ContentManager.syncData(this, this, imageUrl,
                            mCurrentUser.Id, lastOrder);
                }
                */
            }
        } else if (requestCode == LESSON_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int finishType = data.getIntExtra(LessonActivity.ARG_NAME_FINISH_TYPE,
                        LessonActivity.FINISH_TYPE_LESSON_COMPLETED);

                switch (finishType) {
                    case LessonActivity.FINISH_TYPE_LESSON_COMPLETED:
                        Lesson lesson = data.getParcelableExtra(LessonActivity.ARG_NAME_LESSON);

                        LessonCompleted lessonCompleted = DbHelper
                                .getInstance(this)
                                .getLessonCompleted(lesson.Id);

                        TotalsFragment totalsFragment = TotalsFragment.newInstance(
                                lessonCompleted.Correct,
                                lessonCompleted.Errors,
                                0,
                                0);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content, totalsFragment)
                                .commitAllowingStateLoss();

                        break;

                }

            }
        }

    }
}
