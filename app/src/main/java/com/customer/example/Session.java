package com.customer.example;

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
}
