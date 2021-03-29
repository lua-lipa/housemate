package com.example.housemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BillsActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbill);




        Button addBillButton = (Button) findViewById(R.id.AddBillButton);
        addBillButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText title_field = (EditText) findViewById(R.id.billTitleField);
                EditText amount_field = (EditText) findViewById(R.id.BillAmountField);
                EditText assignee_field = (EditText) findViewById(R.id.BillAssignField);

                String title = title_field.getText().toString();
                String amount = amount_field.getText().toString();
                String assignee = assignee_field.getText().toString();


                if (title.length()!=0 && amount.length() != 0 && assignee.length() != 0) {
                    // Filed is not empty do your code here.
                } else {
                    if(title.length() == 0) {
                        title_field.requestFocus();
                        title_field.setError("Enter Text");
                        Toast.makeText(getApplicationContext(),"Enter Text",Toast.LENGTH_SHORT).show();
                    }

                    if (amount.length() == 0) {
                        amount_field.requestFocus();
                        amount_field.setError("Enter Text");
                        Toast.makeText(getApplicationContext(),"Enter Amount",Toast.LENGTH_SHORT).show();
                    }

                    if (assignee.length() == 0) {
                        assignee_field.requestFocus();
                        assignee_field.setError("Enter Text");
                        Toast.makeText(getApplicationContext(),"Enter Assignee",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     public void add_bill(View view) { //parse the bills page
     Intent intent = new Intent(this, DisplayMessageActivity.class);
     EditText title_field = (EditText) findViewById(R.id.billTitleField);
     EditText amount_field = (EditText) findViewById(R.id.BillAmountField);
     EditText assignee_field = (EditText) findViewById(R.id.BillAssignField);

     String title = title_field.getText().toString();
     String amount = title_field.getText().toString();
     String assignee = title_field.getText().toString();

     intent.putExtra(EXTRA_MESSAGE, title);
     intent.putExtra(EXTRA_MESSAGE, amount);
     intent.putExtra(EXTRA_MESSAGE, assignee);

     startActivity(intent);
     }

     **/
}