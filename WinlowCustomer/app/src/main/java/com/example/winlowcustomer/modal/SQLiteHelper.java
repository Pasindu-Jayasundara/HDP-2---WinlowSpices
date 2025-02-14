package com.example.winlowcustomer.modal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.winlowcustomer.modal.callback.GetDataCallback;
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
                "    email  TEXT,\n" +
                "    profile_image  TEXT\n" +
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

        db.execSQL("CREATE TABLE IF NOT EXISTS recently_viewed_product (\n" +
                "    id             INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name           TEXT,\n" +
                "    stock          TEXT,\n" +
                "    doc_id         TEXT,\n" +
                "    image_path     TEXT,\n" +
                "    discount       NUMERIC\n" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS address");
        db.execSQL("DROP TABLE IF EXISTS order_history");
        db.execSQL("DROP TABLE IF EXISTS payment_card");
        db.execSQL("DROP TABLE IF EXISTS recently_viewed_product");

        onCreate(db);

    }

    public void insertSingleUser(SQLiteHelper sqLiteHelper, String id, String name, String mobile, String email,String profile_image) {
        insertSingleUser(sqLiteHelper, null, id, name, mobile, email,profile_image);
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

    public void insertSingleUser(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String id, String name, String mobile, String email, String profile_image){

        if(id == null || name == null || mobile == null || email == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("id",id);
                contentValues.put("name",name);
                contentValues.put("mobile",mobile);
                contentValues.put("email",email);
                contentValues.put("profile_image",profile_image);

                long insertedId = db.insertWithOnConflict("user", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
//                db.close();

                if(singleInsertCallback !=null){
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();

    }

    public void insertSingleAddress(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String address){

        if(address == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("address",address);

                long insertedId = db.insert("address", null, contentValues);
//                db.close();

                if(singleInsertCallback != null){
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();

    }

    public void insertSingleOrderHistory(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String referencePath){

        if(referencePath == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("reference_path",referencePath);

                long insertedId = db.insert("order_history", null, contentValues);
//                db.close();

                if(singleInsertCallback != null) {
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();
    }

    public void insertSinglePaymentCard(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String doe, String number, String cvv){

        if(doe == null || number == null || cvv == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("doe",doe);
                contentValues.put("number",number);
                contentValues.put("cvv",cvv);

                long insertedId = db.insert("payment_card", null, contentValues);
//                db.close();

                if(singleInsertCallback != null) {
                    singleInsertCallback.onUserInserted(insertedId);
                }

            }
        }).start();
    }

    public void insertMultipleOrderHistory(SQLiteHelper sqLiteHelper, String[] referencePaths){

        if(referencePaths == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                for(String referencePath : referencePaths){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("reference_path",referencePath);

                    db.insert("order_history", null, contentValues);
                }

//                db.close();

            }
        }).start();

    }

    public void insertSingleRecentlyViewedProduct(SQLiteHelper sqLiteHelper, String name, String stock, String docId, String discount,String imagePath) {
        insertSingleRecentlyViewedProduct(sqLiteHelper, null, name, stock, docId, discount,imagePath);
    }

    public void insertSingleRecentlyViewedProduct(SQLiteHelper sqLiteHelper, SingleInsertCallback singleInsertCallback, String name, String stock, String docId, String discount,String imagePath){
        
        if(name == null || stock == null || docId == null || discount == null || imagePath == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                
                ContentValues contentValues = new ContentValues();
                contentValues.put("name",name);
                contentValues.put("stock",stock);
                contentValues.put("doc_id",docId);
                contentValues.put("discount",discount);
                contentValues.put("image_path",imagePath);

                long inserted = db.insert("recently_viewed_product", null, contentValues);
//                db.close();
                
                if(singleInsertCallback != null){
                    singleInsertCallback.onUserInserted(inserted);
                }

            }
        }).start();
    }
    
    public void getUser(SQLiteHelper sqLiteHelper, GetDataCallback getDataCallback){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

                String[] projection = {"id","name","mobile","email"};
                String limit = "1";

                Cursor cursor = db.query(
                        "user",
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        limit
                );

                if(getDataCallback !=null){
                    getDataCallback.onGetData(cursor);
                }

//                cursor.close();
//                db.close();

            }
        }).start();

    }

    public void getAddress(SQLiteHelper sqLiteHelper, GetDataCallback getDataCallback){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

                String[] projection = {"address"};
                Cursor cursor = db.query(
                        "address",
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if(getDataCallback != null){
                    getDataCallback.onGetData(cursor);
                }

//                cursor.close();
//                db.close();
            }
        }).start();

    }

    public void getOrderHistory(SQLiteHelper sqLiteHelper, GetDataCallback getDataCallback){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

                String[] projection = {"reference_path"};
                Cursor cursor = db.query(
                        "order_history",
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if(getDataCallback != null){
                    getDataCallback.onGetData(cursor);
                }

//                cursor.close();
//                db.close();

            }
        }).start();

    }

    public void getPaymentCard(SQLiteHelper sqLiteHelper, GetDataCallback getDataCallback){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

                String[] projection = {"doe","number","cvv"};
                Cursor cursor = db.query(
                        "payment_card",
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if(getDataCallback != null){
                    getDataCallback.onGetData(cursor);
                }

//                cursor.close();
//                db.close();
            }
        }).start();

    }
    
    public void getRecentlyViewedProduct(SQLiteHelper sqLiteHelper, GetDataCallback getDataCallback){
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

                String[] projection = {"name","stock","doc_id","discount","image_path"};
                Cursor cursor = db.query(
                        "recently_viewed_product",
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                
                if(getDataCallback != null){
                    getDataCallback.onGetData(cursor);
                }
                
//                cursor.close();
//                db.close();
                
            }
        }).start();
        
    }

    public void removeUser(SQLiteHelper sqLiteHelper, String docId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                db.delete("user","id=?",new String[]{docId});
//                db.close();

            }
        }).start();

    }
}
