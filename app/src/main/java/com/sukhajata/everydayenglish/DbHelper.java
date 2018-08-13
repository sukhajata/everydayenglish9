package com.sukhajata.everydayenglish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.sukhajata.everydayenglish.model.Lesson;
import com.sukhajata.everydayenglish.model.LessonCompleted;
import com.sukhajata.everydayenglish.model.Slide;
import com.sukhajata.everydayenglish.model.SlideMedia;
import com.sukhajata.everydayenglish.model.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Created by Tim on 18/02/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static DbHelper mInstance;
    public static final int DB_VERSION = 7;
    public static final String DB_NAME = "EverydayEnglish2.db";
    public static String DB_PATH;

    private SQLiteDatabase mDataBase;
    private Context mContext;
    private boolean mNeedUpdate = false;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                handleError(mIOException);
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }


    public void onCreate(SQLiteDatabase db) {

    }

    public boolean checkLessonExists(int lessonId) {
        boolean exists = false;
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            //String sql = DbContract.getSqlSelectLesson(id);
            String selection = DbContract.Table_Lesson.COLUMN_NAME_ID + " = ?";
            String[] selectionArgs = {String.valueOf(lessonId)};
            cursor = db.query(
                    DbContract.Table_Lesson.TABLE_NAME,
                    null, //select *
                    selection,
                    selectionArgs,
                    null, //group
                    null, //filter
                    null //sort
            );
            if (cursor.moveToFirst()) {
                String sqlSlides = DbContract.SQL_SELECT_LESSON_SLIDES;
                String[] argsSlides = {String.valueOf(lessonId)};
                Cursor cursorSlides = db.rawQuery(sqlSlides, argsSlides);
                if (cursor.moveToFirst()) {
                    exists = true;
                }

            }
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            cursor.close();
            db.close();
        }

        return exists;
    }

    //inserts Lesson only if it does not exist
    public void insertLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put(DbContract.Table_Lesson.COLUMN_NAME_ID, lesson.Id);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_MODULE_ID, lesson.ModuleId);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME, lesson.Name);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC, lesson.Name_Arabic);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC, lesson.Name_Chinese);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_DESCRIPTION, lesson.Description);
            values.put(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER, lesson.LessonOrder);

            db.insertWithOnConflict(
                    DbContract.Table_Lesson.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

    }

    public ArrayList<Lesson> getLessons(boolean purchased) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lesson> lessons = new ArrayList<>();
        //dump();
        int upTo = 6; //minimum number of lessons to show
        try {
            String sql = DbContract.SQL_GET_LAST_LESSON_COMPLETED;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                //show two more than the number of lessons completed
                int finished = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER));
                if (finished > 6) {
                    if (finished > 23) {
                        if (purchased) {
                            upTo = 2 + finished;
                        } else {
                            upTo = 23;
                        }
                    } else {
                        upTo = 2 + finished;
                    }
                }
            }
            cursor.close();

            sql = DbContract.SQL_GET_LESSONS_UP_TO;
            String[] args = {String.valueOf(upTo)};
            cursor = db.rawQuery(sql, args);
            while (cursor.moveToNext()) {
                int lessonId = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_ID));
                int moduleId = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_MODULE_ID));
                String name = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME));
                String nameArabic = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC));
                String nameChinese = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_CHINESE));
                String description = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_DESCRIPTION));
                int order = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER));
                int reviewStartOrder = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER));
                Lesson lesson = new Lesson(
                        lessonId,
                        moduleId,
                        name,
                        nameArabic,
                        nameChinese,
                        description,
                        order,
                        reviewStartOrder);

                lessons.add(lesson);
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return lessons;
    }

    public void dump() {
        try{
            SQLiteDatabase db = this.getReadableDatabase();

            String sql = "SELECT * FROM Lesson";
            Cursor cursor = db.rawQuery(sql, null);
            Log.d("DUMP", "Lessons: " + DatabaseUtils.dumpCursorToString(cursor));
            cursor.close();

            sql = "SELECT * FROM Slide";
            cursor = db.rawQuery(sql, null);
            Log.d("DUMP", "Slides: " + DatabaseUtils.dumpCursorToString(cursor));
            cursor.close();


        } catch (SQLiteException ex) {
            handleError(ex);
        }

    }

    public int getLastLessonId() {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String sql = DbContract.SQL_SELECT_LAST_LESSON_ID;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_ID));
            }

            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return id;
    }

    public LessonCompleted getLessonCompleted(int lessonId) {
        LessonCompleted lessonCompleted = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String sql = DbContract.SQL_EXISTS_LESSON_COMPLETED;
            String[] args = {String.valueOf(lessonId)};
            Cursor cursor = db.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                int errors = cursor.getInt(cursor.getColumnIndex(DbContract.Table_LessonCompleted.COLUMN_NAME_ERRORS));
                int correct = cursor.getInt(cursor.getColumnIndex(DbContract.Table_LessonCompleted.COLUMN_NAME_CORRECT));
                String dateCompleted = cursor.getString(cursor.getColumnIndex(DbContract.Table_LessonCompleted.COLUMN_NAME_DATE));

                lessonCompleted = new LessonCompleted(lessonId, correct, errors, dateCompleted);
            }
            cursor.close();
        }catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return lessonCompleted;
    }

    public void insertLessons(List<Lesson> lessons) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            for (Lesson lesson : lessons) {
                ContentValues values = new ContentValues();
                Log.d("SUKH", "inserting lesson " + String.valueOf(lesson.Id));
                values.put(DbContract.Table_Lesson.COLUMN_NAME_ID, lesson.Id);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_MODULE_ID, lesson.ModuleId);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME, lesson.Name);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC, lesson.Name_Arabic);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_NAME_CHINESE, lesson.Name_Chinese);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_DESCRIPTION, lesson.Description);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER, lesson.LessonOrder);
                values.put(DbContract.Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER, lesson.ReviewStartOrder);

                db.insertWithOnConflict(
                        DbContract.Table_Lesson.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);

                db.setTransactionSuccessful();
            }
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public void insertSlides(List<Slide> slides) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            for (Slide slide : slides) {
                ContentValues values = new ContentValues();
                values.put(DbContract.Table_Slide.COLUMN_NAME_ID, slide.Id);
                values.put(DbContract.Table_Slide.COLUMN_NAME_LESSON_ID, slide.LessonId);
                values.put(DbContract.Table_Slide.COLUMN_NAME_CAT_ID, slide.CatId);
                values.put(DbContract.Table_Slide.COLUMN_NAME_CONTENT_ENGLISH, slide.ContentEnglish);
                values.put(DbContract.Table_Slide.COLUMN_NAME_CONTENT_THAI, slide.ContentThai);
                values.put(DbContract.Table_Slide.COLUMN_NAME_CONTENT_ARABIC, slide.ContentArabic);
                values.put(DbContract.Table_Slide.COLUMN_NAME_CONTENT_CHINESE, slide.ContentChinese);
                values.put(DbContract.Table_Slide.COLUMN_NAME_SLIDE_ORDER, slide.SlideOrder);
                values.put(DbContract.Table_Slide.COLUMN_NAME_LESSON_REFERENCE, slide.LessonReference);
                values.put(DbContract.Table_Slide.COLUMN_NAME_IMAGE_FILE_NAME, slide.ImageFileName);

                db.insertWithOnConflict(
                        DbContract.Table_Slide.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );

            }
            db.setTransactionSuccessful();

        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    private boolean slideExists(int slideId) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String sql = DbContract.SQL_SELECT_SLIDE;
            String[] args = {String.valueOf(slideId)};
            Cursor cursor = db.rawQuery(sql, args);

            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return result;
    }


    public String getSlideInstructions(int catId) {
        String instructions = "";
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String[] projection = {
                    DbContract.Table_Categories.COLUMN_NAME_INSTRUCTIONS
            };
            String selection = DbContract.Table_Categories.COLUMN_NAME_ID + " = ?";
            String[] selectionArgs = {String.valueOf(catId)};


            Cursor cursor = db.query(
                    DbContract.Table_Categories.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,//don't group
                    null, //don't filter
                    null //don't sort
            );

            if (cursor.moveToFirst()) {
                instructions = cursor.getString(cursor.getColumnIndex(DbContract.Table_Categories.COLUMN_NAME_INSTRUCTIONS));
            }

            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return instructions;
    }

    public ArrayList<Lesson> getLessonsByModule(int moduleId) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String sql = DbContract.SQL_GET_LESSONS_BY_MODULE;
            String[] args = {String.valueOf(moduleId)};
            Cursor cursor = db.rawQuery(sql, args);
            while (cursor.moveToNext()) {
                int lessonId = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_ID));
                String name = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME));
                String nameArabic = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC));
                String nameChinese = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_CHINESE));
                String description = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_DESCRIPTION));
                int order = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER));
                int reviewStartOrder = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER));
                Lesson lesson = new Lesson(lessonId, moduleId, name, nameArabic, nameChinese, description, order, reviewStartOrder);

                lessons.add(lesson);
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return lessons;
    }

    public boolean checkForLesson(int lessonOrder, int moduleId) {
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            String sql = DbContract.SQL_EXISTS_LESSON;
            String[] args = {String.valueOf(lessonOrder), String.valueOf(moduleId)};
            Cursor cursor = db.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                exists = true;
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return exists;
    }

    public Lesson getLesson(int lessonId) {
        Lesson lesson = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {

            String sql = DbContract.SQL_SELECT_LESSON;
            Log.d("SUKH", sql);
            String[] args = {String.valueOf(lessonId)};
            Cursor cursor = db.rawQuery(sql, args);

            if (cursor.moveToFirst()) {
                int moduleId = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_MODULE_ID));
                String name = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME));
                String nameArabic = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_ARABIC));
                String nameChinese = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_NAME_CHINESE));
                String description = cursor.getString(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_DESCRIPTION));
                int order = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_LESSON_ORDER));
                int reviewStartOrder = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER));
                lesson = new Lesson(lessonId, moduleId, name, nameArabic, nameChinese, description, order, reviewStartOrder);

                ArrayList<Slide> slides = new ArrayList<Slide>();
                String sqlSlides = DbContract.SQL_SELECT_LESSON_SLIDES;
                String[] argsSlides = {String.valueOf(lessonId)};
                Cursor cursorSlides = db.rawQuery(sqlSlides, argsSlides);

                while (cursorSlides.moveToNext()) {
                    int sid = cursorSlides.getInt(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_ID));
                    int catId = cursorSlides.getInt(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_CAT_ID));
                    int slideOrder = cursorSlides.getInt(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_SLIDE_ORDER));
                    String contentEnglish = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_CONTENT_ENGLISH));
                    String contentThai = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_CONTENT_THAI));
                    String contentArabic = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_CONTENT_ARABIC));
                    String contentChinese = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_CONTENT_CHINESE));
                    String slideImage = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_IMAGE_FILE_NAME));
                    String slideAudio = cursorSlides.getString(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_AUDIO_FILE_NAME));
                    int lessonReference = cursorSlides.getInt(cursorSlides.getColumnIndex(DbContract.Table_Slide.COLUMN_NAME_LESSON_REFERENCE));

                    ArrayList<SlideMedia> slideMediaList = new ArrayList<SlideMedia>();

                    //media
                    sql = DbContract.SQL_SELECT_SLIDE_MEDIA;
                    String[] argsMedia = {String.valueOf(sid)};
                    Cursor mediaCursor = db.rawQuery(sql, argsMedia);
                   // Log.d("DUMP", "Media: " + DatabaseUtils.dumpCursorToString(mediaCursor));
                    mediaCursor.moveToFirst();
                    for (int i = 0; i < mediaCursor.getCount(); i++) {
                        int slideMediaId = mediaCursor.getInt(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_ID));
                        int isTarget = mediaCursor.getInt(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_IS_TARGET));
                        String english = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_ENGLISH));
                        String thai = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_THAI));
                        String arabic = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_ARABIC));
                        String chinese = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_CHINESE));
                        String imageFileName = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_IMAGE_FILE_NAME));
                        String audioFileName = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_AUDIO_FILE_NAME));
                        String notes = mediaCursor.getString(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_NOTES));
                        int mediaOrder = mediaCursor.getInt(mediaCursor.getColumnIndex(DbContract.Table_SlideMedia.COLUMN_NAME_MEDIA_ORDER));

                        SlideMedia slideMedia = new SlideMedia(
                                slideMediaId,
                                sid,
                                (isTarget == 1 ? true : false),
                                english,
                                thai,
                                arabic,
                                chinese,
                                imageFileName,
                                audioFileName,
                                notes,
                                mediaOrder);
                        slideMediaList.add(slideMedia);

                        mediaCursor.moveToNext();
                    }
                    mediaCursor.close();


                    //slide
                    Slide slide = new Slide(
                            sid,
                            lessonId,
                            catId,
                            contentEnglish,
                            contentThai,
                            contentArabic,
                            contentChinese,
                            slideOrder,
                            slideImage,
                            slideAudio,
                            lessonReference,
                            slideMediaList);
                    Log.d("SUKH", "adding slide cat " + String.valueOf(catId));
                    slides.add(slide);

                }
                lesson.Pages = slides;
                //return lesson;
            } else {
                /*
                sql = "SELECT * FROM LessonQueue";
                Cursor dump = db.rawQuery(sql, null);
                Log.d("SUKH", DatabaseUtils.dumpCursorToString(dump));
                Log.d("SUKH", "no lessons found for user " + String.valueOf(userId));
                */

            }

            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }


        return lesson;
    }


    public void insertAccuracy(int mediaId, int slideId, boolean correct) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String sql = DbContract.SQL_INSERT_TABLE_ACCURACY;
            ContentValues values = new ContentValues();
            values.put(DbContract.Table_Accuracy.COLUMN_NAME_MEDIA_ID, mediaId);
            values.put(DbContract.Table_Accuracy.COLUMN_NAME_SLIDE_ID, slideId);
            values.put(DbContract.Table_Accuracy.COLUMN_NAME_CORRECT, (correct ? 1 : 0));

            db.insert(
                    DbContract.Table_Accuracy.TABLE_NAME,
                    null,
                    values);

        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {

            db.close();
        }
    }

    public void insertLessonsCompleted(ArrayList<LessonCompleted> lessonCompletedArrayList) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        try {
            dbWrite.beginTransaction();
            SQLiteStatement insertStatement = dbWrite.compileStatement(DbContract.SQL_INSERT_LESSON_COMPLETED);

            for (LessonCompleted lessonCompleted : lessonCompletedArrayList) {
                String sql = DbContract.SQL_EXISTS_LESSON_COMPLETED;
                String[] args = {String.valueOf(lessonCompleted.LessonId)};
                Cursor cursor = dbRead.rawQuery(sql, args);
                if (!cursor.moveToFirst()) {
                    insertStatement.clearBindings();
                    insertStatement.bindLong(1,lessonCompleted.LessonId);
                    insertStatement.bindLong(2,lessonCompleted.Correct);
                    insertStatement.bindLong(3,lessonCompleted.Errors);
                    insertStatement.bindString(4, lessonCompleted.DateCompleted);

                    insertStatement.executeInsert();
                }
                cursor.close();
            }

            dbWrite.setTransactionSuccessful();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            dbWrite.endTransaction();
            dbWrite.close();
            dbRead.close();
        }
    }


    public void updateLessonCompleted(int lessonId) {

        SQLiteDatabase dbRead = this.getReadableDatabase();
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        try {
            //update lesson completed table
            //remove old value
            String sql = DbContract.SQL_DELETE_LESSON_COMPLETED;
            String[] args2 = {String.valueOf(lessonId)};
            dbWrite.execSQL(sql, args2);

            //make new value
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateStr = sdf.format(new Date());

            String sqlErrorCount = DbContract.SQL_GET_SLIDE_ERROR_COUNT;
            String[] argErrorCount = {String.valueOf(lessonId)};
            Cursor cursor1 = dbRead.rawQuery(sqlErrorCount, argErrorCount);
            int errors = 0;
            if (cursor1.moveToFirst()) {
                errors = cursor1.getInt(cursor1.getColumnIndex(DbContract.Table_SlideCompleted.COLUMN_NAME_ERRORS));
            }

            String sqlSlideCount = DbContract.SQL_SLIDE_COUNT;
            String[] argSlideCount = {String.valueOf(lessonId)};
            Cursor cursor2 = dbRead.rawQuery(sqlSlideCount, argSlideCount);
            int total = 0;
            if (cursor2.moveToFirst()) {
                total = cursor2.getInt(cursor2.getColumnIndex("Total"));
            }
            int correct = total - errors;

            ContentValues values = new ContentValues();
            values.put(DbContract.Table_LessonCompleted.COLUMN_NAME_LESSON_ID, lessonId);
            values.put(DbContract.Table_LessonCompleted.COLUMN_NAME_CORRECT, correct);
            values.put(DbContract.Table_LessonCompleted.COLUMN_NAME_ERRORS, errors);
            values.put(DbContract.Table_LessonCompleted.COLUMN_NAME_DATE, dateStr);

            dbWrite.insert(
                    DbContract.Table_LessonCompleted.TABLE_NAME,
                    null,
                    values
            );
            cursor1.close();
            cursor2.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            dbRead.close();
            dbWrite.close();
        }

    }

    /*
    public int getSlideCompleted(int userId, int lessonId) {
        int slideOrder = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        try {

            String sql = DbContract.SQL_CHECK_SLIDE_COMPLETED_ORDER;
            String[] args = {String.valueOf(userId), String.valueOf(lessonId)};
            Cursor cursor = db.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                slideOrder = cursor.getInt(cursor.getColumnIndex(DbContract.Table_SlideCompleted.COLUMN_NAME_SLIDE_ORDER));
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }

        return slideOrder;
    }


    public void checkClearSlideCompleted(int userId, int moduleId, int lessonId, int lessonOrder) {
        SQLiteDatabase dbRead = this.getReadableDatabase();

        try {
            String sql = DbContract.SQL_EXISTS_LESSON_COMPLETED;
            String[] args = {String.valueOf(userId), String.valueOf(moduleId), String.valueOf(lessonOrder)};
            Cursor cursor = dbRead.rawQuery(sql, args);
            if (cursor.moveToFirst()) {
                SQLiteDatabase db = this.getWritableDatabase();
                sql = DbContract.SQL_CLEAR_SLIDE_COMPLETED;
                String[] args2 = {String.valueOf(userId), String.valueOf(lessonId)};
                db.execSQL(sql, args2);
            }
            cursor.close();
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            dbRead.close();
        }
    }
*/
    public void updateSlideCompleted(int slideId, int mistakes) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        try {

            //delete any existing entry
            String sql = DbContract.SQL_DELETE_SLIDE_COMPLETED;
            String[] args = {String.valueOf(slideId)};
            dbWrite.execSQL(sql, args);

            ContentValues values = new ContentValues();
            values.put(DbContract.Table_SlideCompleted.COLUMN_NAME_SLIDE_ID, slideId);
            values.put(DbContract.Table_SlideCompleted.COLUMN_NAME_ERRORS, mistakes);
            dbWrite.insert(
                    DbContract.Table_SlideCompleted.TABLE_NAME,
                    null,
                    values
            );

        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            dbWrite.close();
        }

    }

    public ArrayList<Word> getWords() {
        ArrayList<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String sql = DbContract.SQL_SELECT_WORDS;
            Log.d("SQL", sql);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_ID));
                    String word = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD));
                    String word2 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD2));
                    String word3 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD3));
                    String word4 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD4));
                    String thai = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_THAI));
                    int frequency = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_FREQUENCY));
                    int total = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Lesson_Word_Group.COLUMN_NAME_TOTAL));
                    Word newWord = new Word(id, word, word2, word3, word4, thai, frequency, total);
                    words.add(newWord);
                }
            }

        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }
        return words;
    }

    public ArrayList<Word> getTop500Words() {
        ArrayList<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String sql = DbContract.SQL_SELECT_TOP_WORDS;
            Log.d("SQL", sql);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_ID));
                    String word = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD));
                    String word2 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD2));
                    String word3 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD3));
                    String word4 = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_WORD4));
                    String thai = cursor.getString(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_THAI));
                    int frequency = cursor.getInt(cursor.getColumnIndex(DbContract.Table_Top_1000.COLUMN_NAME_FREQUENCY));

                    Word newWord = new Word(id, word, word2, word3, word4, thai, frequency, 0);
                    words.add(newWord);
                }
            }

        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }
        return words;
    }

