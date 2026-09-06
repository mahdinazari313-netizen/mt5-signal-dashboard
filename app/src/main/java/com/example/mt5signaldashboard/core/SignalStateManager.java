package com.example.mt5signaldashboard.core;

import android.content.Context;
import com.example.mt5signaldashboard.model.Signal;
import java.util.*;

public final class SignalStateManager {
 private static SignalStateManager instance; private final Map<String,Signal> map=new HashMap<>(); private final Context context;
 private SignalStateManager(Context c){context=c.getApplicationContext();}
 public static synchronized SignalStateManager get(Context c){if(instance==null)instance=new SignalStateManager(c);return instance;}
 public synchronized void updateSignal(Signal s,long validityMillis){s.expirationTime=s.receivedTime+validityMillis;map.put(s.getKey(),s);}
 public synchronized List<Signal> getActive(long now){removeExpired(now);return new ArrayList<>(map.values());}
 public synchronized void removeExpired(long now){Iterator<Map.Entry<String,Signal>> it=map.entrySet().iterator();while(it.hasNext())if(!it.next().getValue().isValid(now))it.remove();}
}
