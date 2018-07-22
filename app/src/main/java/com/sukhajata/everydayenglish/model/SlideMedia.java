package com.sukhajata.everydayenglish.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by Tim on 19/02/2017.
 */

public class SlideMedia implements Parcelable, Comparable<SlideMedia>{
    public int Id;
    public int SlideId;
    public boolean IsTarget;
    public String English;
    public String Thai;
    public String Arabic;
    public String Chinese;
    public String ImageFileName;
    public String AudioFileName;
    public String Notes;
    public int MediaOrder;

    public SlideMedia(){}

    public SlideMedia(int id,
                      int slideId,
                      boolean isTarget,
                      String english,
                      String thai,
                      String arabic,
                      String chinese,
                      String imageFileName,
                      String audioFileName,
                      String notes,
                      int mediaOrder) {

        Id = id;
        SlideId = slideId;
        IsTarget = isTarget;
        English = english;
        Thai = thai;
        Arabic = arabic;
        Chinese = chinese;
        ImageFileName = imageFileName;
        AudioFileName = audioFileName;
        Notes = notes;
        MediaOrder = mediaOrder;
    }

    public int compareTo(SlideMedia other) {
        if (this.MediaOrder < other.MediaOrder) {
            return -1;
        }

        return 1;
    }

    private SlideMedia(Parcel in) {
        Id = in.readInt();
        SlideId = in.readInt();
        IsTarget = in.readByte() != 0;
        English = in.readString();
        Thai = in.readString();
        Arabic = in.readString();
        Chinese = in.readString();
        ImageFileName = in.readString();
        AudioFileName = in.readString();
        Notes = in.readString();
        MediaOrder = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(Id);
        out.writeInt(SlideId);
        out.writeByte((byte)(IsTarget ? 1 : 0));
        out.writeString(English);
        out.writeString(Thai);
        out.writeString(Arabic);
        out.writeString(Chinese);
        out.writeString(ImageFileName);
        out.writeString(AudioFileName);
        out.writeString(Notes);
        out.writeInt(MediaOrder);
    }


    public static final Parcelable.Creator<SlideMedia> CREATOR
            = new Parcelable.Creator<SlideMedia>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public SlideMedia createFromParcel(Parcel in) {
            return new SlideMedia(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public SlideMedia[] newArray(int size) {
            return new SlideMedia[size];
        }
    };

}
