package com.morrigan.m;

import com.morrigan.m.main.Duration;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DurationTest {

    @Test
    public void textDuration() throws Exception {
        Assert.assertEquals("00:00", generateTimeText(0));
        Assert.assertEquals("00:59", generateTimeText(59));
        Assert.assertEquals("01:01", generateTimeText(61));
        Assert.assertEquals("01:59", generateTimeText(119));
        Assert.assertEquals("59:59", generateTimeText(3599));
        Assert.assertEquals("01:00:00", generateTimeText(3600));
        Assert.assertEquals("01:00:01", generateTimeText(3601));
        Assert.assertEquals("01:01:01", generateTimeText(3661));
        Assert.assertEquals("02:00:01", generateTimeText(7201));
        Assert.assertEquals("20:00:01", generateTimeText(72001));
        Assert.assertEquals("25:00:01", generateTimeText(90001));
    }

    private String generateTimeText(long time) {
        return new Duration().toValue(time);
    }
}