package com.morrigan.m;

import org.junit.Test;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        long time = 12 * 60 * 60 * 1000;
        System.out.println(time / 360);
        System.out.println(time % 360);
        final Calendar zero = Calendar.getInstance();
        zero.set(Calendar.HOUR_OF_DAY, 12);
        zero.set(Calendar.MINUTE, 0);
        zero.set(Calendar.SECOND, 0);
        zero.set(Calendar.MILLISECOND, 0);
        final Calendar calendar = Calendar.getInstance();
        long t = calendar.getTimeInMillis() - zero.getTimeInMillis();
        System.out.println(t);
        System.out.println(t / 120000);
        long time2 = 12 * 60 * 1000;
        System.out.println(time2);
    }
}