package net.rdcmedia.presstoandroid.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.HashMap;

public class Post extends WordPressObject implements Parcelable {

    @SerializedName("title")
    private HashMap<String, String> title;
    @SerializedName("excerpt")
    private HashMap<String, String> excerpt;
    @SerializedName("content")
    private HashMap<String, String> content;
    @SerializedName("_embedded")
    private Object embeddedData;
    @SerializedName("featured_media")
    private int featuredImageId;
    @SerializedName("link")
    private String link;
    @SerializedName("categories")
    private int[] categoryIds;
    @SerializedName("date_gmt")
    private String date;
    private String featuredImage;

    public Post (int id, HashMap<String, String> title, HashMap<String, String> excerpt, HashMap<String, String> content,
                 int featuredImageId, String link, int[] categoryIds, String date, Object embeddedData) {

        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.featuredImageId = featuredImageId;
        this.link = link;
        this.categoryIds = categoryIds;
        this.date = date;
        this.embeddedData = embeddedData;
    }

    public String getTitle() {
        return title.get("rendered");
    }

    public String getSummaryText(){
        return excerpt.get("rendered");
    }

    public String getFeaturedImage() {
        try {
            LinkedTreeMap<String, Object> json = (LinkedTreeMap<String, Object>)embeddedData;
            ArrayList <LinkedTreeMap<String, String>> featuredMediaList = (
                    ArrayList<LinkedTreeMap<String, String>>)json.get("wp:featuredmedia");
            LinkedTreeMap<String, String>  featuredMedia = featuredMediaList.get(0);
            return featuredMedia.get("source_url");
        }
        catch (Exception e) {
            return null;
        }
    }

    public int getFeaturedImageId() {
        return featuredImageId;
    }

    public String getLink() {
        return link;
    }

    public int[] getCategoryIds() {
        return categoryIds;
    }

    public String getDate() {
        return date;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    protected Post(Parcel in) {
        title = (HashMap) in.readValue(HashMap.class.getClassLoader());
        excerpt = (HashMap) in.readValue(HashMap.class.getClassLoader());
        content = (HashMap) in.readValue(HashMap.class.getClassLoader());
        featuredImageId = in.readInt();
        link = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(excerpt);
        dest.writeValue(content);
        dest.writeInt(featuredImageId);
        dest.writeString(link);
        dest.writeString(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}