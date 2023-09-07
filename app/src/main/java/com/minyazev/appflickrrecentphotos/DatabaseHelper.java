package com.minyazev.appflickrrecentphotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "image_cache.db";
    private static final int DATABASE_VERSION  = 1;
    private int count = 0;
    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_IMAGE = "image";
    public static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void saveImage(String url, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put (COLUMN_URL, url);
        contentValues.put (COLUMN_IMAGE, image);
        db.insertWithOnConflict(TABLE_IMAGES,null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        count++;
        Log.d(TAG, "saveImage: "+count);
    }

    public byte[] getImage (String url){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, new String[]{COLUMN_IMAGE}, COLUMN_URL+"=?", new String[]{url},null, null, null);
        if(cursor.moveToFirst()){
            byte[] image = cursor.getBlob(0);
            cursor.close();
            return image;
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE "+TABLE_IMAGES+"("+
                COLUMN_URL+ " TEXT PRIMARY KEY,"+
                COLUMN_IMAGE +" BLOB)";
        sqLiteDatabase.execSQL(createTable);
        Log.d(TAG, "onCreate: table in database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_IMAGES);
        Log.d(TAG, "onUpgrade: drop table!");
        onCreate(sqLiteDatabase);
    }
}
