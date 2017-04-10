package com.example.sri.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sri on 29/3/17.
 */

public class CustomAdapter extends BaseAdapter {

    Context context;
    Set<String> nameString = new HashSet<>();
    Set<String> phoneString = new HashSet<>();

    public CustomAdapter(Set<String> nameString, Set<String> phoneString, Context context) {
        this.nameString = nameString;
        this.phoneString = phoneString;
        this.context = context;
    }

    @Override
    public int getCount() {
        return nameString.size();
    }

    @Override
    public Object getItem(int position) {
        List<String> list = new ArrayList<String>(nameString);
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.list_stuff, null);
        String[] nameStringss = nameString.toArray(new String[nameString.size()]);
        String[] phoneNumberss = phoneString.toArray(new String[phoneString.size()]);
        TextView nameView = (TextView) view.findViewById(R.id.textView3);
        TextView phoneNumber = (TextView) view.findViewById(R.id.textView4);
        nameView.setText(nameStringss[position]);
        phoneNumber.setText(phoneNumberss[position]);
        return view;


    }


}
