package com.think.siet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {
    View c,cplus,csharp,phy,java,php;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        c =(View) findViewById(R.id.c_gcc);
        cplus=(View)findViewById(R.id.c_plus);
        csharp =(View) findViewById(R.id.csharp);
        phy = (View) findViewById(R.id.phy);
        java =(View) findViewById(R.id.java);
        php = (View) findViewById(R.id.php);

        //navigating to C Compiler
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,LaunchActivity.class);
                i.putExtra("LANG_TAG","C");
                startActivity(i);
            }
        });

        //navigating to c++ compiler
        cplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,LaunchActivity.class);
                i.putExtra("LANG_TAG","C++");
                startActivity(i);
            }
        });

        //navigating to c# compiler
        csharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,LaunchActivity.class);
                i.putExtra("LANG_TAG","C#");
                startActivity(i);
            }
        });

        //navigating to Phython Compiler
        phy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this, LaunchActivity.class);
                i.putExtra("LANG_TAG","PHY");
                startActivity(i);
            }
        });

        //navigating to PHP Compiler
        php.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this, LaunchActivity.class);
                i.putExtra("LANG_TAG","PHP");
                startActivity(i);
            }
        });

        //navigating to JAVA Compiler
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this, LaunchActivity.class);
                i.putExtra("LANG_TAG","JAVA");
                startActivity(i);
            }
        });


    }


}
