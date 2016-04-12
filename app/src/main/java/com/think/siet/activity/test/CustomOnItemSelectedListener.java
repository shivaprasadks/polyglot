package com.think.siet.activity.test;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
/**
 * Created by Super on 13-03-2016.
 */
public class CustomOnItemSelectedListener implements  OnItemSelectedListener{
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "Language Selected : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        String s = parent.getItemAtPosition(pos).toString();


    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
