package com.lingocoder.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CommandHelperTest {

    private CommandHelper classUnderTest;
    private File aJar = new File("/home/lingocoder/libs/some.jar");
    private List<String> args = Arrays.asList("-s", "Sam", "-m", "Mam");

    @Before
    public void setUp(){ classUnderTest = new CommandHelper(); }

    @Test
    public void testToString() {
        String expected = "one two three";
        String actual = this.classUnderTest.toString("one,two,three".split(","));
        assertEquals(expected, actual);
    }

    @Test
    public void testToStringTrimsSpace() {
        String expected = "one two three";
        String actual = this.classUnderTest.toString("one,  two, three".split(","));
        assertEquals(expected, actual);
    }

    @Test
    public void testToCmdArray1stTokenIsJava() {
        String expected = "java";
        String actual = this.classUnderTest.toCmdArray(aJar, args)[0];
        assertEquals(expected, actual);
    }

    @Test
    public void testToCmdArray2ndTokenIsJar() {
        String expected = "-jar";
        String actual = this.classUnderTest.toCmdArray(aJar, args)[1];
        assertEquals(expected, actual);
    }

    @Test
    public void testToCmdArray3rdTokenEndsWithDotJar() {
        String expected = ".jar";
        String actual = this.classUnderTest.toCmdArray(aJar, args)[2];
        assertTrue(actual.endsWith(expected));
    }
}
