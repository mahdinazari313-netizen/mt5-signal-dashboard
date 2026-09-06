package com.example.mt5signaldashboard.parser;

import com.example.mt5signaldashboard.model.*;
import java.util.regex.*;

public final class Mt5SignalParser {
 private static final Pattern P=Pattern.compile("\\(([^,()]+),\\s*([^()]+)\\)\\s+(BUY|SELL)\\s+Signal-\\([^)]*-([0-9]+(?:\\.[0-9]+)?)\\)-\\(",Pattern.CASE_INSENSITIVE);
 public static Signal parse(String text,long time){if(text==null)return null;Matcher m=P.matcher(text);if(!m.find())return null;Timeframe tf=Timeframe.fromString(m.group(2));if(tf==null)return null;try{return new Signal(m.group(1).trim(),tf,m.group(3).toUpperCase(),Double.parseDouble(m.group(4)),time);}catch(Exception e){return null;}}
}
