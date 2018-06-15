package com.example.liaojingxi.empirecouriers;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class BookCollectionActivity extends AppCompatActivity {
    private EditText mEditTextBusinessName;
    private EditText mEditTextContactNumber;
    private EditText mEditTextNumberOfPackages;
    private EditText mEditTextPackageType;
    private EditText mEditTextSuburb;
    private EditText mEditTextAdditionalInfo;
    private Button sendButton;

    private TextView mTextViewDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_collection);
        mEditTextBusinessName = findViewById(R.id.editTextBusinessName);
        mEditTextContactNumber = findViewById(R.id.editTextContactNumber);
        mEditTextNumberOfPackages = findViewById(R.id.editTextNumberOfPackages);
        mEditTextPackageType = findViewById(R.id.editTextPackageType);
        mEditTextSuburb = findViewById(R.id.editTextSuburb);
        mEditTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        sendButton = findViewById(R.id.sendButton);

        mTextViewDescription = findViewById(R.id.textViewDescription);

        mTextViewDescription.setText(Html.fromHtml("*Please ensure booking via the app is processed by <strong><font color=#0000ff>3:00 P.M.</font></strong> for a same-day collection. Call us on (03) 6272 3535 for anything urgent !"));
        //mTextViewDescription.setText("*Please ensure booking via the app is processed by 3:00 P.M. for a same-day collection. Call us on (03) 6272 3535 for anything urgent !");
        Linkify.addLinks(mTextViewDescription, Linkify.PHONE_NUMBERS);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String recipient = "booking@empirecouriers.com.au";
        Random random = new Random();
        int randomNum = random.nextInt((999999 - 100000) +1) + 100000;
        String subject = "Pick up require: " + randomNum;
        String businessName = mEditTextBusinessName.getText().toString();
        String contactNumber = mEditTextContactNumber.getText().toString();
        String numberOfPackages = mEditTextNumberOfPackages.getText().toString();
        String packageType = mEditTextPackageType.getText().toString();
        String suburb = mEditTextSuburb.getText().toString();
        String additionalInfo = mEditTextAdditionalInfo.getText().toString();

        String finalMessage = "Business name:" + businessName + "\n" + "Contact Number: " + contactNumber + "\n" + "Number Of Packages: " + numberOfPackages +
        "\n" + "Package Type: " +packageType + "\n" +"Suburb: " + suburb + "\n" + "Additional Info: " + additionalInfo;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,finalMessage);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Chose an email client"));
    }

}
