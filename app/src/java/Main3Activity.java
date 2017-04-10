package com.example.sri.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main3Activity extends AppCompatActivity {


    static final int PICK_CONTACT=1;
    TextView emptyView;
    ListView contactList;
    Button addButton;
    Set<String> phoneSet;


    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        emptyView = (TextView) findViewById(R.id.textView2);
        contactList = (ListView) findViewById(R.id.listView2);
        addButton = (Button) findViewById(R.id.button5);
        preferences =  getSharedPreferences("CONTACTS",MODE_PRIVATE);
        if(preferences.getBoolean("done",true)){
            contactList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            updateTheFilledUI();
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

            }
        });

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Main3Activity.this, , Toast.LENGTH_SHORT).show();
                List<String> list = new ArrayList<String>(phoneSet);
                Toast.makeText(Main3Activity.this, list.get(position), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT :
                if (resultCode == RESULT_OK) {
                    String cNumber = "";
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if(preferences.getStringSet("nameSet",null) == null){
                            //first time..
                            SharedPreferences.Editor editor = preferences.edit();
                            Set<String> nameSet =new HashSet<String>();
                            Set<String> phoneSet =new HashSet<String>();
                            nameSet.add(name);
                            phoneSet.add(cNumber);
                            editor.putStringSet("nameSet",nameSet);
                            editor.putStringSet("phoneSet",phoneSet);
                            editor.putBoolean("done",false);
                            editor.apply();
                            updateTheFilledUI();
                        }
                        else{
                            //already done
                            SharedPreferences.Editor editor = preferences.edit();
                            Set<String> nameSet =preferences.getStringSet("nameSet",null);
                            Set<String> phoneSet = preferences.getStringSet("phoneSet",null);
                            nameSet.add(name);
                            phoneSet.add(cNumber);
                            editor.putStringSet("nameSet",nameSet);
                            editor.putStringSet("phoneSet",phoneSet);
                            editor.apply();
                            updateTheFilledUI();
                        }

                    }
                }
                break;
        }
    }


    private void updateTheFilledUI() {
        emptyView.setVisibility(View.GONE);
        contactList.setVisibility(View.VISIBLE);
        Set<String> nameSet = preferences.getStringSet("nameSet",null);
        phoneSet = preferences.getStringSet("phoneSet",null);
        CustomAdapter customAdapter = new CustomAdapter(nameSet,phoneSet,this);
        contactList.setAdapter(customAdapter);

    }


}
