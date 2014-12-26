package com.android.jota.eurofootball.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EurofootballSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static EurofootballSyncAdapter sEurofootballSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("EurofootballSyncService", "onCreate - EurofootballSyncService");
        synchronized (sSyncAdapterLock) {
            if (sEurofootballSyncAdapter == null) {
                sEurofootballSyncAdapter = new EurofootballSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sEurofootballSyncAdapter.getSyncAdapterBinder();
    }
}