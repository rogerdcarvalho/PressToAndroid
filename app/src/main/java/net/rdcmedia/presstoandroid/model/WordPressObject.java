package net.rdcmedia.presstoandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class WordPressObject implements Parcelable {

    @SerializedName("id")
    protected int id;

    public int getId() {
        return id;
    }

    protected  WordPressObject(){

    }

    protected WordPressObject(Parcel in) {
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WordPressObject> CREATOR = new Parcelable.Creator<WordPressObject>() {
        @Override
        public WordPressObject createFromParcel(Parcel in) {
            return new WordPressObject(in);
        }

        @Override
        public WordPressObject[] newArray(int size) {
            return new WordPressObject[size];
        }
    };
}