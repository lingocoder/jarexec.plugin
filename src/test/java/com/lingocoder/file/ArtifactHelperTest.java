package com.lingocoder.file;

/*import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.LogLevel;
import org.gradle.internal.logging.slf4j.OutputEventListenerBackedLogger;*/
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;*/

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/*import static org.gradle.api.logging.LogLevel.DEBUG;*/
import static org.junit.Assert.*;

public class ArtifactHelperTest {

    private ArtifactHelper classUnderTest;

    private static final Logger LOGGER = LoggerFactory.getLogger("ArtifactHelperTest");

    @Before
    public void setUp() {/*LOGGER.log(DEBUG, "in setup()");*/
        /* FIXME – repoLocation MUST be configurable */
        try {LOGGER.debug("in setup()"); /*(OutputEventListenerBackedLogger).log(LogLevel.DEBUG, (Throwable)null, "in setup()")*/;
            this.classUnderTest = new ArtifactHelper();
        }
        catch (NonExistentLocationException nele){
            LOGGER.error(nele.getMessage(), nele);
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArtifactHelper1ArgRejectsNullArg(){

        new ArtifactHelper(null);

        fail();
    }

    @Test
    public void testGroupToPath(){

        Optional<Path> expected = classUnderTest.groupToPath("com.google.guava");

        expected.ifPresent(p-> assertTrue(Files.exists(p)));
    }

    @Test
    public void testGroupArtifactToPath(){

        Optional<Path> expected = classUnderTest.groupArtifactToPath("com.google.guava", "guava");

        expected.ifPresent(p-> assertTrue(Files.exists(p)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupArtifactToPathRejectsNullGroup(){

        classUnderTest.groupArtifactToPath(null, "guava");

        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupArtifactToPathRejectsEmptyArtifact(){

        classUnderTest.groupArtifactToPath("foo.bar.baz", "");

        fail();
    }

    @Test
    public void testGroupArtifactVerToPath(){

        Optional<Path> expected = classUnderTest.groupArtifactVerToPath("com.google.guava", "guava", "19.0");

        expected.ifPresent(p-> assertTrue(Files.exists(p)));
    }

    @Test
    public void testGavDiscriminatorToPath(){

        Optional<Path> expected = classUnderTest.gavClassifierToPath("org.raml.jaxrs", "raml-to-jaxrs-cli", "3.0.6-SNAPSHOT", Optional.of("jar-with-dependencies"));

        expected.ifPresent(p -> LOGGER.debug(p.toString()));

        expected.ifPresent(p-> assertTrue(Files.exists(p)));
    }

    @Test
    public void testGavNoDiscriminatorToPath(){

        Optional<Path> expected = classUnderTest.gavClassifierToPath("junit", "junit", "4.12", Optional.empty());

        expected.ifPresent(p -> assertTrue(Files.exists(p)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGavNoDiscriminatorToPathRejectsNullVersion(){

        classUnderTest.gavClassifierToPath("junit", "junit", null, Optional.empty());

        fail();
    }

    @Test
    public void testFromMavenLocal(){System.out.println("M2_REPO = '" + System.getenv("M2_REPO") + "'"); System.out.println("-Dmaven.repo.local = '" + System.getProperty("maven.repo.local"));

        String coordinates = "org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies";

        Optional<File> expected = classUnderTest.fromMavenLocal(coordinates);

        String failed = "Artifact at '" + coordinates + "' does not exist.";

        expected.ifPresent(artifact -> assertTrue(failed,Files.exists(artifact.toPath())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromMavenLocalRejectsNullParam(){
        classUnderTest.fromMavenLocal(null);
    }

    @Test(expected = NonExistentLocationException.class)
    public void testOneArgConstructorWithNonExistentRepo() throws Exception {

        new ArtifactHelper(new URI("file:///$dfao£"));
    }
}
