package com.example.mt5signaldashboard.notification;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.example.mt5signaldashboard.core.SignalStateManager;
import com.example.mt5signaldashboard.model.Signal;
import com.example.mt5signaldashboard.parser.Mt5SignalParser;

public class Mt5NotificationListener extends NotificationListenerService {
 @Override public void onNotificationPosted(StatusBarNotification sbn){
  if(sbn==null||sbn.getNotification()==null)return; Notification n=sbn.getNotification();
  CharSequence t=n.extras==null?null:n.extras.getCharSequence(Notification.EXTRA_TEXT); if(t==null||TextUtils.isEmpty(t))return;
  Signal s=Mt5SignalParser.parse(t.toString(),System.currentTimeMillis()); if(s==null)return;
  // Default validity: 10 candles. Same Symbol+Timeframe replaces the previous signal.
  long validity=s.timeframe.getBaseMinutes()*60_000L*10L;
  SignalStateManager.get(this).updateSignal(s,validity);
  sendBroadcast(new android.content.Intent("com.example.mt5signaldashboard.SIGNAL_UPDATED"));
 }
}
