package com.example.mt5signaldashboard.core;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mt5signaldashboard.model.Signal;
import com.example.mt5signaldashboard.model.Timeframe;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

/** Single source of truth for active signals. Identity = Symbol + Timeframe. */
public final class SignalStateManager {
    private static final String PREFS = "signal_state";
    private static final String SIGNALS = "signals";
    private static final String VALIDITY = "validity_candles";
    private static final int DEFAULT_VALIDITY_CANDLES = 10;

    private static SignalStateManager instance;
    private final Map<String, Signal> map = new HashMap<>();
    private final SharedPreferences prefs;

    private SignalStateManager(Context c) {
        prefs = c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        load();
    }

    public static synchronized SignalStateManager get(Context c) {
        if (instance == null) instance = new SignalStateManager(c);
        return instance;
    }

    public synchronized int getValidityCandles() {
        return Math.max(1, prefs.getInt(VALIDITY, DEFAULT_VALIDITY_CANDLES));
    }

    public synchronized void setValidityCandles(int candles) {
        int value = Math.max(1, Math.min(1000, candles));
        prefs.edit().putInt(VALIDITY, value).apply();
    }

    public synchronized void updateSignal(Signal s) {
        long validityMillis = s.timeframe.getBaseMinutes() * 60_000L * getValidityCandles();
        s.expirationTime = s.receivedTime + validityMillis;
        map.put(s.getKey(), s);
        save();
    }

    public synchronized List<Signal> getActive(long now) {
        removeExpired(now);
        List<Signal> result = new ArrayList<>(map.values());
        Collections.sort(result, new Comparator<Signal>() {
            @Override public int compare(Signal a, Signal b) {
                int symbol = a.symbol.compareToIgnoreCase(b.symbol);
                if (symbol != 0) return symbol;
                return Integer.compare(a.timeframe.getBaseMinutes(), b.timeframe.getBaseMinutes());
            }
        });
        return result;
    }

    public synchronized void removeExpired(long now) {
        boolean changed = false;
        Iterator<Map.Entry<String, Signal>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            if (!it.next().getValue().isValid(now)) {
                it.remove();
                changed = true;
            }
        }
        if (changed) save();
    }

    private void save() {
        JSONArray array = new JSONArray();
        try {
            for (Signal s : map.values()) {
                JSONObject o = new JSONObject();
                o.put("symbol", s.symbol);
                o.put("direction", s.direction);
                o.put("timeframe", s.timeframe.name());
                o.put("price", s.price);
                o.put("receivedTime", s.receivedTime);
                o.put("expirationTime", s.expirationTime);
                array.put(o);
            }
        } catch (Exception ignored) { }
        prefs.edit().putString(SIGNALS, array.toString()).apply();
    }

    private void load() {
        map.clear();
        String raw = prefs.getString(SIGNALS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                Timeframe tf = Timeframe.fromString(o.optString("timeframe"));
                if (tf == null) continue;
                Signal s = new Signal(o.optString("symbol"), tf, o.optString("direction"),
                        o.optDouble("price", 0d), o.optLong("receivedTime", 0L));
                s.expirationTime = o.optLong("expirationTime", 0L);
                if (s.isValid(System.currentTimeMillis())) map.put(s.getKey(), s);
            }
        } catch (Exception ignored) {
            map.clear();
        }
    }
}
