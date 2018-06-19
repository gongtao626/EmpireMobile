package com.example.liaojingxi.empirecouriers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

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
    private ProgressDialog progressBar;

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

        camera = findViewById(R.id.cameraButton);
        camera.setVisibility(View.GONE);
        light = findViewById(R.id.lightButton);
        light.setVisibility(View.GONE);
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
                    progressBar = ProgressDialog.show(DriverLoginActivity.this, "", "Loading...");
                } else if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/web/driverlogout")){
                    if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

                        //TO add
                        barcodeView.pause();

                        barcodeView.setVisibility(View.GONE);

                    }
                    light.setVisibility(View.GONE);

                    camera.setVisibility(View.GONE);
                }
                view.loadUrl(url);

                return (true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/web/driverlogin"))
                {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //if (progressBar.isShowing()) {
                //  progressBar.dismiss();
                //}
                if (url.contains("redirect")) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }

                    view.loadUrl("https://empirecouriers.willowit.net.au:8443/web/driverlogin");
                }else if(url.contains("https://empirecouriers.willowit.net.au:8443/driver/web?#action=driver.ui")){
                   // camera.setVisibility(View.VISIBLE);

                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    barcodeView.pause();
                }
                //else if(url.equalsIgnoreCase("https://empirecouriers.willowit.net.au:8443/driver/web?")){
                //progressBar = ProgressDialog.show(ContinuousCaptureActivity.this, "", "Loading...");
                //}
            }
        });
        //if(isNetworkAvailable()) {
          //  progressBar = ProgressDialog.show(DriverLoginActivity.this, "", "Loading...");
        //} //xin dai ma
        progressBar = ProgressDialog.show(DriverLoginActivity.this, "", "Loading...");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuaction, menu);
        return true;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();//Add permission of ACCESS_NETWORK_STATE
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }//xin dai ma

    public void receivedLabel2(View view) {
        Log.d("test", "Scan button Starts to work!");

        if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

            //TO add
            barcodeView.pause();

            barcodeView.setVisibility(View.GONE);

            light.setVisibility(View.GONE);

        } else {

            if(isTorchOn == true) {
                barcodeView.setTorchOff();
                isTorchOn = false;
            }
            light.setVisibility(View.VISIBLE);

            lastText = "";//To allow scan again same label
            //TO add
            barcodeView.resume();

            //browserTest.setLayoutParams(new ViewGroup.LayoutParams(200, 400));

            barcodeView.setVisibility(View.VISIBLE);

            //barcodeView.decodeContinuous(callback);

            String scriptForBlur = "document.getElementsByTagName('input')[0].blur();";

            //String script = "var inputEle = document.getElementsByTagName('input')[0];var event1 = document.createEvent('HTMLEvents');  event1.initEvent('change', true, true); event1.eventType = 'change'; inputEle.dispatchEvent(event1);";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                browserTest.evaluateJavascript(scriptForBlur, null);
            } else {
                browserTest.loadUrl("javascript:" + scriptForBlur);
            }
        }//end else
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

    //TO add
    //One button for light
    public void torchOnOff(View view) {

        isTorchOn = !isTorchOn;
        if(isTorchOn == true) {
            barcodeView.setTorchOn();
        }else
        {
            barcodeView.setTorchOff();
        }
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

        //barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        //barcodeView.setVisibility(View.GONE);
        //Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.UPC_A, BarcodeFormat.EAN_13);

        //barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

            //TO add
            barcodeView.pause();

            barcodeView.setVisibility(View.GONE);
            //barcodeView.stopDecoding();
            //light.setVisibility(View.GONE);

            //lastText = "";//To allow scan again same label
            //browserTest.setLayoutParams(WebView.LayoutParams(ViewGroup.LayoutParams.));
        } else {

            if (isTorchOn == true) {
                barcodeView.setTorchOff();
                isTorchOn = false;
            }
            //light.setVisibility(View.VISIBLE);

            lastText = "";//To allow scan again same label
            //TO add
            barcodeView.resume();

            //browserTest.setLayoutParams(new ViewGroup.LayoutParams(200, 400));

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
    }//end else

        //For menu action
        public void torchOnOff_menu() {

            isTorchOn = !isTorchOn;
            if(isTorchOn == true) {
                barcodeView.setTorchOn();
            }else
            {
                barcodeView.setTorchOff();
            }
        }

}
