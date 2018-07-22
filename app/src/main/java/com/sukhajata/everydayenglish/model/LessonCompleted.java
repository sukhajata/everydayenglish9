package com.sukhajata.everydayenglish.model;

import android.content.Context;

/**
 * Created by Administrator on 22/7/2560.
 */

public class LessonCompleted {
    public int LessonId;
    public int Correct;
    public int Errors;
    public String DateCompleted;

    public LessonCompleted(int lessonId, int correct, int errors, String dateCompleted) {
        LessonId = lessonId;
        Correct = correct;
        Errors = errors;
        DateCompleted = dateCompleted;
    }
}
