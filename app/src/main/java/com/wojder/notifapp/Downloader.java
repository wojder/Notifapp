package com.wojder.notifapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wojder on 15.02.16.
 */
public class Downloader extends IntentService {

    private static final int NOTIFY_ID = 1337;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

    public Downloader() {
        super("Downloader");
    }

    @Override
    public void onHandleIntent(Intent i) {
        try {
            File root =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            root.mkdirs();

            File output = new File(root, i.getData().getLastPathSegment());

            if (output.exists()) {
                output.delete();
            }

            URL url = new URL(i.getData().toString());
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            FileOutputStream fos = new FileOutputStream(output.getPath());
            BufferedOutputStream out = new BufferedOutputStream(fos);

            try {
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[8192];
                int len = 0;

                while ((len = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }

                out.flush();
            } finally {
                fos.getFD().sync();
                out.close();
                c.disconnect();
            }

            launchNotification(i, output, null);
        } catch (IOException e2) {
            launchNotification(i, null, e2);
        }
    }

    private void launchNotification(Intent inbound, File output, Exception e) {
        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        makeNotificationExpandable(mgr);

        builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);

        if (e == null) {

            generalNotificationBuild(this);

            Intent outbound = new Intent(Intent.ACTION_VIEW);

            outbound.setDataAndType(Uri.fromFile(output), inbound.getType());

        } else {
            builder.setContentTitle(getString(R.string.exception))
                    .setContentText(e.getMessage())
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setTicker(getString(R.string.exception));
        }

        mgr.notify(NOTIFY_ID, builder.build());

    }

    private void makeNotificationExpandable(NotificationManager mgr) {
        NotificationCompat.InboxStyle large = new NotificationCompat.InboxStyle(builder);

        mgr.notify(NOTIFY_ID, large.setSummaryText("Test summary")
                .addLine("First line")
                .addLine("Second Line")
                .addLine("4 line")
                .addLine("5th line")
                .addLine("Third line").build());
    }

    private NotificationCompat.Builder generalNotificationBuild(Context ctxt) {

        builder.setAutoCancel(true)
                .setContentTitle(getString(R.string.download_complete))
                .setContentText(getString(R.string.fun))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(buildPendingIntent(Settings.ACTION_SECURITY_SETTINGS))
                .setTicker(getString(R.string.download_complete))
                .addAction(android.R.drawable.ic_media_play,
                        "Play", buildPendingIntent(Settings.ACTION_SETTINGS))
                .addAction(android.R.drawable.sym_action_call,
                        "Call him!", buildPendingIntent(Intent.ACTION_CALL));

        return builder;
    }

    private PendingIntent buildPendingIntent(String go) {
        Intent intent = new Intent(go);

        return (PendingIntent.getActivity(this, 0, intent, 0));
    }
}
