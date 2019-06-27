package net.rdcmedia.presstoandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Media extends WordPressObject implements Parcelable {

    @SerializedName("source_url")
    private String url;

    public Media (int id, String name, String url) {
        this.id = id;
        this.url = url;

    }

    public String getUrl() {
        return url;
    }


    protected Media(Parcel in) {
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}