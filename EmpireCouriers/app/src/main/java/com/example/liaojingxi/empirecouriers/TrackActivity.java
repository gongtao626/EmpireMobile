package com.example.liaojingxi.empirecouriers;

//import android.annotation.SuppressLint;
//import android.app.Activity;
import android.content.Intent;
//import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import im.delight.android.webview.AdvancedWebView;


public class TrackActivity extends AppCompatActivity {

    WebView browser; //Public or Private
    private static final String URL_STRING = "https://empirecouriers.willowit.net.au:8443/parcel_track_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.LABEL_NUMBER);

        browser=(WebView)findViewById(R.id.webkit);

        browser.getSettings().setJavaScriptEnabled(true);

        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("parcel_num", message);


        Collection<Map.Entry<String, String>> postData1 = mapParams.entrySet();

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return (true);
            }
        });

        webview_ClientPost(browser, "https://empirecouriers.willowit.net.au:8443/parcel_track_details", postData1);
        //String postDataW = "parcel_num=" + message;
        //String postDataW = "fileContents=" + URLEncoder.encode(message, "UTF-8");
        //browser.postUrl(URL_STRING, EncodingUtils.getBytes(postDataW, "BASE64"));

    }

    public static void webview_ClientPost(WebView webView, String url, Collection< Map.Entry<String, String>> postData){
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");

        webView.loadData(sb.toString(), "text/html", "UTF-8");//encoding with BASE64 IS OK as well
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
            browser.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

}

/*
public class TrackActivity extends Activity implements AdvancedWebView.Listener {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.LABEL_NUMBER);


        mWebView = (AdvancedWebView) findViewById(R.id.webkit);
        mWebView.setListener(this, this);
        final String myBaseUrl = "https://empirecouriers.willowit.net.au:8443/";
        mWebView.loadHtml(message, myBaseUrl);

        //mWebView.loadUrl("https://empirecouriers.willowit.net.au:8443/web/driverlogin");

        // ...
    }
    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }
}*/
