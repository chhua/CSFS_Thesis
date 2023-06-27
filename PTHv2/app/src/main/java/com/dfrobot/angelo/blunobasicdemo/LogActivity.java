package com.dfrobot.angelo.blunobasicdemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class LogActivity extends Activity {

    public static ArrayAdapter<String> lstadapter;
    public static ListView lstview1 = null;
    public static String logST1[] = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readSQLite();
        setContentView(R.layout.activity_log);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lstview1 = (ListView)findViewById(R.id.listView1);
        lstadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, logST1){
            public View getView(int position, View convertview, ViewGroup parent){
                View view = super.getView(position, convertview, parent);
                return view;
            }
        };
        lstview1.setAdapter(lstadapter);
        lstview1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void readSQLite(){
        try{
            SQLiteOpenHelper DatabaseHelper = new DatabaseHelper(this);
            SQLiteDatabase db = DatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("table_ble_601CH", new String[]{"P0", "P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8"}, null, null, null, null, null);
            //cursor.moveToFirst();
            cursor.moveToLast();
            int a = 0;
            System.out.println("===========> " +cursor.getCount());
            logST1 = new String[cursor.getCount()-1];
            while (cursor.moveToPrevious()){
                String P0 = cursor.getString(0);
                String P1 = cursor.getString(1);
                String P2 = cursor.getString(2);
                String P3 = cursor.getString(3);
                String P4 = cursor.getString(4);
                String P5 = cursor.getString(5);
                String P6 = cursor.getString(6);
                String P7 = cursor.getString(7);
                String P8 = cursor.getString(8);
                logST1[a] =P0 + "           " + String.format("%1$-12s",P1) + String.format("%1$-12s",P2) + String.format("%1$-12s",P3) +String.format("%1$-12s",P4) +String.format("%1$-12s",P5) + String.format("%1$-12s",P6) + String.format("%1$-12s",P7) + String.format("%1$-12s",P8);
                a++;
            }
        }
        catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        System.out.println("================Read SQLite");
    }

}

