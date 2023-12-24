package com.example.outgoingcallblocker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(@NotNull Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private Cursor getAllEntries() {
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME};
        return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<String> getContacts() {
        List<String> contacts = new ArrayList<>();
        Cursor cursor = getAllEntries();

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            contacts.add(name);
        }

        cursor.close();

        return contacts;
    }

    public boolean findContact(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_NAME);
        Cursor cursor = database.rawQuery(query, new String[] {name});

        boolean flag = cursor.moveToFirst();
        cursor.close();

        return flag;
    }

    public void insert (String name) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, name);
        database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public void delete(String name) {
        String whereClause = "name = ?";
        String[] whereArgs = new String[] {name};
        database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }
}
