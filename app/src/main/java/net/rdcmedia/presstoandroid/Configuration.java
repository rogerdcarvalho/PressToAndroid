package net.rdcmedia.presstoandroid;

import com.google.common.collect.ImmutableMap;

public class Configuration {

    //The URL for the WordPress Blog
    public static String WORDPRESS_URL = "https://presstoapp.com";

    //If your WordPress instance is not running in the root of the above domain, please add the subfolder below
    //i.e. https://mywordpressdomain.com/location would enter "/location" below
    public static String WORDPRESS_LOCATION = "";

    //The icons to use for the Tab Bar buttons
    public static String[] TAB_ICONS = {"ic_action_news", "ic_action_list_2", "ic_action_help", "ic_action_dialog"};

    //Whether the first tab should be the list of blog posts
    public static final boolean SHOW_BLOG = true;

    //The name of the tab showing blog posts
    public static String POSTS_TAB = "Showcase";

    //The slug names of WordPress pages you would like to display as tabs (if the slugs do not exist in your WordPress, the tabs will not appear)
    public static String[] PAGE_TABS = {"features", "questions", "contact"};

    //The names you want to display to the user, they can differ from the real slug names
    public static String[] PAGE_TAB_NAMES = {"features", "questions", "contact"};
    
    //Configuration Settings
    public static final int TIME_OUT_SECONDS = 30;
    public static final boolean USE_LAZY_LOADING = true;
    public static final boolean LOAD_LINKS_EXTERNALLY = false;
    public static final boolean REMOVE_WEBTITLE_CONTENT_AFTER_LAST_DASH = true;
    public static ImmutableMap.Builder<String, String> WEB_ELEMENTS_TO_REMOVE =  ImmutableMap.
            <String, String>builder()
            .put("header",".entry-header")
            .put(".masthead", ".entry-header")
            .put(".featured-image", ".main-content")
            .put(".wp-custom-header", ".entry-header")
            .put("nav", "")
            .put("footer", "")
            .put("aside", "");
            
}
