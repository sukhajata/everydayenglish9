package com.sukhajata.everydayenglish;

import android.provider.BaseColumns;

public class DbContract {

    //Lessons
    public static final class Table_Lesson implements BaseColumns {
        public static final String TABLE_NAME = "Lesson";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_MODULE_ID = "ModuleId";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_NAME_ARABIC = "Name_Arabic";
        public static final String COLUMN_NAME_NAME_CHINESE = "Name_Chinese";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_LESSON_ORDER = "LessonOrder";
        public static final String COLUMN_NAME_REVIEW_START_ORDER = "ReviewStartOrder";

    }

    public static final String SQL_CREATE_TABLE_LESSON =
            "CREATE TABLE " + Table_Lesson.TABLE_NAME + " (" +
                    Table_Lesson.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                    Table_Lesson.COLUMN_NAME_MODULE_ID + " INTEGER, " +
                    Table_Lesson.COLUMN_NAME_NAME + " TEXT, " +
                    Table_Lesson.COLUMN_NAME_NAME_ARABIC + " TEXT, " +
                    Table_Lesson.COLUMN_NAME_NAME_CHINESE + " TEXT, " +
                    Table_Lesson.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    Table_Lesson.COLUMN_NAME_LESSON_ORDER + " INTEGER, " +
                    Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER + " INTEGER )";


    public static String getSqlSelectLesson(int id) {
        String sql = "SELECT * FROM " + Table_Lesson.TABLE_NAME + " " +
                "WHERE " + Table_Lesson.COLUMN_NAME_ID + " = " + String.valueOf(id);

        return sql;
    }

    public static String SQL_INSERT_LESSON =
            "INSERT INTO " + Table_Lesson.TABLE_NAME + " (" +
                    Table_Lesson.COLUMN_NAME_ID + ", " +
                    Table_Lesson.COLUMN_NAME_MODULE_ID + ", " +
                    Table_Lesson.COLUMN_NAME_NAME + ", " +
                    Table_Lesson.COLUMN_NAME_NAME_ARABIC + ", " +
                    Table_Lesson.COLUMN_NAME_NAME_CHINESE + ", " +
                    Table_Lesson.COLUMN_NAME_DESCRIPTION + ", " +
                    Table_Lesson.COLUMN_NAME_LESSON_ORDER + ", " +
                    Table_Lesson.COLUMN_NAME_REVIEW_START_ORDER + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    public static String SQL_SELECT_LESSON_ORDER =
            "SELECT " + Table_Lesson.COLUMN_NAME_LESSON_ORDER + " " +
                    "FROM " + Table_Lesson.TABLE_NAME + " " +
                    "WHERE " + Table_Lesson.COLUMN_NAME_ID + " = ?";

    public static final String SQL_SELECT_LAST_LESSON_ID =
            "SELECT " + Table_Lesson.COLUMN_NAME_ID + " " +
                    "FROM " + Table_Lesson.TABLE_NAME + " " +
                    "ORDER BY " + Table_Lesson.COLUMN_NAME_ID + " DESC " +
                    "LIMIT 1";

    public static String SQL_SELECT_LESSON =
            "SELECT l.* FROM " + Table_Lesson.TABLE_NAME + " l " +
                    "WHERE " + Table_Lesson.COLUMN_NAME_ID + " = ? ";

    public static String SQL_GET_LESSONS_BY_MODULE =
            "SELECT * FROM " + Table_Lesson.TABLE_NAME + " " +
                    "WHERE " + Table_Lesson.COLUMN_NAME_MODULE_ID + " = ? " +
                    "ORDER BY " + Table_Lesson.COLUMN_NAME_LESSON_ORDER;

    public static String SQL_GET_LESSONS_UP_TO =
            "SELECT * FROM " + Table_Lesson.TABLE_NAME + " " +
                    "WHERE " + Table_Lesson.COLUMN_NAME_LESSON_ORDER + " <= ? " +
                    "ORDER BY " + Table_Lesson.COLUMN_NAME_LESSON_ORDER;


    //Slides
    public static final class Table_Slide implements BaseColumns {
        public static final String TABLE_NAME = "Slide";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_LESSON_ID = "LessonId";
        public static final String COLUMN_NAME_CAT_ID = "CategoryId";
        public static final String COLUMN_NAME_CONTENT_ENGLISH = "Content_English";
        public static final String COLUMN_NAME_CONTENT_THAI = "Content_Thai";
        public static final String COLUMN_NAME_CONTENT_ARABIC = "Content_Arabic";
        public static final String COLUMN_NAME_CONTENT_CHINESE = "Content_Chinese";
        public static final String COLUMN_NAME_SLIDE_ORDER = "SlideOrder";
        public static final String COLUMN_NAME_IMAGE_FILE_NAME = "ImageFileName";
        public static final String COLUMN_NAME_AUDIO_FILE_NAME = "AudioFileName";
        public static final String COLUMN_NAME_UPDATED = "Updated";
        public static final String COLUMN_NAME_LESSON_REFERENCE = "LessonReference";
    }

