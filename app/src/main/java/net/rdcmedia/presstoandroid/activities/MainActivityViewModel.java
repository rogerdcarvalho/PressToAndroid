package net.rdcmedia.presstoandroid.activities;

import android.arch.lifecycle.MutableLiveData;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.rdcmedia.presstoandroid.fragments.WebViewFragment;
import net.rdcmedia.presstoandroid.core.WordPressApiManager;
import net.rdcmedia.presstoandroid.model.Category;
import net.rdcmedia.presstoandroid.model.Media;
import net.rdcmedia.presstoandroid.model.Page;
import net.rdcmedia.presstoandroid.model.Post;
import net.rdcmedia.presstoandroid.model.WordPressObject;

import java.util.ArrayList;

public class MainActivityViewModel extends WordPressApiManager {

    //Database Objects
    private MutableLiveData<ArrayList<Category>> categories;
    private MutableLiveData<ArrayList<Media>> media;
    private MutableLiveData<ArrayList<Page>> pages;
    private MutableLiveData<ArrayList<Post>> posts;

    //Activity Properties
    public int bottomNavigationMargin;
    public int activeTab;
    public WebViewFragment activeWebFragment;
    public String[] tabNames;
    public int dataObjectListsToLoad;
    public boolean dataLoaded;
    public boolean uiLoaded;
    public boolean cacheDisabled;
    public boolean cacheCompleted;
    public boolean loading;
    public boolean loadedCachedContent;


    MutableLiveData<ArrayList<Category>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
            getObjects(Category.class, categories);
        }
        return categories;
    }

    MutableLiveData<ArrayList<Media>> getMedia() {
        if (media == null) {
            media = new MutableLiveData<>();
            getObjects(Media.class, media);
        }
        return media;
    }

    MutableLiveData<ArrayList<Page>> getPages() {
        if (pages == null) {
            pages = new MutableLiveData<>();
            getObjects(Page.class, pages);
        }
        return pages;
    }

    MutableLiveData<ArrayList<Post>> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<>();
            getObjects(Post.class, posts);
        }
        return posts;
    }

    public void getAdditionalPosts(){
        if (posts != null){
            currentCall = (int) Math.ceil((double) posts.getValue().size() / 100) + 1;
            partialObjectList = new ArrayList<WordPressObject>(posts.getValue());
            getObjects(Post.class, posts);
        }

    }

    public void addMediaToPosts(){

        //Add image urls to Posts for easier management
        if (media.getValue() != null && media.getValue().size() > 0 && posts.getValue() != null) {
            for (Post post : posts.getValue().toArray(new Post[0])) {
                if (post.getFeaturedImage() == null){
                    final Post finalPost = post;
                    ArrayList<Media> mediaList = media.getValue();
                    Iterable featuredImageIterable = Iterables.filter(media.getValue(), new Predicate<Media>() {
                        @Override
                        public boolean apply(Media input) {
                            return input.getId() == finalPost.getFeaturedImageId();
                        }
                    });

                    Media[] featuredImageArray = Iterables.toArray(featuredImageIterable, Media.class);

                    if (featuredImageArray.length > 0) {
                        post.setFeaturedImage(featuredImageArray[0].getUrl());
                    }
                }

            }
        }

    }

    public void reset(){
        pages = null;
        posts = null;
    }
}
