package com.vucode.androidbenchmark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Benchmark extends AppCompatActivity {

    private TextView result;
    private Button compute;
    private String textHash;
    private String HashValue;
    private String MD5Value;
    private Long totalTimeSHA;
    private Long totalTimeMD5;
    private Integer jobsDone;

    private Integer numOfTimesToCompute = 20000;
    private Integer numOfJobs = 2;

    // Handler for SHA-1
    Handler handlerSHA = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            jobsDone++;
            totalTimeSHA = (Long) msg.obj;
            if (jobsDone == numOfJobs) {
                printResults();
            }
        }
    };

    // Handler for MD5
    Handler handlerMD5 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            jobsDone++;
            totalTimeMD5 = (Long) msg.obj;
            if (jobsDone == numOfJobs) {
                printResults();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

        compute = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);
        textHash = getResources().getString(R.string.textHash);
    }


    public void printResults() {
        String testAgain = getResources().getString(R.string.testAgain);
        compute.setText(testAgain);
        Long average = (totalTimeSHA + totalTimeMD5) / numOfJobs;
        // Divide average time by 100,000,000 to get a reasonable number
        Integer score = Math.round(average / 100000000);
        result.setText("SHA-1 Time Taken: " + totalTimeSHA.toString() + " ns" +
                "\nMD5 Time Taken: " + totalTimeMD5.toString() + " ns" +
                "\n \nScore: " + score.toString());
    }

    // Function call Begin Test button click
    public void onBeginClick(View view) {
        jobsDone = 0;
        computeSHAHash(textHash);
        computeMD5Hash(textHash);
        compute.setText("CALCULATING");
    }

    //
    // Encode a string of text using SHA-1
    //

    // Compute SHA-1 Hash
    public void computeSHAHash(final String text) {
        Runnable r = new Runnable() {
            public void run() {
                Long tsLong = System.nanoTime();
                for (Integer i = 0; i < numOfTimesToCompute; i++) {
                    MessageDigest mdSha1 = null;
                    try {
                        mdSha1 = MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException e1) {
                        Log.e("Benchmark", "Error initializing SHA-1");
                    }
                    try {
                        mdSha1.update(text.getBytes("ASCII"));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    byte[] data = mdSha1.digest();
                    StringBuffer sb = new StringBuffer();
                    String hex = null;
                    hex = Base64.encodeToString(data, 0, data.length, 0);

                    sb.append(hex);
                    HashValue = sb.toString();
                }

                Long ttLong3 = System.nanoTime() - tsLong;
                Message msg = Message.obtain();
                msg.obj = ttLong3;
                msg.setTarget(handlerSHA);
                msg.sendToTarget();

            }

        };

        Thread newThread = new Thread(r);
        newThread.start();
    }

    //
    // Encode a string of text using MD5
    //

    // Compute MD5 Hash
    public void computeMD5Hash(final String text) {
        Runnable r = new Runnable() {
            public void run() {
                Long tsLong = System.nanoTime();
                for (Integer s = 0; s < numOfTimesToCompute; s++) {
                    try {
                        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                        digest.update(text.getBytes());
                        byte messageDigest[] = digest.digest();

                        StringBuffer MD5Hash = new StringBuffer();
                        for (int i = 0; i < messageDigest.length; i++) {
                            String h = Integer.toHexString(0xFF & messageDigest[i]);
                            while (h.length() < 2)
                                h = "0" + h;
                            MD5Hash.append(h);
                        }

                        MD5Value = MD5Hash.toString();

                    } catch (NoSuchAlgorithmException e) {
                        Log.e("Benchmark", "Error initializing MD5");
                    }
                }
                Long ttLong2 = System.nanoTime() - tsLong;
                Message msg = Message.obtain();
                msg.obj = ttLong2;
                msg.setTarget(handlerMD5);
                msg.sendToTarget();

            }
        };

        Thread newThread = new Thread(r);
        newThread.start();
    }

}