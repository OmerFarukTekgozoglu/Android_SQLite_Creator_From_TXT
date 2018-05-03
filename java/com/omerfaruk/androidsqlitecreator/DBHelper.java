package com.omerfaruk.androidsqlitecreator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by asd on 21.3.2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dbSQLite.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    private final String URL_LINK = "https://......your link adress";
    private SQLiteDatabase sqLiteDatabase;

    private double timeEnd,timeStart,timeResult;

    private OkHttpClient okHttpClient;
    private Request request;
    private Response response;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(URL_LINK).build();
    }

    public void myDBCreator(final SQLiteDatabase sqLiteDatabase){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timeStart = System.currentTimeMillis();
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    int lineCount = 1;
                    InputStream is = response.body().byteStream();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                    String result = bf.readLine();
                    String line = result;
                    sqLiteDatabase.execSQL(result);
                    Log.d(TAG,(lineCount++) + " " + result);
                    while ((line = bf.readLine()) != null){
                        if (!line.equals("")) {
                            sqLiteDatabase.execSQL(line);
                            Log.d(TAG, line);
                            timeEnd = System.currentTimeMillis();
                        }else{
                            Log.d(TAG, "Skipping line " + lineCount++);
                        }
                    }
                    timeResult = timeEnd - timeStart;
                    Log.d(TAG,"########Gecen Süre = " + timeResult);
                    is.close();
                    bf.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


}
