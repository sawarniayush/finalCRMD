package com.example.android.splash.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =1;
    public static final String DATABASE_NAME="reports.db";

    public DBHelper(Context context) {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String TABLE1=" CREATE TABLE IF NOT EXISTS "+ DataContract.Report.TABLE_NAME+" ( " + DataContract.Report.DATA+" TEXT NOT NULL, "+DataContract.Report.LATITUDE+" TEXT ,"+DataContract.Report.LONGITUDE+" TEXT NOT NULL, "+DataContract.Report.IMEI+" TEXT NOT NULL, "+ DataContract.Report.IMAGE+" TEXT NOT NULL, "+DataContract.Report.STATUS+" INTEGER NOT NULL , "+DataContract.Report.TIME+" TEXT NOT NULL)";
        final String TABLE2=" CREATE TABLE IF NOT EXISTS "+ DataContract.Supevisor.TABLE_NAME+" ( " + DataContract.Supevisor.NAME+" TEXT NOT NULL )" ;
        sqLiteDatabase.execSQL(TABLE1);
        sqLiteDatabase.execSQL(TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS "+ DataContract.Report.TABLE_NAME);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS "+ DataContract.Supevisor.TABLE_NAME);
        final String TABLE1=" CREATE TABLE IF NOT EXISTS "+ DataContract.Report.TABLE_NAME+" ( " + DataContract.Report.DATA+" TEXT NOT NULL , "+DataContract.Report.LATITUDE+" TEXT NOT NULL , "+DataContract.Report.LONGITUDE+" TEXT NOT NULL , "+DataContract.Report.IMEI+" TEXT NOT NULL , "+ DataContract.Report.IMAGE+" TEXT , "+DataContract.Report.STATUS+" INTEGER NOT NULL , "+DataContract.Report.TIME+" TEXT NOT NULL )";
        final String TABLE2=" CREATE TABLE IF NOT EXISTS "+ DataContract.Supevisor.TABLE_NAME+" ( " + DataContract.Supevisor.NAME+" TEXT NOT NULL )" ;
        sqLiteDatabase.execSQL(TABLE1);
        sqLiteDatabase.execSQL(TABLE2);
    }


    public boolean insertReport_previous(String report,String latitude,String longitude,String imei,String image,int status,String time){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.Report.DATA, report);
        contentValues.put(DataContract.Report.IMAGE,image);
        contentValues.put(DataContract.Report.LATITUDE,latitude);
        contentValues.put(DataContract.Report.LONGITUDE,longitude);
        contentValues.put(DataContract.Report.IMEI,imei);
        contentValues.put(DataContract.Report.STATUS,status);
        contentValues.put(DataContract.Report.TIME,time);
        db.insert(DataContract.Report.TABLE_NAME, null, contentValues);
        return true;

    }
    public boolean insertSupervisor(String name){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.Supevisor.NAME, name);
        db.insert(DataContract.Supevisor.TABLE_NAME, null, contentValues);
        return true;
    }
    public Cursor getListSuperVisor(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( " select * from "+ DataContract.Supevisor.TABLE_NAME, null );
        return res;
    }
    public Cursor getPendingList()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( " select * from "+ DataContract.Report.TABLE_NAME+" where "+DataContract.Report.STATUS+"=0 ", null );
        return res;
    }
    public void deleteSuperVisorList(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(" DROP TABLE IF EXISTS "+ DataContract.Supevisor.TABLE_NAME);

        final String TABLE2=" CREATE TABLE IF NOT EXISTS "+ DataContract.Supevisor.TABLE_NAME+" ( " + DataContract.Supevisor.NAME+" TEXT NOT NULL )" ;
        db.execSQL(TABLE2);
    }
    public void deletePrevList(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(" DROP TABLE IF EXISTS "+ DataContract.Report.TABLE_NAME);

        final String TABLE1=" CREATE TABLE IF NOT EXISTS "+ DataContract.Report.TABLE_NAME+" ( " + DataContract.Report.DATA+" TEXT NOT NULL , "+DataContract.Report.LATITUDE+" TEXT NOT NULL , "+DataContract.Report.LONGITUDE+" TEXT NOT NULL , "+DataContract.Report.IMEI+" TEXT NOT NULL , "+ DataContract.Report.IMAGE+" TEXT , "+DataContract.Report.STATUS+" INTEGER NOT NULL , "+DataContract.Report.TIME+" TEXT NOT NULL )";
        db.execSQL(TABLE1);
    }

    public Cursor getListReportPrev(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( " select * from report_prev ", null );
        return res;
    }
    public void deleteReport(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ DataContract.Report.TABLE_NAME +" WHERE data in (SELECT data FROM "+ DataContract.Report.TABLE_NAME+" LIMIT 1 OFFSET "+id+" )" );
    }
    public void updateStatus(String msg,String latitude,String longitude,String imei,String capt_image,int status)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String query=" update "+ DataContract.Report.TABLE_NAME+ " set status="+status+" where "+DataContract.Report.DATA+"=\""+msg+"\" and "+ DataContract.Report.LATITUDE+"=\""+latitude
                +"\" and "+DataContract.Report.LONGITUDE+"=\""+longitude+"\" and "+DataContract.Report.IMEI+"=\""+imei+"\" and "+DataContract.Report.IMAGE+"=\""+capt_image+"\"";
        db.execSQL(query);
    }
}