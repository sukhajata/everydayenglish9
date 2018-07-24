package com.sukhajata.everydayenglish.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Word implements Parcelable {
    public int Id;
    public String Word;
    public String Word2;
    public String Word3;
    public String Word4;
    public String Thai;
    public int Frequency;
    public int Total;

    public Word(
            int id,
            String word,
            String word2,
            String word3,
            String word4,
            String thai,
            int frequency,
            int total) {

        Id = id;
        Word = word;
        Word2 = word2;
        Word3 = word3;
        Word4 = word4;
        Thai = thai;
        Frequency = frequency;
        Total = total;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(Id);
        out.writeString(Word);
        out.writeString(Word2);
        out.writeString(Word3);
        out.writeString(Word4);
        out.writeString(Thai);
        out.writeInt(Frequency);
        out.writeInt(Total);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Word(){

    }


    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.
    private Word(Parcel in) {
        Id = in.readInt();
        Word = in.readString();
        Word2 = in.readString();
        Word3 = in.readString();
        Word4 = in.readString();
        Thai = in.readString();
        Frequency = in.readInt();
        Total = in.readInt();

    }

    public static final Parcelable.Creator<Word> CREATOR
            = new Parcelable.Creator<Word>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };


}

