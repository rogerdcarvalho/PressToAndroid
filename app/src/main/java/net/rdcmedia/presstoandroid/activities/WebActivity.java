package net.rdcmedia.presstoandroid.activities;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.fragments.WebViewFragment;

public class WebActivity extends AppCompatActivity {

    private WebViewFragment webViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.loading_text);
        setContentView(R.layout.activity_web);
        String url = getIntent().getStringExtra("url");
        webViewFragment = new WebViewFragment();
        Bundle b = new Bundle();
        b.putString("url", url);
        b.putBoolean("setTitle", true);
        b.putBoolean("disableCache", true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        webViewFragment.setArguments(b);
        transaction.replace(R.id.frame, webViewFragment);
        transaction.commit();
    }

    public WebViewFragment getWebViewFragment() {
        return webViewFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webViewFragment.onDestroy();
        webViewFragment = null;
    }
}
