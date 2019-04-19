package com.lingocoder.file;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class GradleLocalFileFinderTest {

    private CachedArtifactFinder interfaceUnderTest;

    private GradleLocalFileFinder classUnderTest;

    private static final Logger LOGGER = LoggerFactory.getLogger(GradleLocalFileFinderTest.class);

    @Before
    public void setUp(){
        this.interfaceUnderTest = new GradleLocalFileFinder();
        this.classUnderTest = (GradleLocalFileFinder)interfaceUnderTest;
    }

    @Test
    public void testFind(){

        RepoResult actual = this.interfaceUnderTest.find("junit:junit:4.12");

        assertNotNull(actual);

        actual.getFile().ifPresentOrElse( junit -> LOGGER.debug("Found junit at '{}'", junit), ()-> LOGGER.debug("JUnit could not be found."));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().contains("junit")));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().contains("4.12")));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().endsWith(".jar")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindRejectsEmptyParam(){
        this.classUnderTest.find(null);
    }

    @Test
    public void testFindFindsModuleInMavenRepo(){

        RepoResult actual = this.interfaceUnderTest.find("org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies");

        assertNotNull(actual);

        actual.getFile().ifPresentOrElse( jaxrs -> LOGGER.debug("Found raml-to-jaxrs-cli at '{}'", jaxrs), ()-> LOGGER.debug("jaxrs-cli could not be found."));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().contains("raml-to-jaxrs-cli")));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().contains("3.0.5")));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().contains("jar-with-dependencies")));

        actual.getFile().ifPresent(file -> assertTrue(file.getName().endsWith(".jar")));
    }

    /* TODO – Test for/allow empty version coordinate */
}
