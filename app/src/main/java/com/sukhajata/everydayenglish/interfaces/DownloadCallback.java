package com.sukhajata.everydayenglish.interfaces;

/**
 * Created by Tim on 18/02/2017.
 */

public interface DownloadCallback {
    String TYPE_LESSONS = "Lessons";
    String TYPE_SLIDES = "Slides";
    String TYPE_IMAGES = "Images";
    String TYPE_SYNC_USER = "User";
    String TYPE_CLASS = "Class";
    String TYPE_GET_USER = "GetUser";
    String TYPE_PUSH_LESSON_COMPLETED = "PushLessonCompleted";
    String TYPE_PULL_LESSONS_COMPLETED = "PullLessonsCompleted";
    String TYPE_TOTALS = "Totals";
    String TYPE_UPDATE_PASSWORD = "UpdatePassword";

    String RESPONSE_OK = "Ok";
    String RESPONSE_ERROR = "Error";


    void onDownloadError(String msg);

    void onDownloadResult(String code, String type, Object result);
}
