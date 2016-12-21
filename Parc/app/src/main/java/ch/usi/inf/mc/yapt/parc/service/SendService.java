package ch.usi.inf.mc.yapt.parc.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ch.usi.inf.mc.yapt.parc.R;
import ch.usi.inf.mc.yapt.parc.util.Gesture;


public class SendService {
    /** The default port over which to send the data */
    public static final String DEFAULT_PORT = "8089";

    private final Context mContext;
    private SharedPreferences mSharedPreferences;

    /**
     * Service to send actions from gestures over UDP
     * @param context The context of the application
     */
    public SendService(Context context) {
        this.mContext = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);;
    }

    /**
     * Send the action associated with a given gesture
     * @param gesture The gesture from which to select the action to be sent
     */
    public void send(Gesture gesture) {
        String payload = mSharedPreferences.getString(
                mContext.getString(gesture.id), mContext.getString(R.string.action_nop));
        send(payload);

    }

    private void send(String payload) {
        if ("NOP".equals(payload)) {
            return;
        }
        final String host = mSharedPreferences.getString("user_ip", "");
        if ("".equals(host)) {
            Toast.makeText(mContext, R.string.no_ip, Toast.LENGTH_LONG).show();
            return;
        }
        final String port = mSharedPreferences.getString("user_port", DEFAULT_PORT);

        String uri = "udp://" + host + ":" + port + "/" + Uri.encode(payload);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));

        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        mContext.startActivity(intent);
    }
}
