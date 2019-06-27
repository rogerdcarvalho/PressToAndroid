package net.rdcmedia.presstoandroid.core.interfaces;

import net.rdcmedia.presstoandroid.model.Category;
import net.rdcmedia.presstoandroid.model.Media;
import net.rdcmedia.presstoandroid.model.Page;
import net.rdcmedia.presstoandroid.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetDataService {

    @GET
    Call<List<Category>> getAllCategories(@Url String url);
    @GET
    Call<List<Media>> getAllMedia(@Url String url);
    @GET
    Call<List<Page>> getAllPages(@Url String url);
    @GET
    Call<List<Post>> getAllPosts(@Url String url);

}