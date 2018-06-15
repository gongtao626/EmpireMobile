package com.example.liaojingxi.empirecouriers;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String LABEL_NUMBER = "TRACK_NUMBER";
    private EditText labelNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        labelNumber=findViewById(R.id.editText4);
        labelNumber.setGravity(Gravity.CENTER);

    }

    public void parcelTrackDetails(View view) {


        EditText editText = (EditText) this.findViewById(R.id.editText4);
        String message = editText.getText().toString();

        if(!TextUtils.isEmpty(message))
        {
            Intent intent = new Intent(this, TrackActivity.class);
            intent.putExtra(LABEL_NUMBER,message);
            this.startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please enter tracking number", Toast.LENGTH_LONG).show();
        }

    }

    public  void  goDriverLogin(View view) {
        Intent intent = new Intent(this,DriverLoginActivity.class);
        this.startActivity(intent);
    }

    public  void  goOrderLabel(View view) {
        Intent intent = new Intent(this,OrderLabelActivity.class);
        this.startActivity(intent);
    }

    public  void  goBoobCollection(View view) {
        Intent intent = new Intent(this,BookCollectionActivity.class);
        this.startActivity(intent);
    }





}
