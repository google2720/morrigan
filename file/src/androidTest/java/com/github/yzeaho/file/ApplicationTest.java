package com.github.yzeaho.file;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;

import java.io.IOException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testA() throws IOException {
        FileApi.copy(null, null);
    }
}