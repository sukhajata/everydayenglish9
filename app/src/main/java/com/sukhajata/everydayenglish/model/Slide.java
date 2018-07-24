package com.sukhajata.everydayenglish.model;

import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Tim on 17/02/2017.
 */

public class Slide implements Parcelable{

    public int Id;
    public int LessonId;
    public int CatId;
    public String ContentEnglish;
    public String ContentThai;
    public String ContentArabic;
    public String ContentChinese;
    public int SlideOrder;
    public String ImageFileName;
    public String AudioFileName;
    public int LessonReference;
    public ArrayList<SlideMedia> MediaList;
    //public ArrayList<SlideWord> SlideWordList;

    public Slide(){

    }
    
    public Slide(int id,
                 int lessonId,
                 int catId,
                 String contentEnglish,
                 String contentThai,
                 String contentArabic,
                 String contentChinese,
                 int slideOrder,
                 String imageFileName,
                 String audioFileName,
                 int lessonReference,
                 ArrayList<SlideMedia> mediaList) {
        Id = id;
        LessonId = lessonId;
        CatId = catId;
        ContentEnglish = contentEnglish;
        ContentArabic = contentArabic;
        ContentChinese = contentChinese;
        ContentThai = contentThai;
        SlideOrder = slideOrder;
        ImageFileName = imageFileName;
        AudioFileName = audioFileName;
        LessonReference = lessonReference;
        MediaList = mediaList;
    }

    private Slide(Parcel in) {
        Id = in.readInt();
        LessonId = in.readInt();
        CatId = in.readInt();
        ContentEnglish = in.readString();
        ContentArabic = in.readString();
        ContentChinese = in.readString();
        ContentThai = in.readString();
        SlideOrder = in.readInt();
        ImageFileName = in.readString();
        AudioFileName = in.readString();
        LessonReference = in.readInt();
        MediaList = new ArrayList<>();
        in.readTypedList(MediaList, SlideMedia.CREATOR);

    }

    public int getLayoutResId() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(Id);
        out.writeInt(LessonId);
        out.writeInt(CatId);
        out.writeString(ContentEnglish);
        out.writeString(ContentArabic);
        out.writeString(ContentChinese);
        out.writeString(ContentThai);
        out.writeInt(SlideOrder);
        out.writeString(ImageFileName);
        out.writeString(AudioFileName);
        out.writeInt(LessonReference);
        out.writeTypedList(MediaList);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Slide> CREATOR
            = new Parcelable.Creator<Slide>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Slide createFromParcel(Parcel in) {
            return new Slide(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Slide[] newArray(int size) {
            return new Slide[size];
        }
    };

}
