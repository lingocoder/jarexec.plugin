package com.lingocoder.file;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class FSCommandTest {

    private static FSCommand classUnderTest;

    private static final Logger LOGGER = LoggerFactory.getLogger(FSCommandTest.class);

    private static String nameOfMainClass;

    private File nullObjFile = new File("@d\\e#/v@;n;u.l!l");

    private static Path mainClassRootDir;

    @BeforeClass
    static public void setUpOnce() {

        /* FIXME – repoLocation MUST be configurable */

        mainClassRootDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve(FSCommandTest.class.getName());

        nameOfMainClass = "Code";

        /*        pathToMainClass = mainClassRootDir.resolve(nameOfMainClass +".class");*/

    }

    @Before
    public void setUp() throws Exception {

        assertTrue(mainClassRootDir.toFile().mkdir());
        assertTrue(mainClassRootDir.toFile().exists());
        assertTrue(mainClassRootDir.toFile().isDirectory());

    }
    @After
    public void tearDown(){

/*
        for( File child : mainClassRootDir.toFile().listFiles()){
            assertFalse(child.isDirectory());
            assertTrue(child.delete());
        }
*/

/*        assertTrue(mainClassRootDir.toFile().listFiles().length == 0);*/
        assertTrue(mainClassRootDir.toFile().delete());

    }

    @Test
    public void testResolveToPathCorrectlyResolvesDottedPkg(){

        Path expected = Paths.get(mainClassRootDir.toUri()).resolve("pkg").resolve("of").resolve("main").resolve("class").resolve(nameOfMainClass);

        LOGGER.debug(expected.toString());

        Path actual = classUnderTest.resolveToPath(mainClassRootDir, Optional.of("pkg.of.main.class"), Optional.ofNullable(nameOfMainClass));

        assertEquals(expected,actual);

    }

    @Test
    public void testResolveToPathCorrectlyResolvesDefaultPkg(){

        Path expected = Paths.get(mainClassRootDir.toUri()).resolve(nameOfMainClass);

        LOGGER.debug(expected.toString());

        Path actual = classUnderTest.resolveToPath(mainClassRootDir, Optional.empty(), Optional.ofNullable(nameOfMainClass));

        assertEquals(expected,actual);

    }

    @Test(expected = NonExistentLocationException.class)
    public void testResolveToPathRejectsNonExistentRootDir(){

        classUnderTest.resolveToPath(this.nullObjFile.toPath(), Optional.empty(), Optional.ofNullable(nameOfMainClass));

    }

}
