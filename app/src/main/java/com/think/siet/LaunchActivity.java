package com.think.siet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.think.siet.activity.test.LineNumberEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaunchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tv;
    View run_it, clr, op;
    String sourceCode, sL, in_put, filename;
    EditText ip;
    ProgressDialog pDialog;
    private static final int REQUEST_CHOOSER = 1234;
    private static final String CODE_SAVING_FOLDER = "Polyglot";
    // contacts JSONArray
    JSONArray contacts = null;
    private LineNumberEditText eT;
    ProgressDialog progressDoalog;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "http://166.62.10.140/~pvppmzjvvhlg/polyglot/getData.php";


    NiftyDialogBuilder dialogBuilder;
    private EditText file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        dialogBuilder = NiftyDialogBuilder.getInstance(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tv = (TextView) findViewById(R.id.tv);

        run_it = (View) findViewById(R.id.click);
        clr = (View) findViewById(R.id.clear);
        op = (View) findViewById(R.id.op);
        ip = (EditText) findViewById(R.id.input_var);
        eT = (LineNumberEditText) findViewById(R.id.Scode);


        Intent in = getIntent();

        sL = in.getStringExtra("LANG_TAG");
        setTitle(sL + " Compiler");


        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                op.setVisibility(View.INVISIBLE);

                tv.setText(" ");


            }
        });


        run_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get spinner item
                // lang = "C";
                Intent in = getIntent();
                sL = in.getStringExtra("LANG_TAG");
                in_put = ip.getText().toString();
                sourceCode = eT.getText().toString();
                if (sourceCode.equals("")) {
                    Toast.makeText(LaunchActivity.this, "No Source Code Provided", Toast.LENGTH_SHORT).show();
                } else {

                    //API call with selected language type and source code
                    sendAndRequestResponse(sourceCode, sL, in_put);
                }

            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void sendAndRequestResponse(final String inputSourceCode, final String langSelected, String inputParams) {

        progressDoalog = new ProgressDialog(LaunchActivity.this);
        progressDoalog.setMessage("Compiling....");
        progressDoalog.show();

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        ///////////////////////////

        JSONObject jsonBodyObj = new JSONObject();

        try {
            jsonBodyObj.put("lang", getLanguageCode(langSelected));
            jsonBodyObj.put("source", inputSourceCode);

            if(inputParams != "") jsonBodyObj.put("input", inputParams);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBodyObj.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Response", String.valueOf(response));
                if (progressDoalog.isShowing()) progressDoalog.dismiss();

//                Toast.makeText(LaunchActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();
                processResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                if (progressDoalog.isShowing()) progressDoalog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }


            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }


        };

        mRequestQueue.add(jsonObjectRequest);


    }




    public void processResult(JSONObject response){
        String output = "";
        if (response.has("output")) {
            try{
                output = response.getString("output");
            }
            catch(Exception e){ Log.i("PARSING_FAIL", e.toString());}

            op.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(output)) {
                tv.setText(output);
                tv.setTextColor(getResources().getColor(R.color.bg_login));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent i = new Intent(LaunchActivity.this, FirstActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/docs");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        REQUEST_CHOOSER);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.",
                        Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_share) {
            File sd = Environment.getExternalStorageDirectory();
            File fileDir = new File(sd, "/Polyglot");
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileDir.getAbsolutePath() + "/"));
            startActivity(Intent.createChooser(email, "Email: Text File"));


        } else if (id == R.id.nav_send) {


        } else if (id == R.id.nav_abtus) {
            // navigate to about us
            Intent i = new Intent(LaunchActivity.this, AboutUsActivity.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected String getLanguageCode(String lang) {
        switch (lang) {
            case "PHP":
                return "php";
            case "C":
                return "c";
            case "JAVA":
                return "java";
            case "C++":
                return "cpp";
            case "C#":
                return "csharp";
            case "PHY":
                return "python2";

            default:
                return lang;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();

                    // Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    // Toast.makeText(LaunchActivity.this, "File Uri: " + uri.toString()+"\n"+path, Toast.LENGTH_LONG).show();
                    //  Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload

                    File file = new File(path);

                    String strLine = "";
                    StringBuilder text = new StringBuilder();

                    try {
                        FileReader fReader = new FileReader(file);
                        BufferedReader bReader = new BufferedReader(fReader);

                        /** Reading the contents of the file , line by line */
                        while ((strLine = bReader.readLine()) != null) {
                            text.append(strLine + "\n");
                        }

                        Toast.makeText(getBaseContext(), "Successfully loaded", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    eT.setText(text);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


}
