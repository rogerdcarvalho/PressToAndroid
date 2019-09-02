package net.rdcmedia.presstoandroid.core;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import net.rdcmedia.presstoandroid.Configuration;
import net.rdcmedia.presstoandroid.core.interfaces.GetDataService;

import net.rdcmedia.presstoandroid.model.Category;
import net.rdcmedia.presstoandroid.model.Media;
import net.rdcmedia.presstoandroid.model.Page;
import net.rdcmedia.presstoandroid.model.Post;
import net.rdcmedia.presstoandroid.model.WordPressObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordPressApiManager extends ViewModel {

    //Properties to manage API calls
    protected int currentCall;
    protected ArrayList<WordPressObject> partialObjectList;
    protected Hashtable<Class, String> urlStrings;
    protected Hashtable<Class, Boolean> dataCompletionStatus;
    public WebPreloader webPreloader;

    public WordPressApiManager() {

        dataCompletionStatus = new Hashtable<>();
        partialObjectList = new ArrayList<>();

        urlStrings = new Hashtable<>();
        urlStrings.put(Page.class, Configuration.WORDPRESS_LOCATION + "/wp-json/wp/v2/pages/?per_page=100");
        urlStrings.put(Category.class, Configuration.WORDPRESS_LOCATION +  "/wp-json/wp/v2/categories/?per_page=100");
        urlStrings.put(Media.class, Configuration.WORDPRESS_LOCATION + "/wp-json/wp/v2/media/?per_page=100");
        urlStrings.put(Post.class, Configuration.WORDPRESS_LOCATION + "/wp-json/wp/v2/posts/?_embed&per_page=100");

        currentCall = 1;

    }

    public void getObjects(final Class objectClass, final MutableLiveData returnArray){
        try {
            RetrofitClientInstance.reset();
            GetDataService service = RetrofitClientInstance.getRetrofitInstance(Configuration.WORDPRESS_URL).
                    create(GetDataService.class);

            if (objectClass == Category.class) {
                Call<List<Category>> call = service.getAllCategories(urlStrings.get(objectClass) +
                        "&page=" + String.valueOf(currentCall));

                call.enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                        if (response.body() == null){
                            ArrayList<Category> allCategories = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Category>(){
                                        @Override
                                        public Category apply(WordPressObject object) {
                                            return (Category)object;
                                        }
                                    }));
                            returnArray.postValue(allCategories);
                            partialObjectList = new ArrayList<WordPressObject>();
                            return;
                        }

                        partialObjectList.addAll(response.body());

                        Headers headers = response.headers();
                        int pages = Integer.parseInt(headers.get("x-wp-totalpages"));

                        if (pages > currentCall) {

                        /*We can't use lazy loading for categories, as every post relies on a
                        complete set.
                       */
                            currentCall += 1;
                            getObjects(objectClass, returnArray);
                            return;
                        } else {
                            ArrayList<Category> allCategories = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Category>(){
                                        @Override
                                        public Category apply(WordPressObject object) {
                                            return (Category)object;
                                        }
                                    }));
                            returnArray.postValue(allCategories);
                            currentCall = 1;
                            partialObjectList = new ArrayList<WordPressObject>();
                            dataCompletionStatus.put(objectClass, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        ArrayList<Category> allCategories = new ArrayList<>(Lists.transform(
                                partialObjectList,new Function<WordPressObject,Category>(){
                                    @Override
                                    public Category apply(WordPressObject object) {
                                        return (Category)object;
                                    }
                                }));
                        returnArray.postValue(allCategories);
                        partialObjectList = new ArrayList<WordPressObject>();
                    }
                });
            } else if (objectClass == Media.class){
                Call<List<Media>> call = service.getAllMedia(urlStrings.get(objectClass) +
                        "&page=" +String.valueOf(currentCall));

                call.enqueue(new Callback<List<Media>>() {
                    @Override
                    public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {

                        if (response.body() == null){
                            ArrayList<Media> allMedia = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Media>(){
                                        @Override
                                        public Media apply(WordPressObject object) {
                                            return (Media)object;
                                        }
                                    }));
                            returnArray.postValue(allMedia);
                            partialObjectList = new ArrayList<WordPressObject>();
                            return;
                        }

                        partialObjectList.addAll(response.body());

                        Headers headers = response.headers();
                        int pages = Integer.parseInt(headers.get("x-wp-totalpages"));

                        if (pages > currentCall) {

                        /*We can't use lazy loading for media, as every post relies on a
                        complete set.
                       */
                            currentCall += 1;
                            getObjects(objectClass, returnArray);
                            return;
                        } else {
                            ArrayList<Media> allMedia = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Media>(){
                                        @Override
                                        public Media apply(WordPressObject object) {
                                            return (Media)object;
                                        }
                                    }));
                            returnArray.postValue(allMedia);
                            currentCall = 1;
                            partialObjectList = new ArrayList<WordPressObject>();
                            dataCompletionStatus.put(objectClass, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Media>> call, Throwable t) {
                        ArrayList<Media> allMedia = new ArrayList<>(Lists.transform(
                                partialObjectList,new Function<WordPressObject,Media>(){
                                    @Override
                                    public Media apply(WordPressObject object) {
                                        return (Media)object;
                                    }
                                }));
                        returnArray.postValue(allMedia);
                        partialObjectList = new ArrayList<WordPressObject>();
                    }
                });
            }  else if (objectClass == Page.class) {
                Call<List<Page>> call = service.getAllPages(urlStrings.get(objectClass) +
                        "&page=" + String.valueOf(currentCall));
                call.enqueue(new Callback<List<Page>>() {
                    @Override
                    public void onResponse(Call<List<Page>> call, Response<List<Page>> response) {

                        if (response.body() == null){
                            ArrayList<Page> partialPages = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Page>(){
                                        @Override
                                        public Page apply(WordPressObject object) {
                                            return (Page)object;
                                        }
                                    }));
                            returnArray.postValue(partialPages);
                            partialObjectList = new ArrayList<WordPressObject>();
                            return;
                        }
                        partialObjectList.addAll(response.body());

                        Headers headers = response.headers();
                        int pages = Integer.parseInt(headers.get("x-wp-totalpages"));

                        if (pages > currentCall) {

                            if (Configuration.USE_LAZY_LOADING) {
                                dataCompletionStatus.put(objectClass, false);
                                ArrayList<Page> partialPages = new ArrayList<>(Lists.transform(
                                        partialObjectList,new Function<WordPressObject,Page>(){
                                            @Override
                                            public Page apply(WordPressObject object) {
                                                return (Page)object;
                                            }
                                        }));
                                returnArray.postValue(partialPages);
                                currentCall = 1;
                                partialObjectList = new ArrayList<WordPressObject>();

                            } else {
                                currentCall += 1;
                                getObjects(objectClass, returnArray);
                                return;
                            }

                        } else {
                            ArrayList<Page> partialPages = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Page>(){
                                        @Override
                                        public Page apply(WordPressObject object) {
                                            return (Page)object;
                                        }
                                    }));
                            returnArray.postValue(partialPages);
                            currentCall = 1;
                            dataCompletionStatus.put(objectClass, true);
                            partialObjectList = new ArrayList<WordPressObject>();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Page>> call, Throwable t) {
                        ArrayList<Page> partialPages = new ArrayList<>(Lists.transform(
                                partialObjectList,new Function<WordPressObject,Page>(){
                                    @Override
                                    public Page apply(WordPressObject object) {
                                        return (Page)object;
                                    }
                                }));
                        returnArray.postValue(partialPages);
                        partialObjectList = new ArrayList<WordPressObject>();
                    }
                });
            }   else if (objectClass == Post.class) {
                Call<List<Post>> call = service.getAllPosts(urlStrings.get(objectClass) +
                        "&page=" + String.valueOf(currentCall));
                call.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                        if (response.body() == null){
                            ArrayList<Post> partialPosts = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Post>(){
                                        @Override
                                        public Post apply(WordPressObject object) {
                                            return (Post)object;
                                        }
                                    }));
                            returnArray.postValue(partialPosts);
                            partialObjectList = new ArrayList<WordPressObject>();
                            return;
                        }
                        partialObjectList.addAll(response.body());

                        Headers headers = response.headers();
                        int pages = Integer.parseInt(headers.get("x-wp-totalpages"));

                        if (pages > currentCall) {
                            if (Configuration.USE_LAZY_LOADING) {
                                dataCompletionStatus.put(objectClass, false);
                                ArrayList<Post> partialPosts = new ArrayList<>(Lists.transform(
                                        partialObjectList,new Function<WordPressObject,Post>(){
                                            @Override
                                            public Post apply(WordPressObject object) {
                                                return (Post)object;
                                            }
                                        }));
                                returnArray.postValue(partialPosts);
                                currentCall = 1;
                                partialObjectList = new ArrayList<WordPressObject>();
                            }
                            else {
                                currentCall += 1;
                                getObjects(objectClass, returnArray);
                                return;
                            }

                        } else {
                            ArrayList<Post> partialPosts = new ArrayList<>(Lists.transform(
                                    partialObjectList,new Function<WordPressObject,Post>(){
                                        @Override
                                        public Post apply(WordPressObject object) {
                                            return (Post)object;
                                        }
                                    }));
                            returnArray.postValue(partialPosts);
                            currentCall = 1;
                            partialObjectList = new ArrayList<WordPressObject>();
                            dataCompletionStatus.put(objectClass, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        ArrayList<Post> partialPosts = new ArrayList<>(Lists.transform(
                                partialObjectList,new Function<WordPressObject,Post>(){
                                    @Override
                                    public Post apply(WordPressObject object) {
                                        return (Post)object;
                                    }
                                }));
                        returnArray.postValue(partialPosts);
                        partialObjectList = new ArrayList<WordPressObject>();
                    }
                });
            }

        } catch (IllegalArgumentException e){


        }
    }

    public boolean hasAdditionalObjectsOnServer(Class objectClass){
        return !dataCompletionStatus.get(objectClass);
    }

}