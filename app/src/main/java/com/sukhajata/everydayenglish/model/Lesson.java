package com.sukhajata.everydayenglish.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tim on 17/02/2017.
 */

public class Lesson implements Parcelable{

    public int Id;
    public int ModuleId;
    public String Name;
    public String Name_Arabic;
    public String Name_Chinese;
    public String Description;
    public int LessonOrder;
    public int ReviewStartOrder;
    public ArrayList<Slide> Pages;


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(Id);
        out.writeInt(ModuleId);
        out.writeString(Name);
        out.writeString(Name_Arabic);
        out.writeString(Name_Chinese);
        out.writeString(Description);
        out.writeInt(LessonOrder);
        out.writeInt(ReviewStartOrder);
        out.writeTypedList(Pages);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Lesson(){

    }

    public Lesson(int id,
                  int moduleId,
                  String name,
                  String name_Arabic,
                  String name_Chinese,
                  String description,
                  int lessonOrder,
                  int reviewStartOrder) {
        Id = id;
        ModuleId = moduleId;
        Name = name;
        Name_Arabic = name_Arabic;
        Name_Chinese = name_Chinese;
        Description = description;
        LessonOrder = lessonOrder;
        ReviewStartOrder = reviewStartOrder;
    }

    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.
    private Lesson(Parcel in) {
        Id = in.readInt();
        ModuleId = in.readInt();
        Name = in.readString();
        Name_Arabic = in.readString();
        Name_Chinese = in.readString();
        Description = in.readString();
        LessonOrder = in.readInt();
        ReviewStartOrder = in.readInt();
        Pages = new ArrayList<Slide>();
        in.readTypedList(Pages, Slide.CREATOR);
    }

    public static final Parcelable.Creator<Lesson> CREATOR
            = new Parcelable.Creator<Lesson>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };


}
