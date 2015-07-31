package com.customer.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by khsu on 7/30/15.
 */
public class Session {
    private Map<Long, Integer> heartMap = new LinkedHashMap<Long,Integer>();
    public Long totalTime = 0L;
    private Integer minBpm = null;
    public Double totalBeats = 0.0;
    public String show;
    public String title;
    public String user;

    public void addHeartRange(Integer bpm, Long start, Long end) {
        if (minBpm == null) {
            minBpm = bpm;
        } else {
            minBpm = Math.min(minBpm, bpm);
        }
        Long timePassed = end - start;

        //type might make this super inaccurate...
        Double fBeats = bpm.doubleValue() * timePassed.doubleValue()/60000.0;
        totalBeats += fBeats.intValue();
        totalTime += timePassed;
    }

    public Integer intBeats() {
        return totalBeats.intValue();
    }
    public Long heartScore() {
        if (totalBeats == 0) {
            return 0L;
        } else {
            return intBeats()*60000/(totalTime) - minBpm;
        }
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("heart-score", heartScore());
            json.put("watch-time", totalTime);
            json.put("show", show);
            json.put("title", title);
            json.put("user", user);
        } catch (JSONException e) {
            Integer a = 1;
        }
        return json.toString();

    }
}
