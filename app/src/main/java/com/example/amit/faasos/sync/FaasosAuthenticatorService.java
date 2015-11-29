package com.example.amit.faasos.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by amit on 7/23/2015.
 */
public class FaasosAuthenticatorService extends Service{
    // Instance field that stores the authenticator object
    private FaasosAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new FaasosAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