    public static final String SQL_CREATE_TABLE_SLIDE =
            "CREATE TABLE " + Table_Slide.TABLE_NAME + "(" +
                    Table_Slide.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                    Table_Slide.COLUMN_NAME_LESSON_ID + " INTEGER, " +
                    Table_Slide.COLUMN_NAME_CAT_ID + " INTEGER, " +
                    Table_Slide.COLUMN_NAME_CONTENT_ENGLISH + " TEXT, " +
                    Table_Slide.COLUMN_NAME_CONTENT_CHINESE + " TEXT, " +
                    Table_Slide.COLUMN_NAME_CONTENT_THAI + " TEXT, " +
                    Table_Slide.COLUMN_NAME_SLIDE_ORDER + " INTEGER, " +
                    Table_Slide.COLUMN_NAME_IMAGE_FILE_NAME + " TEXT, " +
                    Table_Slide.COLUMN_NAME_AUDIO_FILE_NAME + " TEXT, " +
                    Table_Slide.COLUMN_NAME_UPDATED + " INTEGER, " +
                    Table_Slide.COLUMN_NAME_LESSON_REFERENCE + " INTEGER)";


    public static String SQL_SELECT_SLIDE =
            "SELECT * FROM " + Table_Slide.TABLE_NAME + " " +
                    "WHERE " + Table_Slide.COLUMN_NAME_ID + " = ?";

    public static final String SQL_SELECT_LESSON_SLIDES =
            "SELECT * FROM " + Table_Slide.TABLE_NAME + " " +
                    "WHERE " + Table_Slide.COLUMN_NAME_LESSON_ID + " = ? " +
                    "ORDER BY " + Table_Slide.COLUMN_NAME_SLIDE_ORDER;

    public static final String SQL_SLIDE_COUNT =
            "SELECT COUNT(*) AS Total FROM " + Table_Slide.TABLE_NAME + " " +
                    "WHERE " + Table_Slide.COLUMN_NAME_LESSON_ID + " = ?";

    public static String SQL_INSERT_SLIDE =
            "INSERT INTO " + Table_Slide.TABLE_NAME + " (" +
                    Table_Slide.COLUMN_NAME_ID + ", " +
                    Table_Slide.COLUMN_NAME_LESSON_ID + ", " +
                    Table_Slide.COLUMN_NAME_CAT_ID + ", " +
                    Table_Slide.COLUMN_NAME_CONTENT_ENGLISH + ", " +
                    Table_Slide.COLUMN_NAME_CONTENT_THAI + ", " +
                    Table_Slide.COLUMN_NAME_CONTENT_ARABIC + ", " +
                    Table_Slide.COLUMN_NAME_CONTENT_CHINESE + ", " +
                    Table_Slide.COLUMN_NAME_SLIDE_ORDER + ", " +
                    Table_Slide.COLUMN_NAME_IMAGE_FILE_NAME + ", " +
                    Table_Slide.COLUMN_NAME_AUDIO_FILE_NAME + ", " +
                    Table_Slide.COLUMN_NAME_UPDATED + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    //SlideMedia
    public static final class Table_SlideMedia implements BaseColumns {
        public static final String TABLE_NAME = "SlideMedia";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_SLIDE_ID = "SlideId";
        public static final String COLUMN_NAME_IS_TARGET = "IsTarget";
        public static final String COLUMN_NAME_ENGLISH = "English";
        public static final String COLUMN_NAME_THAI = "Thai";
        public static final String COLUMN_NAME_ARABIC = "Arabic";
        public static final String COLUMN_NAME_CHINESE = "Chinese";
        public static final String COLUMN_NAME_IMAGE_FILE_NAME = "ImageFileName";
        public static final String COLUMN_NAME_AUDIO_FILE_NAME = "AudioFileName";
        public static final String COLUMN_NAME_NOTES = "Notes";
        public static final String COLUMN_NAME_MEDIA_ORDER = "MediaOrder";

    }

    public static String SQL_SELECT_SLIDE_MEDIA =
        "SELECT * FROM " + Table_SlideMedia.TABLE_NAME + " WHERE " + Table_SlideMedia.COLUMN_NAME_SLIDE_ID + " = ?";

