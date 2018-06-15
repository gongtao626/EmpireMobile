package com.example.liaojingxi.empirecouriers;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

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

            String script = "var inputEle = document.getElementsByTagName('input')[0];inputEle.value = '"+lastText+"';var event1 = document.createEvent('HTMLEvents');  event1.initEvent('change', true, true); event1.eventType = 'change'; inputEle.dispatchEvent(event1);";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                browserTest.evaluateJavascript(script, null);
            } else {
                browserTest.loadUrl("javascript:" + script);
            }
            //Added preview of scanned barcode
            //ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
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
        //browserTest.setFocusable(false);
        //browserTest.setFocusableInTouchMode(false);

        final WebSettings webSettings = browserTest.getSettings();

        webSettings.setJavaScriptEnabled(true);
        //CookieManager.getInstance().setAcceptCookie(true);


        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webSettings.setSaveFormData(true);
        //webSettings.setDatabaseEnabled(true);


        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        //if (Build.VERSION.SDK_INT >= 21) {
        //webSettings.setAllowContentAccess(true);
        //}


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
                    camera.setVisibility(View.VISIBLE);

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
        progressBar = ProgressDialog.show(DriverLoginActivity.this, "", "Loading...");
        browserTest.loadUrl("https://empirecouriers.willowit.net.au:8443/driver/web?");

    }

    public void receivedLabel2(View view) {
        Log.d("test", "Scan button Starts to work!");

        //barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        //barcodeView.setVisibility(View.GONE);
        //Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.UPC_A, BarcodeFormat.EAN_13);

        //barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        if (barcodeView.getVisibility() == barcodeView.VISIBLE) {

            //TO add
            barcodeView.pause();

            barcodeView.setVisibility(View.GONE);

            light.setVisibility(View.GONE);

            //lastText = "";//To allow scan again same label
            //browserTest.setLayoutParams(WebView.LayoutParams(ViewGroup.LayoutParams.));
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
}
