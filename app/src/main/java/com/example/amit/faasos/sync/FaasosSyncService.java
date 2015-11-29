package com.example.amit.faasos.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by amit on 7/23/2015.
 */
public class FaasosSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FaasosSyncAdapter sFasoosSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sFasoosSyncAdapter == null) {
                sFasoosSyncAdapter = new FaasosSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFasoosSyncAdapter.getSyncAdapterBinder();
    }
}
