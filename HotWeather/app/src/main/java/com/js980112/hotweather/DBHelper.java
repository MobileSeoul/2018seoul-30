package com.js980112.hotweather;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper{

    private static String DB_PATH = "/data/data/com.js980112.hotweather/databases/";
    private static String DB_NAME ="map.db"; // 데이터베이스 이름
    private SQLiteDatabase mDataBase;

    public DBHelper(Context context) {
        super(context, "map.db",null,1);
        setDB(context);
    }

    public static void setDB(Context ctx) {
        if(android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = ctx.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + ctx.getPackageName() + "/databases/";
        }
        File folder = new File(DB_PATH);
        if(folder.exists()) {
        } else {
            folder.mkdirs();
        }
        AssetManager assetManager = ctx.getResources().getAssets(); //ctx가 없으면 assets폴더를 찾지 못한다.
        File outfile = new File(DB_PATH+DB_NAME);
        InputStream is = null;
        FileOutputStream fo = null;
        long filesize = 0;
        try {
            is = assetManager.open("db/map.db");
            filesize = is.available();
            if (outfile.length() <= 0) {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } else {}
        } catch (IOException e) {
            Log.d("dbHelper",DB_PATH+DB_NAME);
            e.printStackTrace();
        }
    }
    // 이곳에 public으로 쿼리코드 생성

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
