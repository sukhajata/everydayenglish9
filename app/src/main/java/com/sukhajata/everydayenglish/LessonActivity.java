package com.sukhajata.everydayenglish;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.sukhajata.everydayenglish.interfaces.AudioFinishedCallback;
import com.sukhajata.everydayenglish.interfaces.AudioSetupCallback;
import com.sukhajata.everydayenglish.interfaces.SlideCompletedListener;
import com.sukhajata.everydayenglish.model.Lesson;
import com.sukhajata.everydayenglish.model.LessonCompleted;
import com.sukhajata.everydayenglish.model.Slide;

public class LessonActivity extends AppCompatActivity
        implements SlideCompletedListener, AudioFinishedCallback, AudioSetupCallback {

    public static final String ARG_NAME_LESSON = "Lesson";
    public static final String ARG_NAME_IMAGE_URL = "ImageUrl";
    public static final String ARG_NAME_SLIDE = "Slide";
    public static final String ARG_NAME_INSTRUCTIONS = "Instructions";
    public static final String ARG_NAME_USER_ID = "UserID";
    public static final String ARG_NAME_MODULE_ID = "ModuleId";
    public static final String ARG_NAME_FINISH_TYPE = "FinishType";

    public static final int FINISH_TYPE_LESSON_COMPLETED = 1;
    public static final int FINISH_TYPE_SWITCH_USER = 2;
    public static final int FINISH_TYPE_REFRESH_LESSONS = 3;
    public static final int FINISH_TYPE_START_OVER = 4;
    public static final int FINISH_TYPE_CANCEL = 5;
    public static final int CHECK_DATA_CODE = 10;


    private int numPages;
    private NonSwipeableViewPager mPager;
    private LessonPagerAdapter mPagerAdapter;
    private int mUserId;
    private Lesson mLesson;
    private String mImageUrl;
    private int mCurrentSlide;
    private int mModuleId;
    private Fragment mCurrentFragment;
    private ProgressDialog progressDialog;
    private boolean mWaitingForAudio;
    private boolean mSetupComplete;
    //private ArrayList<ImageView> stars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson2);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        //bundle.setClassLoader(Lesson.class.getClassLoader());
        mLesson = bundle.getParcelable(ARG_NAME_LESSON);


        /*check if user has already started this lesson
        int slideCompleted = DbHelper
                .getInstance(this)
                .getSlideCompleted(mUserId, mLesson.Id);

        if (slideCompleted > 0) {
            mCurrentSlide = slideCompleted;
        }


        //if this lesson has already been completed, remove values from slideCompleted table
        EverydayLanguageDbHelper
                .getInstance(this)
                .checkClearSlideCompleted(mUserId, mModuleId, mLesson.Id, mLesson.LessonOrder);
        */

        if (mCurrentSlide >= mLesson.Pages.size() - 1) {
            mCurrentSlide = 0;
        }

        LinearLayout progressPanel = (LinearLayout) findViewById(R.id.lesson_progressPanel);
        //show stars
        for (int i = 0; i < mCurrentSlide; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_gold, null));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            imageView.setLayoutParams(layoutParams);

            progressPanel.addView(imageView);
        }

        for (int i = mCurrentSlide; i < mLesson.Pages.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_white, null));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            imageView.setLayoutParams(layoutParams);

            progressPanel.addView(imageView);
        }

        //home button
        ImageView home = (ImageView)findViewById(R.id.lesson_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_CANCEL);
                intent.putExtra(ARG_NAME_LESSON, mLesson);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });

        mSetupComplete = true;
        if (!mWaitingForAudio) {
            moveNext();
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

    private void moveNext() {

        Slide slide = mLesson.Pages.get(mCurrentSlide);
        DbHelper dbHelper = DbHelper.getInstance(getApplicationContext());

        String instructions = dbHelper.getSlideInstructions(slide.CatId);
        Log.d("SLIDE", "cat = " + String.valueOf(slide.CatId));
        mCurrentFragment = null;
        switch (slide.CatId) {
            case 1:
                mCurrentFragment = MultipleChoiceImageFragment.newInstance(slide, mImageUrl);
                break;
            case 2:
                mCurrentFragment = MultipleChoiceTextFragment.newInstance(slide, mImageUrl);
                break;
            case 3:
                mCurrentFragment = MissingWordFragment.newInstance(slide, mImageUrl);
                break;
            case 4:
                mCurrentFragment = TeachingFragment.newInstance(slide);
                break;
            case 6:
                mCurrentFragment = MatchingPairsTextFragment.newInstance(slide, mImageUrl);
                break;
            case 9:
                mCurrentFragment = TranslateFragment.newInstance(slide, instructions, mImageUrl);
                break;
            case 11:
                mCurrentFragment = MatchingPairsImageFragment.newInstance(slide, mImageUrl);
                break;
            case 12:
                mCurrentFragment = MatchingPairsImageTextFragment.newInstance(slide, mImageUrl);
                break;
            case 14:
                mCurrentFragment = WritingFragment.newInstance(slide);
                break;
            case 15:
                mCurrentFragment = BingoFragment.newInstance(slide, mImageUrl);
                break;
            case 16:
                mCurrentFragment = ConversationFragment.newInstance(slide, mImageUrl);
                break;
            case 17:
                mCurrentFragment = QuestionFragment.newInstance(slide, mImageUrl);
                break;
            case 18:
                mCurrentFragment = ListeningFragment.newInstance(slide, mImageUrl);
                break;
            case 20:
                mCurrentFragment = MissingWordWritingFragment.newInstance(slide, mImageUrl);
                break;
            case 21:
                mCurrentFragment =SongFragment.newInstance(slide);
                break;
        }

        mCurrentFragment.onAttach(this);


        //getSupportActionBar().setTitle(instructions);
        TextView txtInstructions = (TextView)findViewById(R.id.lesson_instructions);
        txtInstructions.setText(instructions);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_lesson2, mCurrentFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_switchUser:
                intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_SWITCH_USER);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_start_over:
                intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_START_OVER);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_refresh_lessons:
                intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_REFRESH_LESSONS);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_next_lesson:
                /*
                EverydayLanguageDbHelper
                        .getInstance(getApplicationContext())
                        .updateLessonCompleted(mUserId, mLesson.Id);
                        */
                intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_LESSON_COMPLETED);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onSlideCompleted(int slideId, int errors) {
        DbHelper.getInstance(this)
                .updateSlideCompleted(mCurrentSlide, errors);

        if (errors > 0) {
            LinearLayout progressPanel = (LinearLayout)findViewById(R.id.lesson_progressPanel);
            ImageView star = (ImageView)progressPanel.getChildAt(mCurrentSlide);
            star.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_red, null));

        } else {
            LinearLayout progressPanel = (LinearLayout) findViewById(R.id.lesson_progressPanel);
            ImageView star = (ImageView) progressPanel.getChildAt(mCurrentSlide);
            star.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_gold, null));
        }

        mCurrentSlide++;
        if (mCurrentSlide < mLesson.Pages.size()) {
            moveNext();
        } else {
            DbHelper.getInstance(getApplicationContext())
                    .updateLessonCompleted(mLesson.Id);

            Intent intent = new Intent();
            intent.putExtra(ARG_NAME_FINISH_TYPE, FINISH_TYPE_LESSON_COMPLETED);
            intent.putExtra(ARG_NAME_LESSON, mLesson);
            setResult(Activity.RESULT_OK, intent);

            finish();
        }


    }

    public void onAudioFinished(String key) {
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof AudioFinishedCallback) {
                ((AudioFinishedCallback)mCurrentFragment).onAudioFinished(key);
            }
        }
    }


    public void onAudioSetupComplete(int code) {
        mWaitingForAudio = false;
        hideProgressDialog();

        if (code == AUDIO_SETUP_MISSING_LANGUAGE) {
            Intent installTTSIntent = new Intent();
            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            ArrayList<String> languages = new ArrayList<String>();
            languages.add("en-US");
            installTTSIntent.putStringArrayListExtra(
                    TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR, languages);
            startActivityForResult(installTTSIntent, CHECK_DATA_CODE);

        } else if (mSetupComplete){
            moveNext();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
       // mWaitingForAudio = true;
        //showProgressDialog();
        //ContentManager.setupAudio(this, this, this);
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        //ContentManager.shutDownAudio();
    }


}
