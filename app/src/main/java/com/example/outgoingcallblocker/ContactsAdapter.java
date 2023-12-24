package com.example.outgoingcallblocker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter {

    private final List<String> contacts = new ArrayList<>();

    public ContactsAdapter() {
    }

    public List<String> getAllContacts() {
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(
//                ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null
//        );
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                @SuppressLint("Range") String contact = cursor.getString(
//                        cursor.getColumnIndex(
//                                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
//                        )
//                );
//                contacts.add(contact);
//            }
//            cursor.close();
//        }
        return contacts;
    }
}
