package com.example.liaojingxi.empirecouriers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DriverLoginActivity extends AppCompatActivity {

    private static final String TAG = DriverLoginActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private Button camera;
    private Button light;
    private WebView browserTest;
    private ProgressBar pg1;
    private Menu menu;

    //TO add
    private boolean isTorchOn = false;

    private static final int DELAY = 1000; // 1 second

    private BarcodeCallback callback = new BarcodeCallback() {
        private long lastTimestamp = 0;

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                if(System.currentTimeMillis() - lastTimestamp < DELAY) {
                    // Too soon after the last barcode - ignore.
                    return;
                }
                lastTimestamp = System.currentTimeMillis();

                lastText = result.getText();

                barcodeView.setStatusText(result.getText());


                beepManager.setVibrateEnabled(true);
                //beepManager.setBeepEnabled(false);
                beepManager.playBeepSoundAndVibrate();

                String scriptForBlur = "document.getElementsByTagName('input')[0].blur();";

                //String script = "var inputEle = document.getElementsByTagName('input')[0];var event1 = document.createEvent('HTMLEvents');  event1.initEvent('change', true, true); event1.eventType = 'change'; inputEle.dispatchEvent(event1);";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    browserTest.evaluateJavascript(scriptForBlur, null);
                } else {
                    browserTest.loadUrl("javascript:" + scriptForBlur);
                }

                String script = "var inputEle = document.getElementsByTagName('input')[0];inputEle.value = '" + lastText + "';var event1 = document.createEvent('HTMLEvents');  event1.initEvent('change', true, true); event1.eventType = 'change'; inputEle.dispatchEvent(event1);";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    browserTest.evaluateJavascript(script, null);
                } else {
                    browserTest.loadUrl("javascript:" + script);
                }
                //Added preview of scanned barcode
                //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
                //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
            }
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        //camera = findViewById(R.id.cameraButton);
        //camera.setVisibility(View.GONE);
        //light = findViewById(R.id.lightButton);
        //light.setVisibility(View.GONE);
        beepManager = new BeepManager(this);//

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128, BarcodeFormat.EAN_8,BarcodeFormat.EAN_13, BarcodeFormat.QR_CODE, BarcodeFormat.UPC_E);

        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));

        barcodeView.setVisibility(View.GONE);

        barcodeView.decodeContinuous(callback);
        //barcodeView.decodeSingle(callback);

        //TO add

        browserTest = (WebView) findViewById(R.id.driverLoginAct2);

        final WebSettings webSettings = browserTest.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);


        browserTest.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/driver/web?")){
                    Log.d("DRIVER", "Log in with existing credentials");


                } else if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/web/driverlogout")){
                    if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

                        //TO add
                        barcodeView.pause();

                        barcodeView.setVisibility(View.GONE);


                    }
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);

                }
                view.loadUrl(url);

                return (true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                /*if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/web/driverlogin"))
                {

                }*/
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (url.contains("redirect")) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                   view.loadUrl("https://empirecouriers.willowit.net.au:8443/web/driverlogin");
                }else if(url.contains("https://empirecouriers.willowit.net.au:8443/driver/web?#action=driver.ui")){
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(true);
                    barcodeView.pause();
                }

            }
        });

        pg1= findViewById(R.id.progressBar1);

        browserTest.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if(newProgress==100){
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }
        });

        browserTest.loadUrl("https://empirecouriers.willowit.net.au:8443/driver/web?");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                receivedLabel_menu();
                return true;
            case R.id.action_light:
                torchOnOff_menu();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuaction, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void receivedLabel_menu() {
        Log.d("test", "Scan button Starts to work!");

        if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

            //TO add
            barcodeView.pause();

            barcodeView.setVisibility(View.GONE);
            //barcodeView.stopDecoding();
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.flash_off));

        } else {

            if (isTorchOn == true) {
                barcodeView.setTorchOff();
                menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.flash_off));
                isTorchOn = false;
            }

            lastText = "";//To allow scan again same label
            //TO add
            barcodeView.resume();

            barcodeView.setVisibility(View.VISIBLE);

            barcodeView.decodeContinuous(callback);

            String scriptForBlur = "document.getElementsByTagName('input')[0].blur();";

            //String script = "var inputEle = document.getElementsByTagName('input')[0];var event1 = document.createEvent('HTMLEvents');  event1.initEvent('change', true, true); event1.eventType = 'change'; inputEle.dispatchEvent(event1);";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                browserTest.evaluateJavascript(scriptForBlur, null);
            } else {
                browserTest.loadUrl("javascript:" + scriptForBlur);
            }

        }
    }

        //For menu action
        public void torchOnOff_menu() {

            isTorchOn = !isTorchOn;
            if(isTorchOn == true) {
                if(barcodeView.getVisibility() == barcodeView.VISIBLE)
                {
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.dark_ray));
                }
                barcodeView.setTorchOn();
            }else
            {
                if(barcodeView.getVisibility() == barcodeView.VISIBLE)
                {
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.flash_off));

                }

                barcodeView.setTorchOff();
            }
        }

}
