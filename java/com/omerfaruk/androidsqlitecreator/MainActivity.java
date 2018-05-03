package com.omerfaruk.androidsqlitecreator;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Button btnExecute;
    Button btnDeleteDB;
    Button btnDownloadDB;
    Button btnCreateDB;
    ListView lvShow;
    AccesLayer myDB;

     ArrayList<String> theTableContentList;
     Cursor listData;
     int columnNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        myDB = new AccesLayer(MainActivity.this);
        myDB.open();
        btnExecute = (Button) findViewById(R.id.btnExecute);
        btnDeleteDB = (Button) findViewById(R.id.btnDeleteDB);
        btnDownloadDB = (Button) findViewById(R.id.btnDownloadDB);
        btnCreateDB = (Button) findViewById(R.id.btnCreateDB);
        lvShow = (ListView) findViewById(R.id.lvShow);

        File dbPath;
        if((dbPath  = this.getDatabasePath("dbSQLite.db")).exists()){
            Toast.makeText(this,"Database already exists. You can display data.",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Database doesn't exists.It takes about 1 minute to create a new database. Please check debug Logs.",Toast.LENGTH_LONG).show();
        }

        try{
            theTableContentList = new ArrayList<String>();
            listData = myDB.getListContents();
            columnNumber = myDB.getColumnNumber();
        }catch (Exception exception){
            exception.printStackTrace();
        }

        btnCreateDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.myDBCreator(myDB.db);
            }
        });


        btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (listData.getCount() == 0) {
                        Toast.makeText(MainActivity.this, "Table is empty.", Toast.LENGTH_SHORT).show();
                    } else {
                        while (listData.moveToNext()) {
                            int i = 0;
                            StringBuilder sb = new StringBuilder();
                            while (i < columnNumber) {
                                sb.append("\t" + listData.getString(i) + "\t|");
                                i++;
                            }
                            theTableContentList.add(String.valueOf(sb));
                            ListAdapter listAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, theTableContentList);
                            lvShow.setAdapter(listAdapter);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"The Database can be deleted, please try close the application on your device and restart, it will be work!",Toast.LENGTH_LONG).show();
                    lvShow.setAdapter(null);
                }
            }
        });

        btnDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.deleteDB(MainActivity.this);
                Toast.makeText(MainActivity.this,"Delete database button clicked.",Toast.LENGTH_SHORT).show();
                lvShow.setAdapter(null);
            }
        });

        btnDownloadDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.downloadDB(MainActivity.this);
                Toast.makeText(MainActivity.this,"Download button clicked. If you don't delete the database before the clicking this button " +
                        "its will not work properly!",Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    protected void onResume() {
        myDB.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        myDB.close();
        super.onPause();
    }
}
