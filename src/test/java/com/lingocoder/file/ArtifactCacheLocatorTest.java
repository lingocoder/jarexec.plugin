package com.lingocoder.file;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class ArtifactCacheLocatorTest {

    private ArtifactCacheLocator classUnderTest;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactCacheLocatorTest.class);

    @Before
    public void setUp(){

        this.classUnderTest = new ArtifactCacheLocator();
    }

    @Test
    public void testLocateMavenLocal(){

        Optional<URI> expected = classUnderTest.locateMavenLocal();

        assertIfFound(expected,"Maven Repository");
    }

    @Test
    public void testLocateGradleJarsCache(){

        Optional<URI> expected = classUnderTest.locateGradleJarsCache();

        assertIfFound(expected,"Gradle's jars-3 cache");

        expected.ifPresent(g ->assertEquals("jars-3",Paths.get(g).toFile().getName()));
    }

    @Test
    public void testLocateGradleModuleCache(){

        Optional<URI> expected = classUnderTest.locateGradleModulesCache();

        assertIfFound(expected,"Gradle's modules-2 cache");

        expected.ifPresent(g ->assertEquals("modules-2",Paths.get(g).toFile().getName()));
    }

    private void assertIfFound(final Optional<URI> expected, final String cache){

        expected.ifPresent(mvn -> assertTrue(Paths.get(mvn).toFile().exists()));

        expected.ifPresent(mvn -> assertTrue(Paths.get(mvn).toFile().isDirectory()));

        expected.ifPresentOrElse(mvn -> LOGGER.debug("Found {} at '{}'", cache, mvn.toString()), ()-> LOGGER.debug("{} could not be found.", cache));
    }
}
