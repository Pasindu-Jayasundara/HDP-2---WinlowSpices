package com.example.winlowcustomer.modal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.winlowcustomer.modal.callback.SingleInsertCallback;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS user (\n" +
                "    name   TEXT,\n" +
                "    id     TEXT PRIMARY KEY,\n" +
                "    mobile TEXT,\n" +
                "    email  TEXT\n" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS address (\n" +
                "    address TEXT,\n" +
                "    id      INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS order_history (\n" +
                "    id             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    reference_path TEXT\n" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS payment_card (\n" +
                "    id     INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    doe    TEXT,\n" +
                "    number TEXT,\n" +
                "    cvv    TEXT\n" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS address");
        db.execSQL("DROP TABLE IF EXISTS order_history");
        db.execSQL("DROP TABLE IF EXISTS payment_card");

        onCreate(db);

    }

    public void insertSingleUser(SQLiteHelper sqLiteHelper, String id, String name, String mobile, String email) {
        insertSingleUser(sqLiteHelper, null, id, name, mobile, email);
    }

    public void insertSingleAddress(SQLiteHelper sqLiteHelper, String address){
        insertSingleAddress(sqLiteHelper, null, address);
    }

    public void insertSingleOrderHistory(SQLiteHelper sqLiteHelper, String referencePath){
        insertSingleOrderHistory(sqLiteHelper, null, referencePath);
    }

    public void insertSinglePaymentCard(SQLiteHelper sqLiteHelper, String doe, String number, String cvv){
        insertSinglePaymentCard(sqLiteHelper, null, doe, number, cvv);
    }

    public void insertSingleUser(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String id, String name, String mobile, String email){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("id",id);
                contentValues.put("name",name);
                contentValues.put("mobile",mobile);
                contentValues.put("email",email);

                long insertedId = db.insert("user", null, contentValues);
                db.close();

                if(singleInsertCallback !=null){
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();

    }

    public void insertSingleAddress(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String address){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("address",address);

                long insertedId = db.insert("address", null, contentValues);
                db.close();

                if(singleInsertCallback != null){
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();

    }

    public void insertSingleOrderHistory(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String referencePath){
        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("reference_path",referencePath);

                long insertedId = db.insert("order_history", null, contentValues);
                db.close();

                if(singleInsertCallback != null) {
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();
    }

    public void insertSinglePaymentCard(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String doe, String number, String cvv){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("doe",doe);
                contentValues.put("number",number);
                contentValues.put("cvv",cvv);

                long insertedId = db.insert("payment_card", null, contentValues);
                db.close();

                if(singleInsertCallback != null) {
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();
    }

    public void insertMultipleOrderHistory(SQLiteHelper sqLiteHelper, String[] referencePaths){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                for(String referencePath : referencePaths){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("reference_path",referencePath);

                    db.insert("order_history", null, contentValues);
                }

                db.close();

            }
        }).start();

    }
}
