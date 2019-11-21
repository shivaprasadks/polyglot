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

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.think.siet.activity.test.LineNumberEditText;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LaunchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tv;
    View run_it, clr, op;
    String s, lang, sL, in_put, filename;
    EditText ip;
    ProgressDialog pDialog;
    private static final int REQUEST_CHOOSER = 1234;
    private static final String CODE_SAVING_FOLDER = "Polyglot";
    // contacts JSONArray
    JSONArray contacts = null;
    private LineNumberEditText eT;

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
                in_put = ip.getText().toString();
                s = eT.getText().toString();
                if (s.equals("")) {
                    Toast.makeText(LaunchActivity.this, "No Source Code Provided", Toast.LENGTH_SHORT).show();
                } else {
                    //  Toast.makeText(LaunchActivity.this, "not getting", Toast.LENGTH_SHORT).show();
                    new JsonAsync().execute();
                }
                // new JsonAsync().execute();


            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog dialog = new Dialog(LaunchActivity.this);
                dialog.setContentView(R.layout.activity_dialog);
                dialog.setTitle("Save file as");


                Button button = (Button) dialog.findViewById(R.id.dialog_ok);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        EditText edit = (EditText) dialog.findViewById(R.id.fileName);
                        String text = edit.getText().toString();

                        dialog.dismiss();
                        filename = text;
                        Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_SHORT).show();
                        //caaling savefile function
                        saveFile(v);
                    }
                });

                Button btn_cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {


                        dialog.dismiss();

                    }
                });

                dialog.show();

                // saveFile(view);

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

    private void saveFile(View view) {

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file1 = new File(filepath, CODE_SAVING_FOLDER);

        if (!file1.exists()) {
            file1.mkdirs();
        }


        String path = file1.getAbsolutePath() + "/" + filename;
        File file = new File(path);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);

            /** Saving the contents to the file*/
            writer.write(eT.getText().toString());

            /** Closing the writer object */
            writer.close();

            /** Getting sharedpreferences object to store the path of last saved file */
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

            /** Setting the last saved file's path in the shared preference */
            editor.putString("fpath", file.getPath());

            /** Save the path to the shared preference */
            editor.commit();


            Snackbar.make(view, "Successfully saved at " + path, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            Context con = getApplication();
//            dialogBuilder
//                    .withTitle("Wanna Exit ?")                                  //.withTitle(null)  no title
//                    .withTitleColor("#FFFFFF")                                  //def
//                    .withDividerColor("#11000000")                              //def
//                    .withMessage("Make sure you save the file before exiting")                     //.withMessage(null)  no Msg
//                    .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
//                    .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
//                    .withIcon(getResources().getDrawable(R.drawable.c))
//                    .withDuration(400)                                          //def
//                    .withEffect(Effectstype.Fliph)                                         //def Effectstype.Slidetop
//                    .withButton1Text("OK")                                      //def gone
//                    .withButton2Text("EXIT")                                  //def gone
//                    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
//                    .setCustomView(R.layout.exit_dialog, con)         //.setCustomView(View or ResId,context)
//                    .setButton1Click(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
//
//                            dialogBuilder.onBackPressed();
//                        }
//                    })
//                    .setButton2Click(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // Toast.makeText(v.getContext(),"i'm btn2",Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(LaunchActivity.this, FirstActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    })
//                    .show();
            //  super.onBackPressed();
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


    //communication with server
    public class JsonAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress activity_dialog
            pDialog = new ProgressDialog(LaunchActivity.this);
            pDialog.setMessage("Compiling Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.enggproject.in/compiler/process.php");


            //This is the data to send
            String val1 = "11"; //any data to send
            if (sL.equals("C")) {
                val1 = "11";
            } else if (sL.equals("C++")) {
                val1 = "1";
            } else if (sL.equals("C#")) {
                val1 = "27";
            } else if (sL.equals("PHP")) {
                val1 = "29";
            } else if (sL.equals("PHY")) {
                val1 = "4";
            } else if (sL.equals("JAVA")) {
                val1 = "10";
            } else if (sL.equals("SQL")) {
                val1 = "40";
            } else if (sL.equals("JavaScript")) {
                val1 = "35";
            }

            String sourceC = s;

            try {
// Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("lang", val1));
                nameValuePairs.add(new BasicNameValuePair("source", sourceC));
                nameValuePairs.add(new BasicNameValuePair("process", "1"));
                if (!in_put.equals("")) {
                    nameValuePairs.add(new BasicNameValuePair("input", in_put));
                }


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpclient.execute(httppost, responseHandler);

                //This is the response from a php application


                Log.d("Response: ", "> " + response);

                return response;


            } catch (ClientProtocolException e) {
                e.printStackTrace();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                e.printStackTrace();
// TODO Auto-generated catch block
            }
            return "No Data";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress activity_dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            String output = null;
            String error = null;
            if (result != null) {
                try {
                    JSONObject resultObject = new JSONObject(result);
                    if (resultObject.has("output")) {
                        output = resultObject.getString("output");
                    }
                    if (resultObject.has("raw")) {
                        JSONObject rawObject = resultObject.getJSONObject("raw");
                        error = "ERROR:\n" + rawObject.getString("cmpinfo");
                        error = error + "\n" + rawObject.getString("stderr");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            op.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(output)) {
                tv.setText(output);
                tv.setTextColor(getResources().getColor(R.color.bg_login));
            } else {
                tv.setText(error);
                tv.setTextColor(getResources().getColor(R.color.red));

            }


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
