package com.example.multiscreenapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String MSG = "com.example.multiscreenapp.ORDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void placeOrder(View view){
        //Here we are hanfling the function of the button when clicked
        // Intent is used to switch btwn the activity
        //Now we are going to build an intent to go to the next activity

        Intent intent = new Intent(this, OrderActivity.class);



        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);

        String message = "Order for " + editText1.getText().toString() + ", "
                +  editText2.getText().toString() + " & "
                + editText3.getText().toString() + " has been placed succesfully";
        intent.putExtra(MSG, message);
        startActivity(intent);

    }
}