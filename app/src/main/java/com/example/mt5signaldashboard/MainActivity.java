package com.example.mt5signaldashboard;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.mt5signaldashboard.core.SignalStateManager;
import com.example.mt5signaldashboard.model.Signal;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView list;
    private Adapter adapter;
    private BroadcastReceiver receiver;
    private Handler handler;
    private Runnable refresher;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.recycler);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        list.setAdapter(adapter);

        final SignalStateManager state = SignalStateManager.get(this);
        final EditText validity = findViewById(R.id.validity);
        validity.setText(String.valueOf(state.getValidityCandles()));
        findViewById(R.id.saveValidity).setOnClickListener(v -> {
            try {
                int value = Integer.parseInt(validity.getText().toString().trim());
                state.setValidityCandles(value);
                validity.setText(String.valueOf(state.getValidityCandles()));
                refresh();
                Toast.makeText(this, "Validity saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                validity.setText(String.valueOf(state.getValidityCandles()));
            }
        });

        findViewById(R.id.access).setOnClickListener(v ->
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));

        receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context c, Intent i) { refresh(); }
        };
        registerReceiver(receiver, new IntentFilter("com.example.mt5signaldashboard.SIGNAL_UPDATED"),
                Context.RECEIVER_NOT_EXPORTED);

        refresh();
        handler = new Handler(Looper.getMainLooper());
        refresher = new Runnable() {
            @Override public void run() {
                refresh();
                handler.postDelayed(this, 1000L);
            }
        };
        handler.post(refresher);
    }

    private void refresh() {
        if (adapter == null) return;
        adapter.data = SignalStateManager.get(this).getActive(System.currentTimeMillis());
        adapter.notifyDataSetChanged();
    }

    @Override protected void onDestroy() {
        if (handler != null && refresher != null) handler.removeCallbacks(refresher);
        if (receiver != null) unregisterReceiver(receiver);
        super.onDestroy();
    }

    class Adapter extends RecyclerView.Adapter<VH> {
        List<Signal> data = new ArrayList<>();
        @Override public VH onCreateViewHolder(ViewGroup p, int t) {
            return new VH(getLayoutInflater().inflate(R.layout.item_signal, p, false));
        }
        @Override public void onBindViewHolder(VH h, int p) {
            Signal s = data.get(p);
            String icon = "BUY".equals(s.direction) ? "🟢" : "🔴";
            long remaining = Math.max(0L, (s.expirationTime - System.currentTimeMillis()) / 60000L);
            h.title.setText(s.symbol + "  " + icon + "  " + s.timeframe.name());
            h.detail.setText("Price: " + s.price + "    Remaining: " + remaining + " min");
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, detail;
        VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            detail = v.findViewById(R.id.detail);
        }
    }
}
