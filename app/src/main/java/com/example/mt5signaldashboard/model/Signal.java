package com.example.mt5signaldashboard.model;

public class Signal {
 public final String symbol,direction; public final Timeframe timeframe; public final double price; public final long receivedTime; public long expirationTime;
 public Signal(String symbol,Timeframe timeframe,String direction,double price,long receivedTime){this.symbol=symbol;this.timeframe=timeframe;this.direction=direction;this.price=price;this.receivedTime=receivedTime;}
 /** Identity is Symbol + Timeframe. Direction never creates a second slot. */
 public String getKey(){return symbol.toUpperCase()+"_"+timeframe.name();}
 public boolean isValid(long now){return expirationTime>now;}
}
