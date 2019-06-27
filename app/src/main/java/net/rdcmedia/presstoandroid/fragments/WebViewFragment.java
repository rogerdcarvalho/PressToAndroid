package net.rdcmedia.presstoandroid.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.rdcmedia.presstoandroid.Configuration;
import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.activities.MainActivity;
import net.rdcmedia.presstoandroid.activities.WebActivity;
import net.rdcmedia.presstoandroid.core.interfaces.JavaScriptInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WebViewFragment extends Fragment {

    //region Properties
    //---------------------------------------------------------------------------------------------
    //Views
    private WebView mWebView;
    private ViewGroup mProgressBarParent;
    private View mProgressBar;

    //Member Data Properties
    private boolean mProcessedPage;
    private String mUrl;

    //Public Shared Properties
    public boolean disableCache;
    public boolean setTitle;

    //Properties for file uploads
    private final static int FILECHOOSER_RESULTCODE=1;
    private ValueCallback<Uri[]> mUploadMessage;
    private Uri mOutputFileUri;


    //endregion

    //region Constructors
    //---------------------------------------------------------------------------------------------

    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url, boolean disableCache, boolean setTitle) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putBoolean("disableCache", disableCache);
        args.putBoolean("setTitle", setTitle);
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Android Callbacks
    //---------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {

        //A file was selected for upload. Return the URI to the webpage
        if(requestCode==FILECHOOSER_RESULTCODE && mUploadMessage != null)
        {

            if (intent != null) {
                Uri result = intent.getData();
                Uri [] results = new Uri[1];
                results[0] = result;
                mUploadMessage.onReceiveValue(results);
            }
            else if (mOutputFileUri != null) {
                Uri [] results = new Uri[1];
                results[0] = mOutputFileUri;
                mUploadMessage.onReceiveValue(results);
            }
            else {
                Uri [] results = new Uri[]{};
                mUploadMessage.onReceiveValue(results);

            }

            mUploadMessage = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check which URL to open and whether to change the Activity Title
        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
            if (getArguments().getBoolean("setTitle")){
                setTitle = getArguments().getBoolean("setTitle");
            }
            if (getArguments().getBoolean("disableCache")){
                disableCache = getArguments().getBoolean("disableCache");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment and get views
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = (WebView)view.findViewById(R.id.webView);
        mProgressBar = view.findViewById(R.id.progressBarWebview);
        mProgressBarParent = (ViewGroup) mProgressBar.getParent();

        //Hide webview to ensure users only see content they have access to
        mWebView.setVisibility(View.INVISIBLE);

        //Don't indicate loading status until we're sure page isn't loading from cache
        mProgressBar.setVisibility(View.INVISIBLE);

        //Setup the JavaScript interface
        JavaScriptInterface jsInterface = new JavaScriptInterface(getActivity());
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled( false );
        webSettings.setCacheMode( WebSettings.LOAD_NO_CACHE );

        if ( !disableCache ) { // load from cache where possible
            webSettings.setAppCachePath( getActivity().getApplicationContext().getCacheDir()
                    .getAbsolutePath() );
            webSettings.setAppCacheEnabled( true );
            webSettings.setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mWebView.addJavascriptInterface(jsInterface, "JSInterface");

        //Setup Webview basic callbacks
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                //Remove loading indicator and remove any web elements not relevant for mobile view
                mProgressBarParent.removeView(mProgressBar);

                for (Map.Entry element : Configuration.WEB_ELEMENTS_TO_REMOVE.build().entrySet()){
                    if (element.getValue().equals("")) {
                        mWebView.evaluateJavascript("jQuery('" + element.getKey() + "')" +
                                ".remove();", null);
                    } else {
                        mWebView.evaluateJavascript("jQuery('" + element.getKey()  +
                                        "').not('"+ element.getValue() + "').remove();",
                                null);
                    }
                }

                //Scroll to top
                mWebView.scrollTo(0, 0);
                mProcessedPage = true;
                showContent();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Open all links in external browser if set
                if (Configuration.LOAD_LINKS_EXTERNALLY){
                    if (url != null ) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    Intent webIntent = new Intent( getActivity() ,
                            WebActivity.class);
                    webIntent.putExtra("url", url);
                    getActivity().startActivity(webIntent);
                    return true;
                }
            }
        });
        //Setup Webview file upload
        mWebView.setWebChromeClient(new WebChromeClient() {

            //Allow users to upload images
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                File photo = new File(Environment.getExternalStorageDirectory(),
                        timeStamp + ".jpg");
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                mOutputFileUri = Uri.fromFile(photo);

                Intent choosePicture = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //Create the Chooser
                final Intent chooserIntent = Intent.createChooser(choosePicture, "Select Source");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePicture });
                WebViewFragment.this.startActivityForResult(chooserIntent, WebViewFragment.FILECHOOSER_RESULTCODE);

                mUploadMessage = filePathCallback;
                return true;

            }
        });

        //Load Url
        String lastThreeChars = mUrl.substring(mUrl.length() - 3, mUrl.length());
        if (lastThreeChars.equals("mht")){
            mWebView.loadUrl("file://" + mUrl);
        } else {
            mWebView.loadUrl(mUrl);
        }

        //Ensure bottom of view isn't covered by bottom navigation
        if (getActivity() != null && getActivity().getClass() == MainActivity.class){
            int bottomMargin = ((MainActivity)getActivity()).getBottomNavigationMargin();
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mWebView.getLayoutParams();
            p.setMargins(0,  0 , 0, bottomMargin);
            mWebView.requestLayout();
        }

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        if (mWebView != null) {
            mWebView.restoreState(savedInstanceState);
        }
    }


    //endregion

    //region Methods
    //---------------------------------------------------------------------------------------------


    public void load(String url){
        if (!mUrl.equals(url)){
            mWebView.loadUrl(url);
            mUrl = url;
        }
    }

    public void reload(){
        mWebView.loadUrl(mUrl);
    }

    private void showContent(){
        mWebView.setVisibility(View.VISIBLE);
        if (setTitle) {
            String title = mWebView.getTitle();
            if (Configuration.REMOVE_WEBTITLE_CONTENT_AFTER_LAST_DASH){
                int dashPosition = title.lastIndexOf("–");
                if (dashPosition > 0){
                    title = mWebView.getTitle().substring(0, dashPosition);
                }
            }
            if (title.length() > 25){
                title = title.substring(0, 24) + "...";
            }
            if (getActivity() != null) {
                getActivity().setTitle(title);
            }
        }
    }

    //endregion
}
