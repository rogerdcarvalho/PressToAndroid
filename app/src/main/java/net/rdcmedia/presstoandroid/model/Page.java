package net.rdcmedia.presstoandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Page extends WordPressObject implements Parcelable {

    @SerializedName("link")
    private String link;
    @SerializedName("slug")
    private String slug;

    public Page (int id, String link, String slug) {
        this.id = id;
        this.link = link;
        this.slug = slug;

    }

    public String getSlug() {
        return slug;
    }

    public String getLink() {
        return link;
    }


    protected Page(Parcel in) {
        link = in.readString();
        slug = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(slug);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}