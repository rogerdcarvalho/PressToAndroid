package net.rdcmedia.presstoandroid.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import net.rdcmedia.presstoandroid.Configuration;
import net.rdcmedia.presstoandroid.fragments.PostsFragment;
import net.rdcmedia.presstoandroid.fragments.WebViewFragment;
import net.rdcmedia.presstoandroid.core.interfaces.DataManagerDelegate;
import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.model.Page;
import net.rdcmedia.presstoandroid.model.Post;
import net.rdcmedia.presstoandroid.core.WebPreloader;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataManagerDelegate {

    //region Properties
    //---------------------------------------------------------------------------------------------

    //Views
    protected TextView mTextMessage;
    protected MainActivityViewModel mainActivityViewModel;
    protected PostsFragment postsFragment;
    protected WebViewFragment[] webViewFragment;
    public BottomNavigationViewEx navigation;
    protected BroadcastReceiver networkStateReceiver;
    protected ProgressBar progressBar;

    //endregion

    //region Android Callbacks
    //---------------------------------------------------------------------------------------------


    @Override
    public void onBackPressed() {
        if (!isTaskRoot()) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit_title))
                    .setMessage(getString(R.string.exit_message))
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.super.onBackPressed();
                                }
                            })
                    .setNegativeButton(getString(R.string.no),
                            null)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect ViewModel and set properties to retain
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.activeTab = 0;
        mainActivityViewModel.dataObjectListsToLoad = 2;
        mainActivityViewModel.cacheDisabled = false;

        //Setup views
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.mainActivityProgressBar);
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        //Get Bottom Navigation margin to prevent it overlapping other content
        final View bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.post(new Runnable() {
            @Override
            public void run() {
                mainActivityViewModel.bottomNavigationMargin = bottomNavigationView.getMeasuredHeight();
            }
        });

        //Setup bottom navigation menu
        ArrayList<String> tabNames = new ArrayList();
        navigation.getMenu().clear();
        int i = 0;
        if (Configuration.SHOW_BLOG == true) {
            navigation.getMenu().add(Menu.NONE, i, Menu.NONE, Configuration.POSTS_TAB).setIcon(R.mipmap.ic_action_news);
            setTitle(Configuration.POSTS_TAB);
            tabNames.add(Configuration.POSTS_TAB);
            i += 1;
        }
        for (String tab : Configuration.PAGE_TABS) {
            if (!tab.equals("")) {
                String tabName = tab.substring(0, 1).toUpperCase() + tab.substring(1);
                navigation.getMenu().add(Menu.NONE, i, Menu.NONE, tabName).setIcon(getDrawableIdentifier(Configuration.TAB_ICONS[i]));
                tabNames.add(tabName);
            }
            i += 1;
        }
        mainActivityViewModel.tabNames = tabNames.toArray(new String[0]);
        navigation.enableAnimation(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Setup network listener for online/offline events
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isNetworkAvailable() && !mainActivityViewModel.dataLoaded) {
                    mainActivityViewModel.dataObjectListsToLoad = 2;
                    getAllData();
                }
            }
        };

        if (!isNetworkAvailable()) {
            mainActivityViewModel.dataObjectListsToLoad = 0;
            onObjectsLoaded();
        }

        //Failsafe for when caching mechanism fails, after 5 seconds of inactivity, just load UI and
        //get web content from network
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mainActivityViewModel.dataLoaded && !mainActivityViewModel.uiLoaded) {
                    mainActivityViewModel.cacheDisabled = true;
                    onWebContentPreloaded();
                }

            }
        }, 10000);

    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        setTitle(mainActivityViewModel.tabNames[mainActivityViewModel.activeTab]);
    }

    //endregion

    //region Methods
    //---------------------------------------------------------------------------------------------

    private boolean applyWebViewFragment(int item) {
        final int itemId;

        //Determine the right ID for the webview fragment depending on whether a postsfragment
        //exists
        if (!Configuration.SHOW_BLOG) {
            itemId = item + 1;
        } else {
            itemId = item;
        }

        //Get tabs from SharedPreferences if offline
        if (!isNetworkAvailable()){
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.saved_preferences), Context.MODE_PRIVATE);
            String url = sharedPref.getString(getString(R.string.saved_tab) +
                    Integer.toString(itemId - 1), null);

            if (url != null) {
                if (webViewFragment == null) {
                    webViewFragment = new WebViewFragment[Configuration.PAGE_TABS.length];
                }
                if (webViewFragment[itemId - 1] == null) {
                    webViewFragment[itemId - 1] = new WebViewFragment();
                }
                Bundle b = new Bundle();
                b.putString("url", url);
                b.putBoolean("setTitle", false);
                loadFragment(webViewFragment[itemId - 1], b);
                mainActivityViewModel.activeWebFragment = webViewFragment[itemId - 1];
                if (Configuration.SHOW_BLOG) {
                    setTitle(mainActivityViewModel.tabNames[itemId]);
                } else {
                    setTitle(mainActivityViewModel.tabNames[itemId - 1]);
                }
                mainActivityViewModel.loadedCachedContent = true;
                return true;
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_connection_title))
                        .setMessage(getString(R.string.no_connection_message))
                        .setPositiveButton(getString(R.string.close_button_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                return false;
            }
        } else {
            //Link up API received Pages to desired Page tabs
            ArrayList<Page> allPages = mainActivityViewModel.getPages().getValue();

            //If no pages can be found at all, inform the user and return
            if (allPages == null) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_blog_content_title))
                        .setMessage(getString(R.string.no_blog_content_message))
                        .setPositiveButton(getString(R.string.close_button_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                return false;
            }

            //Match page to available pages
            Iterable desiredPageIterable = Iterables.filter(allPages, new Predicate<Page>() {
                @Override
                public boolean apply(Page input) {
                    return input.getSlug().toLowerCase().equals(Configuration.PAGE_TABS[itemId - 1].toLowerCase());
                }
            });

            Page[] desiredPageArray = Iterables.toArray(desiredPageIterable, Page.class);
            if (desiredPageArray.length > 0) {

                //Load the respective URL
                String url = desiredPageArray[0].getLink();
                if (webViewFragment == null) {
                    webViewFragment = new WebViewFragment[Configuration.PAGE_TABS.length];
                }
                boolean fragmentExisted = false;
                if (webViewFragment[itemId - 1] == null) {
                    webViewFragment[itemId - 1] = new WebViewFragment();
                } else {
                    fragmentExisted = true;
                }

                Bundle b = new Bundle();
                b.putString("url", url);
                b.putBoolean("setTitle", false);
                b.putBoolean("disableCache", mainActivityViewModel.cacheDisabled);
                loadFragment(webViewFragment[itemId - 1], b);
                mainActivityViewModel.activeWebFragment = webViewFragment[itemId - 1];
                if (fragmentExisted && mainActivityViewModel.loadedCachedContent &&
                        mainActivityViewModel.cacheCompleted){
                    webViewFragment[itemId - 1].load(url);
                }

                if (Configuration.SHOW_BLOG) {
                    setTitle(mainActivityViewModel.tabNames[itemId]);
                } else {
                    setTitle(mainActivityViewModel.tabNames[itemId - 1]);
                }

                return true;
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_blog_content_title))
                        .setMessage(getString(R.string.no_blog_content_message))
                        .setPositiveButton(getString(R.string.close_button_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                return false;
            }
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //Only allow users to select menu options when the UI is ready
            if (!mainActivityViewModel.uiLoaded){
                return false;
            }

            //Store the user selection and open respective tab or show error message
            final int itemId = item.getItemId();
            if (itemId == 0 && Configuration.SHOW_BLOG){
                if (isNetworkAvailable()){
                    if (mainActivityViewModel.getPosts().getValue() != null){
                        if (postsFragment == null) {
                            postsFragment = new PostsFragment();
                        }

                        Bundle b = new Bundle();
                        loadFragment(postsFragment, b);
                        mainActivityViewModel.activeWebFragment = null;
                        setTitle(mainActivityViewModel.tabNames[itemId]);
                        mainActivityViewModel.activeTab = itemId;
                        return true;
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.no_blog_content_title))
                                .setMessage(getString(R.string.no_blog_content_message))
                                .setPositiveButton(getString(R.string.close_button_text),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                    }
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.no_connection_title))
                            .setMessage(getString(R.string.no_connection_message))
                            .setPositiveButton(getString(R.string.close_button_text),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                    return false;
                }
            } else {
                if (applyWebViewFragment(itemId)) {
                    mainActivityViewModel.activeTab = itemId;
                    return true;
                }
            }
            return false;
        }
    };

    protected void cacheWebContent(){
        String [] urlsToPreload = new String[Configuration.PAGE_TABS.length];
        ArrayList<Page> allPages = mainActivityViewModel.getPages().getValue();
        if (allPages == null){
            return;
        }
        for (int i = 0; i < Configuration.PAGE_TABS.length; i++) {
            final int tabNum = i;
            Iterable desiredPageIterable = Iterables.filter(allPages, new Predicate<Page>() {
                @Override
                public boolean apply(Page input) {
                    return input.getSlug().toLowerCase().equals(Configuration.PAGE_TABS[tabNum].toLowerCase());
                }
            });

            Page[] desiredPageArray = Iterables.toArray(desiredPageIterable, Page.class);
            if (desiredPageArray.length > 0) {
                String url = desiredPageArray[0].getLink();
                urlsToPreload[i] = url;
            }
        }

        mainActivityViewModel.webPreloader = new WebPreloader(this, urlsToPreload);

    }


    private void getAllData() {
        System.out.println("------------------------------------> Get All Data Called");

        //Load up pages from Wordpress. Once pages are received, the callback will handle follow
        //up calls
        mainActivityViewModel.getPages().observe(this,
                new Observer<ArrayList<Page>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Page> obj) {
                        mainActivityViewModel.dataObjectListsToLoad -= 1;
                        onObjectsLoaded();
                    }
                });

    }
    public int getBottomNavigationMargin(){
        return mainActivityViewModel.bottomNavigationMargin;
    }

    protected int getDrawableIdentifier(String name) {
        return getResources().getIdentifier(name, "mipmap", getPackageName());
    }

    public MutableLiveData<ArrayList<Post>> getPosts(){
        return mainActivityViewModel.getPosts();
    }

    public WebViewFragment getActiveWebViewFragment(){
        return mainActivityViewModel.activeWebFragment;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean hasMorePosts(){
        return mainActivityViewModel.hasAdditionalObjectsOnServer(Post.class);
    }

    private void loadFirstFragment() {
        System.out.println("------------------------------------> loadFirstFragment called");

        //Load the first relevant fragment
        if (isNetworkAvailable()) {
            if (Configuration.SHOW_BLOG == true && mainActivityViewModel.getPosts().getValue() != null
                    && mainActivityViewModel.getPosts().getValue().size() > 0) {
                postsFragment = new PostsFragment();
                Bundle b = new Bundle();
                loadFragment(postsFragment, b);
            }  else {
                if (Configuration.SHOW_BLOG){

                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.no_blog_content_title))
                            .setMessage(getString(R.string.no_blog_content_message))
                            .setPositiveButton(getString(R.string.close_button_text),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                } else {
                    applyWebViewFragment(0);
                }
            }
        } else {

            if (Configuration.SHOW_BLOG) {
                navigation.post(new Runnable() {
                    @Override
                    public void run() {
                        navigation.setCurrentItem(1);
                    }
                });
            } else {
                applyWebViewFragment(0);
            }
        }
    }


    private void loadFragment(Fragment fragment, Bundle bundle) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);

        //Either add the fragment if it was never loaded before, or show a previously loaded one
        for (Fragment otherFragment : getSupportFragmentManager().getFragments()) {
            transaction.hide(otherFragment);
        }
        if (getSupportFragmentManager().findFragmentByTag(fragment.getTag()) != null){

            transaction.show(fragment);
            fragment.onResume();
        } else {
            transaction.add(R.id.frame, fragment, Integer.toString(mainActivityViewModel.activeTab));
        }

        transaction.commit();
    }

    public void loadMorePosts(){

        if (!mainActivityViewModel.loading){
            mainActivityViewModel.dataObjectListsToLoad = 1;
            mainActivityViewModel.getAdditionalPosts();
        }
    }

    public void onApiFailed() {
        System.out.println("------------------------------------> Api failed");

    }

    public void onObjectsLoaded() {
        System.out.println("------------------------------------> onObjectsLoaded called");

        if (mainActivityViewModel.dataObjectListsToLoad == 1) {
            mainActivityViewModel.getPosts().observe(this,
                    new Observer<ArrayList<Post>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<Post> obj) {
                            mainActivityViewModel.dataObjectListsToLoad -= 1;
                            onObjectsLoaded();
                        }
                    });
        }

        if (mainActivityViewModel.dataObjectListsToLoad == 0){

            if (isNetworkAvailable()){
                mainActivityViewModel.loading = false;
                mainActivityViewModel.dataLoaded = true;
                if (!mainActivityViewModel.cacheCompleted) {
                    cacheWebContent();
                }
                if (postsFragment != null) {
                    postsFragment.onReload();
                }
            } else {
                mainActivityViewModel.loading = false;
                mainActivityViewModel.dataLoaded = false;
                ViewGroup parent = (ViewGroup)progressBar.getParent();
                parent.removeView(progressBar);
                progressBar = null;
                if (!mainActivityViewModel.uiLoaded) {
                    loadFirstFragment();
                    mainActivityViewModel.uiLoaded = true;
                } else if (postsFragment != null) {
                    postsFragment.onReload();
                }
            }
        }
    }

    public void onWebContentPreloaded(){
        mainActivityViewModel.webPreloader = null;
        if (progressBar != null){
            ViewGroup parent = (ViewGroup)progressBar.getParent();
            parent.removeView(progressBar);
            progressBar = null;
        }
        if (!mainActivityViewModel.uiLoaded) {
            loadFirstFragment();
            mainActivityViewModel.uiLoaded = true;
        }
        mainActivityViewModel.cacheCompleted = true;
    }

    public void reload(){
        if (!mainActivityViewModel.loading){
            mainActivityViewModel.loading = true;
            mainActivityViewModel.dataObjectListsToLoad = 2;
            mainActivityViewModel.reset();
            getAllData();
        }
    }
}
