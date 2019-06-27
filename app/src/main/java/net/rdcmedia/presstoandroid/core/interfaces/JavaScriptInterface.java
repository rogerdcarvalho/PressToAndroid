package net.rdcmedia.presstoandroid.core.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import net.rdcmedia.presstoandroid.activities.WebActivity;

public class JavaScriptInterface {

    //region Properties
    //---------------------------------------------------------------------------------------------

    private Activity activity;

    //endregion

    //region Constructors
    //---------------------------------------------------------------------------------------------

    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    //endregion

    //region Public Methods
    //---------------------------------------------------------------------------------------------


    @JavascriptInterface
    public void openLink(String url){
        Intent webIntent = new Intent( activity ,
                WebActivity.class);
        webIntent.putExtra("url", url);
        activity.startActivity(webIntent);
    }


    //endregion

}