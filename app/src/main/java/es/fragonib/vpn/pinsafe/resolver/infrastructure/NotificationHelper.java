package es.fragonib.vpn.pinsafe.resolver.infrastructure;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import es.fragonib.vpn.pinsafe.resolver.R;

import static android.app.NotificationManager.IMPORTANCE_HIGH;


/**
 * Helper to lead with notifications
 */
public class NotificationHelper {

    private static final String OTC_CHANNEL_ID = "channel_otc";
    private static final String OTC_CHANNEL_NAME = "OTC";
    private static final int NOTIFY_ID = 1234;

    public void notify(Context context, String msg) {
        configureNotificationManager(context);
        notifyHeadsUp(context, msg);
    }

    private void configureNotificationManager(Context context) {
        NotificationManager mgr = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            mgr.getNotificationChannel(OTC_CHANNEL_ID) == null) {
                mgr.createNotificationChannel(new NotificationChannel(
                        OTC_CHANNEL_ID, OTC_CHANNEL_NAME, IMPORTANCE_HIGH));
        }
    }

    private void notifyHeadsUp(Context context, String msg) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, OTC_CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentTitle(context.getString(R.string.otc_title))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(msg);
        NotificationManagerCompat mgr = NotificationManagerCompat.from(context);
        mgr.notify(NOTIFY_ID, b.build());
    }

}
