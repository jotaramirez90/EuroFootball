package com.android.jota.eurofootball.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EurofootballAuthenticatorService extends Service {

    private EurofootballAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new EurofootballAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