/*
    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DbContract.Table_User.COLUMN_NAME_ID, user.Id);
            values.put(DbContract.Table_User.COLUMN_NAME_STUDENT_POSITION, user.StudentPosition);
            values.put(DbContract.Table_User.COLUMN_NAME_STUDENT_ID, user.StudentId);
            values.put(DbContract.Table_User.COLUMN_NAME_GENDER, user.Gender);
            values.put(DbContract.Table_User.COLUMN_NAME_FIRST_NAME, user.FirstName);
            values.put(DbContract.Table_User.COLUMN_NAME_LAST_NAME, user.LastName);
            values.put(DbContract.Table_User.COLUMN_NAME_EMAIL, user.Email);
            values.put(DbContract.Table_User.COLUMN_NAME_PASSWORD, user.Password);
            values.put(DbContract.Table_User.COLUMN_NAME_CLASS_ID, user.ClassId);
            values.put(DbContract.Table_User.COLUMN_NAME_LESSON_COMPLETED_ORDER, user.LessonCompletedOrder);
            values.put(DbContract.Table_User.COLUMN_NAME_MODULE_ID, user.ModuleId);
            values.put(DbContract.Table_User.COLUMN_NAME_ACTIVE, user.Active);

            db.insertWithOnConflict(
                    DbContract.Table_User.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }
    }

    public void insertUserList(ArrayList<User> users, SQLiteDatabase db) {
        try {
            for (User user : users) {
                ContentValues values = new ContentValues();
                values.put(DbContract.Table_User.COLUMN_NAME_ID, user.Id);
                values.put(DbContract.Table_User.COLUMN_NAME_STUDENT_POSITION, user.StudentPosition);
                values.put(DbContract.Table_User.COLUMN_NAME_STUDENT_ID, user.StudentId);
                values.put(DbContract.Table_User.COLUMN_NAME_GENDER, user.Gender);
                values.put(DbContract.Table_User.COLUMN_NAME_FIRST_NAME, user.FirstName);
                values.put(DbContract.Table_User.COLUMN_NAME_LAST_NAME, user.LastName);
                values.put(DbContract.Table_User.COLUMN_NAME_EMAIL, user.Email);
                values.put(DbContract.Table_User.COLUMN_NAME_PASSWORD, user.Password);
                values.put(DbContract.Table_User.COLUMN_NAME_CLASS_ID, user.ClassId);
                values.put(DbContract.Table_User.COLUMN_NAME_LESSON_COMPLETED_ORDER, user.LessonCompletedOrder);
                values.put(DbContract.Table_User.COLUMN_NAME_MODULE_ID, user.ModuleId);
                values.put(DbContract.Table_User.COLUMN_NAME_ACTIVE, user.Active);
                values.put(DbContract.Table_User.COLUMN_NAME_ACCURACY, user.Accuracy);

                db.insertWithOnConflict(
                        DbContract.Table_User.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE
                );
            }
        } catch (SQLiteException ex) {
            handleError(ex);
        }
    }

    public void checkInsertUser(int id, String email, String password, int lessonCompletedOrder) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put(DbContract.Table_User.COLUMN_NAME_ID, id);
            values.put(DbContract.Table_User.COLUMN_NAME_EMAIL, email);
            values.put(DbContract.Table_User.COLUMN_NAME_PASSWORD, password);
            values.put(DbContract.Table_User.COLUMN_NAME_LESSON_COMPLETED_ORDER, lessonCompletedOrder);

            db.insertWithOnConflict(
                    DbContract.Table_User.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
        } catch (SQLiteException ex) {
            handleError(ex);
        } finally {
            db.close();
        }
    }

    public static final int USER_NOT_EXISTS = 0;
    public static final int USER_WRONG_PASSWORD = -1;
    public static final int USER_EXISTS_CORRECT_PASSWORD = 1;
    public static final int ERROR = -2;
*/
    private void handleError(Exception ex) {
        Toast.makeText(
                mContext,
                "Exception: " + ex.getMessage() + ", at: " + Log.getStackTraceString(ex),
                Toast.LENGTH_LONG)
                .show();
        Log.e("Exception", ex.getMessage() + " at: " + Log.getStackTraceString(ex));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (newVersion > oldVersion)
                mNeedUpdate = true;
        }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
