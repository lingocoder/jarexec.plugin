package com.lingocoder.file;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;

public class MavenLocalFileFinderTest {

    private CachedArtifactFinder classUnderTest;

    private String coordinates;

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenLocalFileFinderTest.class);

    @Before
    public void setUp() throws Exception {
        /* FIXME – repoLocation MUST be configurable */
        this.classUnderTest = new MavenLocalFileFinder();
        coordinates = "org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies";
    }

    @Test
    public void testFindProvidesNonNullResult() {

        RepoResult expectedResult = classUnderTest.find(coordinates);

        assertNotNull(expectedResult);
    }

    @Test
    public void testFindProvidesFolderAndFileThatExist() {

        RepoResult expectedResult = classUnderTest.find(coordinates);

        assertNotNull(expectedResult);

        Optional<File> expectedFolder = expectedResult.getFolder();

        expectedFolder.ifPresent(folder -> assertTrue(folder.exists()));

        Optional<File> expectedFile = expectedResult.getFile();

        expectedFile.ifPresent(file -> assertTrue(file.exists()));

        expectedFile.ifPresentOrElse(file -> LOGGER.debug("Found {}", file.getAbsolutePath()), ()-> LOGGER.debug("No file found at coordinates '{}' in the Maven repository configured for the current context.", coordinates));
    }

    /* TODO – Test for/allow empty version coordinate */
}
