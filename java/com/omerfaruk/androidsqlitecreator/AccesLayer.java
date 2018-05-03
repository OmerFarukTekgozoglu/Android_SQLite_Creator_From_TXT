package com.omerfaruk.androidsqlitecreator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by asd on 21.3.2018.
 */

public class AccesLayer {
    SQLiteDatabase db;
    DBHelper my_db;
    private double downloadTimeStart,downloadTimeStop,downloadTime;
    private OkHttpClient okHttpClient;
    private Request request;
    private Response response;
    public static final String TAG = "AccesLayer";
    private static final String URL_DOWNLOAD_LINK = "https://planet.thy.com/mediacontent/content.db";
    public AccesLayer(Context context){
        my_db = new DBHelper(context);
        okHttpClient = new OkHttpClient();
        request = new Request.Builder().url(URL_DOWNLOAD_LINK).build();
    }
    public void open(){
        db = my_db.getWritableDatabase();
    }
    public void close(){
        my_db.close();
    }

    public Cursor getListContents(){
        db = my_db.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM seriesinfo",null);
        return cursor;
    }

    public int getColumnNumber(){
        db = my_db.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM seriesinfo",null);
        return cursor.getColumnCount();
    }

    public void deleteDB(Context context){
        db = my_db.getWritableDatabase();
        if(db.isOpen()){
            db.close();
        }
        File dbPath = context.getDatabasePath("dbSQLite.db");
        try {
            context.deleteDatabase("dbSQLite.db");
            dbPath.delete();
        }catch (Exception deleteException){
            deleteException.printStackTrace();
        }
        Log.d(TAG, "AccesLayer dbPath:" + dbPath);
    }

    public void downloadDB(final Context context){
        Thread threadDownload = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadTimeStart = System.currentTimeMillis();
                    response = okHttpClient.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Failed to download file: " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    File downloadedFile = new File(context.getDatabasePath("dbSQLite.db").getPath());
                    BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                    sink.writeAll(response.body().source());
                    sink.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        threadDownload.start();
    }



}
