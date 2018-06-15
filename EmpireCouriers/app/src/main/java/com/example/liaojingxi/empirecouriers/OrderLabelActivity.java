package com.example.liaojingxi.empirecouriers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Random;

public class OrderLabelActivity extends AppCompatActivity {
    private EditText mEditTextBusinessName;
    private EditText mEditTextContactNumber;
    private EditText mEditTextLabelQuantity;
    private EditText mEditTextAdditionalInfo;
    private Button sendButton;
    private Spinner spinner;
    private String selectedLabelType;
    String[] SPINNERVALUES = {"NEXT DAY","NW/HBT","LOCAL","COUNTRY","FREIGHT FORWARD","PALLET","OTHER"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_label);

        mEditTextBusinessName = findViewById(R.id.editTextBusinessName);
        mEditTextContactNumber = findViewById(R.id.editTextContactNumber);
        mEditTextLabelQuantity = findViewById(R.id.editTextLabelQuantity);
        mEditTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);

        sendButton = findViewById(R.id.sendButton);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(OrderLabelActivity.this, android.R.layout.simple_list_item_1, SPINNERVALUES);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(OrderLabelActivity.this,spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
                selectedLabelType = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
        String subject = "Order Number: " + randomNum;
        String businessName = mEditTextBusinessName.getText().toString();
        String contactNumber = mEditTextContactNumber.getText().toString();
        String labelQuantity = mEditTextLabelQuantity.getText().toString();
        String additionalInfo = mEditTextAdditionalInfo.getText().toString();

        String finalMessage = "Business name: " + businessName + "\n" + "Contact Number: " + contactNumber + "\n" +"Label Quantity: " + labelQuantity + "\n" +
                "Label Type: " + selectedLabelType + "\n" + "Additional Info:" + additionalInfo;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,finalMessage);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Chose an email client"));

    }
}
