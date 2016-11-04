package com.example.shash.tphttp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ok = (Button) findViewById(R.id.button);
        ok.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnButtonOk();
                    }
                });
            }
        });

        Button web = (Button) findViewById(R.id.button2);
        web.setOnClickListener(new View.OnClickListener() { // whatever happens, we make sure we call the next function on the MAIN thread
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doClickOnButtonWebView();
                    }
                });
            }
        });
    }

    private void doClickOnButtonWebView(){
        View v1 = findViewById(R.id.editText);
        EditText et1 = (EditText) v1;
        String st = et1.getText().toString();
        Uri uri = Uri.parse(st);
        Intent i = new Intent(Intent.ACTION_VIEW,  uri);

        startActivity(i);
    }

    private void doClickOnButtonOk(){
        //creation du thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // verifier que l'url est valide
                try {
                    View v1 = findViewById(R.id.editText);
                    EditText et1 = (EditText) v1;
                    String st = et1.getText().toString();
                    URL u = new URL(st);
                    Log.d("url", st);
                    if (u != null) {
                        // creation du cache
                        File Internal = getFilesDir();
                        final File f = new File(Internal, "cache");
                        long lm = f.lastModified();
                        Date d = new Date();
                        long n = d.getTime();
                        long dif = n - lm;
                        int sem = 604800000;
                        Log.d("dif", ""+dif);
                        if ( f.exists() && ( dif > sem ) ) {
                            f.delete();
                        } else if ( f.exists() && ( dif < sem ) ) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //lecture du fichier cache
                                    try {
                                        FileInputStream is = new FileInputStream(f);
                                        InputStreamReader isr = new InputStreamReader(is);
                                        BufferedReader br = new BufferedReader(isr);
                                        String s = null;
                                        s = br.readLine();
                                        String rs = s;
                                        while (s != null) {
                                            rs = rs + s;
                                            s = br.readLine();
                                        }
                                        Log.e("htmle", rs);
                                        TextView t1 = (TextView) findViewById(R.id.textView);
                                        t1.setText("" + rs);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } else {
                            URLConnection c = u.openConnection();
                            InputStream cis = c.getInputStream();
                            InputStreamReader cisr = new InputStreamReader(cis);
                            BufferedReader cbr = new BufferedReader(cisr);
                            String sc = cbr.readLine();
                            String rsc = sc;
                            while (sc != null) {
                                Log.e("connect", sc);
                                rsc = rsc + sc;
                                sc = cbr.readLine();
                            }

                            // creation du fichier cache
                            f.createNewFile();

                            // ecriture dans le fichier cache
                            FileWriter osw = new FileWriter(f, true);
                            BufferedWriter bw = new BufferedWriter(osw);
                            String sw = rsc;
                            bw.write(sw + "\n \n");
                            Log.e("writer", sw);
                            bw.close();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //lecture du fichier cache
                                    FileInputStream fis = null;
                                    try {
                                        fis = new FileInputStream(f);
                                        InputStreamReader fisr = new InputStreamReader(fis);
                                        BufferedReader fbr = new BufferedReader(fisr);
                                        String fs = fbr.readLine();
                                        String rfs = fs;
                                        while (fs != null) {
                                            rfs = rfs + fs;
                                            fs = fbr.readLine();
                                        }
                                        Log.e("html", rfs);
                                        TextView t1 = (TextView) findViewById(R.id.textView);
                                        t1.setText(""+rfs);

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }
}
