package com.example.outgoingcallblocker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UninstallIntentActivity extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }
}
