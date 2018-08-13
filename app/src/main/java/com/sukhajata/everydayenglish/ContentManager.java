package com.sukhajata.everydayenglish;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

//import com.google.android.gms.common.api.Response;
import com.sukhajata.everydayenglish.interfaces.DownloadCallback;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sukhajata.everydayenglish.model.LessonCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ContentManager {

    private static final String LessonsCompletedUrl = "https://sukhajata.com/api/lessons-completed.php?";
    private static final String UserUrl = "https://sukhajata.com/api/user.php?";

    private static RequestQueue requestQueue;

    public static void fetchImage(Context context, ImageView imgTarget,String imageFileName, String imageUrl) {

        //int id = context.getResources().getIdentifier(imageFileName, "drawable", context.getPackageName());

        imageFileName = imageFileName.substring(0, imageFileName.indexOf('.'));
        Log.d("IMG", imageFileName);
        try {
            Class res = R.drawable.class;
            Field field = res.getField(imageFileName);
            int drawableId = field.getInt(null);
            imgTarget.setImageDrawable(context.getResources().getDrawable(drawableId));
        }
        catch (Exception e) {
            Log.e("IMG", "Failure to get drawable id.", e);
        }
    }

    public static void downloadImage(final Context context, final ImageView imageView, final String imageFileName, final String imageUrl) {

    }


    public static void pushLessonCompleted(Context context, int userId, int lessonId,
                                           int correct, int errors, String dateCompleted, final DownloadCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        String url = LessonsCompletedUrl +
                "userId=" + String.valueOf(userId) +
                "&lessonId=" + String.valueOf(lessonId) +
                "&correct=" + String.valueOf(correct)  +
                "&errors=" + String.valueOf(errors) +
                "&dateCompleted=" + dateCompleted;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] args = {response};
                        callback.onDownloadResult(
                                DownloadCallback.RESPONSE_OK,
                                DownloadCallback.TYPE_PUSH_LESSON_COMPLETED,
                                args);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onDownloadError(error.getLocalizedMessage());
                    }
                }

        );

        requestQueue.add(stringRequest);
    }

    public static void pullLessonsCompleted(final Context context, final int userId, int moduleId,
                                            int lastLessonOrder, final DownloadCallback callback) {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        String url = LessonsCompletedUrl + "userId=" + String.valueOf(userId);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<LessonCompleted> lessonCompletedArrayList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int lessonId = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_LESSON_ID);
                                int correct = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_CORRECT);
                                int errors = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_ERRORS);
                                String dateCompleted = jsonObject.getString(DbContract.Table_LessonCompleted.COLUMN_NAME_DATE);

                                LessonCompleted lessonCompleted = new LessonCompleted(lessonId, correct, errors,
                                        dateCompleted);
                                lessonCompletedArrayList.add(lessonCompleted);
                            }

                            DbHelper.getInstance(context)
                                    .insertLessonsCompleted(lessonCompletedArrayList);

                            callback.onDownloadResult(DownloadCallback.RESPONSE_OK, DownloadCallback.TYPE_PULL_LESSONS_COMPLETED, null);
                        } catch (JSONException ex) {
                            callback.onDownloadError(ex.getLocalizedMessage());
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onDownloadError(error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

    }

    public static void syncUser(
            final Context context,
            final String uid,
            final String email,
            final String name,
            final DownloadCallback callback) {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        String url = UserUrl +
                "uid=" + uid +
                "&email=" + email +
                "&name=" + name;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<LessonCompleted> lessonCompletedArrayList = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int lessonId = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_LESSON_ID);
                                int correct = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_CORRECT);
                                int errors = jsonObject.getInt(DbContract.Table_LessonCompleted.COLUMN_NAME_ERRORS);
                                String dateCompleted = jsonObject.getString(DbContract.Table_LessonCompleted.COLUMN_NAME_DATE);

                                LessonCompleted lessonCompleted = new LessonCompleted(lessonId, correct, errors,
                                        dateCompleted);
                                lessonCompletedArrayList.add(lessonCompleted);
                            }

                            DbHelper.getInstance(context)
                                    .insertLessonsCompleted(lessonCompletedArrayList);

                            callback.onDownloadResult(DownloadCallback.RESPONSE_OK, DownloadCallback.TYPE_SYNC_USER, null);
                        } catch (JSONException ex) {
                            callback.onDownloadError(ex.getLocalizedMessage());
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onDownloadError(error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

}
