package com.example.mt5signaldashboard.model;

public enum Timeframe {
 M1(1),M2(2),M3(3),M4(4),M5(5),M6(6),M10(10),M12(12),M15(15),M20(20),M30(30),H1(60),H2(120),H3(180),H4(240),H6(360),H8(480),H12(720),D1(1440),W1(10080),MN1(43200);
 private final int minutes; Timeframe(int m){minutes=m;} public int getBaseMinutes(){return minutes;}
 public static Timeframe fromString(String s){if(s==null)return null; try{return valueOf(s.trim().toUpperCase());}catch(Exception e){return null;}}
}
