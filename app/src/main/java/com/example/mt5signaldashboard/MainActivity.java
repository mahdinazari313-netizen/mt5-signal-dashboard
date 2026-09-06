package com.example.mt5signaldashboard;

import android.content.*;import android.os.*;import android.provider.Settings;import android.view.*;import android.widget.*;import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.*;import com.example.mt5signaldashboard.core.SignalStateManager;import com.example.mt5signaldashboard.model.Signal;import java.util.*;

public class MainActivity extends AppCompatActivity{
 RecyclerView list; Adapter adapter; BroadcastReceiver receiver;
 @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_main);list=findViewById(R.id.recycler);list.setLayoutManager(new LinearLayoutManager(this));adapter=new Adapter();list.setAdapter(adapter);findViewById(R.id.access).setOnClickListener(v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))); receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){refresh();}};registerReceiver(receiver,new IntentFilter("com.example.mt5signaldashboard.SIGNAL_UPDATED"),Context.RECEIVER_NOT_EXPORTED);refresh();new Handler().postDelayed(new Runnable(){public void run(){refresh();new Handler().postDelayed(this,5000);}},5000);}
 void refresh(){adapter.data=SignalStateManager.get(this).getActive(System.currentTimeMillis());adapter.notifyDataSetChanged();}
 @Override protected void onDestroy(){super.onDestroy();unregisterReceiver(receiver);}
 class Adapter extends RecyclerView.Adapter<VH>{List<Signal> data=new ArrayList<>();public VH onCreateViewHolder(android.view.ViewGroup p,int t){return new VH(getLayoutInflater().inflate(R.layout.item_signal,p,false));}public void onBindViewHolder(VH h,int p){Signal s=data.get(p);h.title.setText(s.symbol+"  "+(s.direction.equals("BUY")?"🟢":"🔴")+"  "+s.timeframe.name());h.detail.setText("Price: "+s.price+"    Remaining: "+Math.max(0,(s.expirationTime-System.currentTimeMillis())/60000)+" min");}public int getItemCount(){return data.size();}}
 static class VH extends RecyclerView.ViewHolder{TextView title,detail;VH(View v){super(v);title=v.findViewById(R.id.title);detail=v.findViewById(R.id.detail);}}
}
