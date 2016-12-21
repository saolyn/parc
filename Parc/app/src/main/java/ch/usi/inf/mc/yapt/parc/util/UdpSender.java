package ch.usi.inf.mc.yapt.parc.util;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.*;

public class UdpSender {
    private static final String TAG = "UdpSender";

    private final Handler mToastHandler = new Handler();

    /**
     * Send a given URI over UDP
     * @param context The activity context
     * @param uri The URI to send
     */
    public void send(final Context context, final Uri uri) {

        if (uri == null) {
            return;
        }

        String msg = Uri.decode(uri.getLastPathSegment());
        if(msg == null) {
            return;
        }

        byte[] msgBytes = msg.getBytes();
        final byte[] buf = msgBytes;

        Log.d(TAG, "send: " + new String(msgBytes));

        new Thread(new Runnable() {
            public void run() {
                try {
                    final InetAddress addr = InetAddress.getByName(uri.getHost());
                    final DatagramSocket socket = new DatagramSocket();
                    if (!socket.getBroadcast()) {
                        socket.setBroadcast(true);
                    }

                    DatagramPacket pkt = new DatagramPacket(buf, buf.length, addr, uri.getPort());

                    socket.send(pkt);
                    socket.close();

                } catch (final IOException e) {
                    mToastHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
