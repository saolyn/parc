package ch.usi.inf.mc.yapt.parc.activities;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import ch.usi.inf.mc.yapt.parc.util.UdpSender;

/** Background activity to send a URI over UDP. */
public class SendToUriActivity extends Activity {
    private static final String TAG = "SendToUriActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final Uri uri = intent.getData();
        Log.d(TAG, "Intent received " + uri.toString());

        UdpSender udpSender = new UdpSender();
        udpSender.send(this.getApplicationContext(), uri);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
