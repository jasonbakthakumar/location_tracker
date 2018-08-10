package com.example.sri.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//Main activity that will be the primary interaction of the user with the app
public class MainActivity extends AppCompatActivity {

    //nameEditText is the textbox used to enter the name of the user
    EditText nameEditText;
	//phoneEditText is the textbox used by the user to enter his/her phone number
    EditText phoneEditText;
	//ergisterButton is 
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editText);
        phoneEditText = (EditText) findViewById(R.id.editText2);
        registerButton = (Button) findViewById(R.id.button);
        SharedPreferences prefs = getSharedPreferences("REG",MODE_PRIVATE);
        if(prefs.getBoolean("done",false)){
            startActivity(new Intent(MainActivity.this,Main2Activity.class));
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRightValidMobileNumber(phoneEditText.getText().toString())) {
                    String name = nameEditText.getText().toString();
                    String phone = phoneEditText.getText().toString().trim().replaceAll("\\s+", "");
                    SharedPreferences prefs = getSharedPreferences("REG", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", name);
                    editor.putBoolean("done", true);
                    editor.putString("phone", phone);
                    editor.apply();
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isRightValidMobileNumber(String s) {
        return s.length() == 10;
    }

}
