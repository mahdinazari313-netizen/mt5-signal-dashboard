package com.example.mt5signaldashboard.notification;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.example.mt5signaldashboard.core.SignalStateManager;
import com.example.mt5signaldashboard.model.Signal;
import com.example.mt5signaldashboard.parser.Mt5SignalParser;

/** Receives MT5 notifications and converts only valid signal messages into dashboard state. */
public class Mt5NotificationListener extends NotificationListenerService {
    private static final String MT5_PACKAGE = "net.metaquotes.metatrader5";
    private static final String UPDATED = "com.example.mt5signaldashboard.SIGNAL_UPDATED";

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) return;
        if (!MT5_PACKAGE.equals(sbn.getPackageName())) return;

        Notification n = sbn.getNotification();
        CharSequence text = null;
        if (n.extras != null) {
            text = n.extras.getCharSequence(Notification.EXTRA_TEXT);
            if (TextUtils.isEmpty(text)) text = n.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        if (TextUtils.isEmpty(text)) return;

        Signal signal = Mt5SignalParser.parse(text.toString(), System.currentTimeMillis());
        if (signal == null) return;

        SignalStateManager.get(this).updateSignal(signal);
        sendBroadcast(new android.content.Intent(UPDATED).setPackage(getPackageName()));
    }
}
