package com.example.outgoingcallblocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final List<String> contacts = new ArrayList<>();

    private DatabaseAdapter databaseAdapter;
    private final Map<String, Boolean> whiteListMap = new HashMap<>();

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_READ_CONTACTS);
        }

        if (READ_CONTACTS_GRANTED) {
            databaseAdapter = new DatabaseAdapter(this);
            loadContacts();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true;
            }
        }

        if (READ_CONTACTS_GRANTED) {
            databaseAdapter = new DatabaseAdapter(this);
            loadContacts();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String contact = cursor.getString(
                        cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                        )
                );
                contacts.add(contact);
            }
            cursor.close();
        }

        databaseAdapter.open();
        loadWhiteListToMap(databaseAdapter.getContacts());
        databaseAdapter.close();

        TextView selection = findViewById(R.id.selection);
        ListView contactList = findViewById(R.id.contactList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                contacts
        );
        contactList.setAdapter(adapter);


        for (int i = 0; i < contacts.size(); i++) {
             if (Boolean.TRUE.equals(
                     whiteListMap.getOrDefault(contacts.get(i), false))) {
                 contactList.setItemChecked(i, true);
             }
        }


        contactList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        String name = contactList.getItemAtPosition(position).toString();

                        databaseAdapter.open();

                        if (whiteListMap.containsKey(name)) {
                            whiteListMap.remove(name);
                            databaseAdapter.delete(name);
                        } else {
                            whiteListMap.put(name,true);
                            databaseAdapter.insert(name);
                        }

                        databaseAdapter.close();

//                        SparseBooleanArray selected = contactList.getCheckedItemPositions();
//                        printMap(selection);
                    }
                }
        );
    }

    private void printMap(TextView selection) {
        StringBuilder text = new StringBuilder("Выбрано: ");
        for (String s : whiteListMap.keySet()) {
            text.append(s).append(", ");
        }
        selection.setText(text);
    }

    private void loadWhiteListToMap(List<String> whiteList) {
        for (String s : whiteList) {
            whiteListMap.put(s, true);
        }
    }
}