    public static String SQL_INSERT_SLIDE_MEDIA =
            "INSERT INTO " + Table_SlideMedia.TABLE_NAME + "(" +
                    Table_SlideMedia.COLUMN_NAME_ID + ", " +
                    Table_SlideMedia.COLUMN_NAME_SLIDE_ID + ", " +
                    Table_SlideMedia.COLUMN_NAME_IS_TARGET + ", " +
                    Table_SlideMedia.COLUMN_NAME_ENGLISH + ", " +
                    Table_SlideMedia.COLUMN_NAME_THAI + ", " +
                    Table_SlideMedia.COLUMN_NAME_ARABIC + ", " +
                    Table_SlideMedia.COLUMN_NAME_CHINESE + ", " +
                    Table_SlideMedia.COLUMN_NAME_IMAGE_FILE_NAME + ", " +
                    Table_SlideMedia.COLUMN_NAME_AUDIO_FILE_NAME + ", " +
                    Table_SlideMedia.COLUMN_NAME_NOTES + ", " +
                    Table_SlideMedia.COLUMN_NAME_NOTES + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    //Categories
    public static final class Table_Categories implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_INSTRUCTIONS = "Instructions";
        public static final String COLUMN_NAME_ARABIC = "Arabic";
        public static final String COLUMN_NAME_CHINESE = "Chinese";
    }

    public static final String SQL_CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + Table_Categories.TABLE_NAME + " (" +
                    Table_Categories.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                    Table_Categories.COLUMN_NAME_NAME + " TEXT, " +
                    Table_Categories.COLUMN_NAME_INSTRUCTIONS + " TEXT, " +
                    Table_Categories.COLUMN_NAME_ARABIC + " TEXT, " +
                    Table_Categories.COLUMN_NAME_CHINESE + " TEXT) ";


    public static String getSqlInsertCategory(int id, String name, String instructions) {
        String sql = "INSERT INTO " + Table_Categories.TABLE_NAME + "( " +
                Table_Categories.COLUMN_NAME_ID + ", " +
                Table_Categories.COLUMN_NAME_NAME + ", " +
                Table_Categories.COLUMN_NAME_INSTRUCTIONS + ") " +
                "VALUES (" +
                String.valueOf(id) + ", " +
                name + ", " +
                instructions + ")";

        return sql;
    }


    public static final class Table_Accuracy implements BaseColumns {
        public static final String TABLE_NAME = "Accuracy";
        public static final String COLUMN_NAME_USER_ID = "UserId";
        public static final String COLUMN_NAME_MEDIA_ID = "MediaId";
        public static final String COLUMN_NAME_SLIDE_ID = "SlideId";
        public static final String COLUMN_NAME_CORRECT = "Correct";

    }

    public static final String SQL_CREATE_TABLE_ACCURACY =
            "CREATE TABLE " + Table_Accuracy.TABLE_NAME + " (" +
                    Table_Accuracy._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Table_Accuracy.COLUMN_NAME_USER_ID + " INTEGER, " +
                    Table_Accuracy.COLUMN_NAME_MEDIA_ID + " INTEGER, " +
                    Table_Accuracy.COLUMN_NAME_SLIDE_ID + " INTEGER, " +
                    Table_Accuracy.COLUMN_NAME_CORRECT + " INTEGER)";


    public static String SQL_INSERT_TABLE_ACCURACY =
            "INSERT INTO " + Table_Accuracy.TABLE_NAME + " (" +
                    Table_Accuracy.COLUMN_NAME_MEDIA_ID + ", " +
                    Table_Accuracy.COLUMN_NAME_SLIDE_ID + ", " +
                    Table_Accuracy.COLUMN_NAME_CORRECT + ") " +
                    "VALUES (?, ?, ?)";


    //Table LessonCompleted
    public static final class Table_LessonCompleted  implements BaseColumns{
        public static final String TABLE_NAME = "LessonCompleted";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_LESSON_ID = "LessonId";
        public static final String COLUMN_NAME_CORRECT = "Correct";
        public static final String COLUMN_NAME_ERRORS = "Errors";
        public static final String COLUMN_NAME_DATE = "DateCompleted";
    }


    public static final String SQL_DELETE_LESSON_COMPLETED =
            "DELETE FROM LessonCompleted WHERE " + Table_LessonCompleted.COLUMN_NAME_LESSON_ID + " = ?";

    public static final String SQL_EXISTS_LESSON_COMPLETED =
            "SELECT * FROM " + Table_LessonCompleted.TABLE_NAME + " " +
                    "WHERE " + Table_LessonCompleted.COLUMN_NAME_LESSON_ID + " = ? ";


    public static final String SQL_INSERT_LESSON_COMPLETED =
            "INSERT INTO " + Table_LessonCompleted.TABLE_NAME + " (" +
                    Table_LessonCompleted.COLUMN_NAME_LESSON_ID + ", " +
                    Table_LessonCompleted.COLUMN_NAME_CORRECT + ", " +
                    Table_LessonCompleted.COLUMN_NAME_ERRORS + ", " +
                    Table_LessonCompleted.COLUMN_NAME_DATE + ") " +
                    "VALUES (?, ?, ?, ?)";

    public static final String SQL_GET_LAST_LESSON_COMPLETED =
            "SELECT " + Table_Lesson.COLUMN_NAME_LESSON_ORDER + " " +
                    "FROM " + Table_Lesson.TABLE_NAME + " l " +
                    "INNER JOIN " + Table_LessonCompleted.TABLE_NAME + " c " +
                    "ON l." + Table_Lesson.COLUMN_NAME_ID + " = " + Table_LessonCompleted.COLUMN_NAME_LESSON_ID + " " +
                    "ORDER BY " + Table_Lesson.COLUMN_NAME_LESSON_ORDER + " DESC " +
                    "LIMIT 1";

    public static final String SQL_EXISTS_LESSON =
            "SELECT 1 FROM " + Table_Lesson.TABLE_NAME + " " +
                    "WHERE " + Table_Lesson.COLUMN_NAME_LESSON_ORDER + " = ? " +
                    "AND " + Table_Lesson.COLUMN_NAME_MODULE_ID + " = ? ";


    //Slide completed
    static final class Table_SlideCompleted implements BaseColumns {
        static final String TABLE_NAME = "SlideCompleted";
        static final String COLUMN_NAME_ID = "_id";
        static final String COLUMN_NAME_SLIDE_ID = "SlideId";
        static final String COLUMN_NAME_ERRORS = "Errors";
    }

    static final String SQL_CHECK_SLIDE_COMPLETED =
            "SELECT * FROM " + Table_SlideCompleted.TABLE_NAME + " " +
                    "WHERE "  + Table_SlideCompleted.COLUMN_NAME_SLIDE_ID + " = ?";

    static final String SQL_DELETE_SLIDE_COMPLETED =
            "DELETE FROM " + Table_SlideCompleted.TABLE_NAME + " " +
                    "WHERE " + Table_SlideCompleted.COLUMN_NAME_SLIDE_ID + " = ?";

    static final String SQL_GET_SLIDE_ERROR_COUNT =
            "SELECT " + Table_SlideCompleted.COLUMN_NAME_ERRORS + " " +
                    "FROM " + Table_SlideCompleted.TABLE_NAME + " sc " +
                    "INNER JOIN " + Table_Slide.TABLE_NAME + " s " +
                    "ON sc." + Table_SlideCompleted.COLUMN_NAME_SLIDE_ID + " = s." + Table_Slide.COLUMN_NAME_ID + " " +
                    "WHERE s." + Table_Slide.COLUMN_NAME_LESSON_ID + " = ?";


    //top 1000 words
    static final class Table_Top_1000 implements BaseColumns {
        static final String TABLE_NAME = "Top1000";
        static final String COLUMN_NAME_ID = "_id";
        static final String COLUMN_NAME_WORD = "Word";
        static final String COLUMN_NAME_WORD2 = "Word2";
        static final String COLUMN_NAME_WORD3 = "Word3";
        static final String COLUMN_NAME_WORD4 = "Word4";
        static final String COLUMN_NAME_THAI = "Thai";
        static final String COLUMN_NAME_ARABIC = "Arabic";
        static final String COLUMN_NAME_CHINESE = "Chinese";
        static final String COLUMN_NAME_FREQUENCY = "Frequency";
        static final String COLUMN_NAME_PERCENTAGE = "Percentage";
    }

    static final String CREATE_TABLE_TOP_1000 =
            "CREATE TABLE " + Table_Top_1000.TABLE_NAME + " (" +
                    Table_Top_1000.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Table_Top_1000.COLUMN_NAME_WORD + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_WORD + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_WORD2 + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_WORD3 + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_WORD4 + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_THAI + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_ARABIC + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_CHINESE + " TEXT, " +
                    Table_Top_1000.COLUMN_NAME_FREQUENCY + " INTEGER, " +
                    Table_Top_1000.COLUMN_NAME_PERCENTAGE + " REAL) ";

    public static final String SQL_INSERT_TOP_1000 =
            "INSERT INTO " + Table_Top_1000.TABLE_NAME + " (" +
                    Table_Top_1000.COLUMN_NAME_ID + ", " +
                    Table_Top_1000.COLUMN_NAME_WORD + ", " +
                    Table_Top_1000.COLUMN_NAME_WORD2 + ", " +
                    Table_Top_1000.COLUMN_NAME_WORD3 + ", " +
                    Table_Top_1000.COLUMN_NAME_WORD4 + ", " +
                    Table_Top_1000.COLUMN_NAME_THAI + ", " +
                    Table_Top_1000.COLUMN_NAME_ARABIC + ", " +
                    Table_Top_1000.COLUMN_NAME_CHINESE + ", " +
                    Table_Top_1000.COLUMN_NAME_FREQUENCY + ", " +
                    Table_Top_1000.COLUMN_NAME_PERCENTAGE + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    //Lesson Word Group
    //Total number of each word grouped by lesson
    static final class Table_Lesson_Word_Group implements BaseColumns {
        static final String TABLE_NAME = "LessonWordGroup";
        static final String COLUMN_NAME_ID = "_id";
        static final String COLUMN_NAME_LESSON_ID = "LessonId";
        static final String COLUMN_NAME_WORD_ID = "WordId";
        static final String COLUMN_NAME_TOTAL = "Total";
    }

    static final String CREATE_TABLE_LESSON_WORD_GROUP =
            "CREATE TABLE " + Table_Lesson_Word_Group.TABLE_NAME + " (" +
                    Table_Lesson_Word_Group.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Table_Lesson_Word_Group.COLUMN_NAME_LESSON_ID + " INTEGER, " +
                    Table_Lesson_Word_Group.COLUMN_NAME_WORD_ID + " INTEGER, " +
                    Table_Lesson_Word_Group.COLUMN_NAME_TOTAL + " INTEGER)";

    public static final String SQL_INSERT_LESSON_WORD_GROUP =
            "INSERT INTO " + Table_Lesson_Word_Group.TABLE_NAME + " (" +
                    Table_Lesson_Word_Group.COLUMN_NAME_ID + ", " +
                    Table_Lesson_Word_Group.COLUMN_NAME_LESSON_ID + ", " +
                    Table_Lesson_Word_Group.COLUMN_NAME_WORD_ID + ", " +
                    Table_Lesson_Word_Group.COLUMN_NAME_TOTAL + ")" +
                    "VALUES (?, ?, ?, ?)";


    public static final String SQL_SELECT_WORDS =
            "SELECT t." + Table_Top_1000.COLUMN_NAME_ID + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_THAI + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_WORD + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_WORD2 + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_WORD3 + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_WORD4 + ", " +
                    "t." + Table_Top_1000.COLUMN_NAME_FREQUENCY + ", " +
                    "SUM(lw." + Table_Lesson_Word_Group.COLUMN_NAME_TOTAL + ") AS Total " +
                    "FROM " + Table_Lesson_Word_Group.TABLE_NAME + " lw " +
                    "INNER JOIN " + Table_Top_1000.TABLE_NAME + " t " +
                    "ON lw." + Table_Lesson_Word_Group.COLUMN_NAME_WORD_ID + " = t." + Table_Top_1000.COLUMN_NAME_ID + " " +
                    "WHERE lw." + Table_Lesson_Word_Group.COLUMN_NAME_LESSON_ID + " IN " +
                    "(SELECT " + Table_LessonCompleted.COLUMN_NAME_LESSON_ID + " " +
                    "FROM " + Table_LessonCompleted.TABLE_NAME + " " +
                    "WHERE " + Table_LessonCompleted.COLUMN_NAME_CORRECT + " >= " + Table_LessonCompleted.COLUMN_NAME_ERRORS + ") " +
                    "GROUP BY lw." + Table_Lesson_Word_Group.COLUMN_NAME_WORD_ID + " " +
                    "HAVING SUM(lw." + Table_Lesson_Word_Group.COLUMN_NAME_TOTAL + ") >= 10 " +
                    "ORDER BY SUM(lw." + Table_Lesson_Word_Group.COLUMN_NAME_TOTAL + ")";

    public static String SQL_SELECT_TOP_WORDS =
            "SELECT * FROM " + Table_Top_1000.TABLE_NAME +
                    "ORDER BY " + Table_Top_1000.COLUMN_NAME_FREQUENCY + " " +
                    "LIMIT 500";


}
