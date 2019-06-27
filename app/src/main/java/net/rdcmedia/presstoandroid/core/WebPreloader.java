package net.rdcmedia.presstoandroid.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.rdcmedia.presstoandroid.Configuration;
import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.activities.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class WebPreloader {
    private int mUrlsToLoad;
    private int mCurrentUrl;

    public WebPreloader(final MainActivity activity, final String[]urls) {
        mUrlsToLoad = urls.length;
        mCurrentUrl = 0;
        try {
            final WebView webView = new WebView(activity);
            final WebView finalWebView = webView;
            webView.clearCache(true);
            webView.getSettings().setAppCachePath( activity.getApplicationContext().getCacheDir().getAbsolutePath() );
            webView.getSettings().setAllowFileAccess( true );
            webView.getSettings().setAppCacheEnabled( true );
            webView.getSettings().setJavaScriptEnabled( true );
            webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
            webView.getSettings().setAppCacheMaxSize( 50 * 1024 * 1024 ); // 50MB
            boolean urlFound = false;
            for (String url : urls){
                if (url != null){
                    urlFound = true;
                    break;
                }
                mCurrentUrl += 1;
            }
            if (!urlFound){
                return;
            }
            webView.loadUrl( urls[mCurrentUrl] );
            //Set Webview callbacks
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {

                    for (Map.Entry element : Configuration.WEB_ELEMENTS_TO_REMOVE.build().entrySet()){
                        if (element.getValue().equals("")) {
                            webView.evaluateJavascript("jQuery('" + element.getKey() + "')" +
                                    ".remove();", null);
                        } else {
                            webView.evaluateJavascript("jQuery('" + element.getKey()  +
                                            "').not('"+ element.getValue() + "').remove();",
                                    null);
                        }
                    }

                    //Check if page is free to view
                    webView.evaluateJavascript("jQuery( \"body\" ).data();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            try {
                                JSONObject obj = new JSONObject(s);
                                if (obj.opt("requires_purchase") != null) {
                                    //Page is premium. Don't cache.
                                    mCurrentUrl += 1;
                                    if (mCurrentUrl < mUrlsToLoad) {
                                        finalWebView.loadUrl( urls[mCurrentUrl] );
                                    }
                                    else {
                                        finalWebView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                finalWebView.destroy();
                                            }
                                        });
                                        activity.onWebContentPreloaded();
                                    }

                                } else {

                                    String pathFilename = activity.getFilesDir().getPath() + File.separator +
                                            Integer.toString(mCurrentUrl) + ".mht";
                                    webView.saveWebArchive(pathFilename);
                                    SharedPreferences sharedPref = activity.getSharedPreferences(
                                            activity.getString(R.string.saved_preferences), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(activity.getString(R.string.saved_tab) + Integer.toString(mCurrentUrl) , pathFilename );
                                    editor.commit();
                                    mCurrentUrl += 1;
                                    if (mCurrentUrl < mUrlsToLoad) {
                                        finalWebView.loadUrl( urls[mCurrentUrl] );
                                    }
                                    else {
                                        finalWebView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                finalWebView.destroy();
                                            }
                                        });
                                        activity.onWebContentPreloaded();
                                    }
                                }


                            } catch (Throwable t) {

                            }
                        }
                    });
                }
            });
        }
        catch (Throwable e){
            System.out.println(e.getLocalizedMessage());
        }

    }
}
