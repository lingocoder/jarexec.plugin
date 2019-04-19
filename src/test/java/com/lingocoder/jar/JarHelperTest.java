package com.lingocoder.jar;

import com.lingocoder.file.MavenLocalFileFinder;
import com.lingocoder.file.CachedArtifactFinder;
import com.lingocoder.file.NonExistentLocationException;
import com.nostacktrace.norman.richards.GenClass2;
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

public class JarHelperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger("JarHelperTest");
/*    private static Path pathToMainClass;*/
    private static String packageOfMainClass;

    private static String nameOfMainClass;

    private JarHelper classUnderTest;

    private static CachedArtifactFinder fileFinder;

    private static String exeCoordinates;

    private static String unExeCoordinates;

    private File nullObjFile = new File("@d\\e#/v@;n;u<l!l");

    private static Path mainClassRootDir;

    
    @BeforeClass
    static public void setUpOnce() {

        /* FIXME – repoLocation MUST be configurable */
        fileFinder = new MavenLocalFileFinder();
        
        exeCoordinates = "org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies";
        
        unExeCoordinates = "junit:junit:4-12";

        mainClassRootDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve(JarHelperTest.class.getName());


        packageOfMainClass = "";

        nameOfMainClass = "Code";

/*        pathToMainClass = mainClassRootDir.resolve(nameOfMainClass +".class");*/

    }
    
    
    @Before
    public void setUp() throws Exception {

        this.classUnderTest = new JarHelper();

        if(mainClassRootDir.toFile().exists())
            mainClassRootDir.toFile().delete();

        assertTrue(mainClassRootDir.toFile().mkdir());
        assertTrue(mainClassRootDir.toFile().exists());
        assertTrue(mainClassRootDir.toFile().isDirectory());

        GenClass2.main(new String[]{mainClassRootDir.toString()});

        LOGGER.debug(System.getProperty("user.dir"));

        LOGGER.debug("Made it out of setUp()");
    }

    @After
    public void tearDown() throws Exception{

        assertNotNull(mainClassRootDir.toFile());
        if(mainClassRootDir.toFile().listFiles() != null)
        for( File child : mainClassRootDir.toFile().listFiles()){
            assertFalse(child.isDirectory());
            assertTrue(child.delete());
        }

        assertTrue(mainClassRootDir.toFile().listFiles() == null
                || mainClassRootDir.toFile().listFiles().length == 0);

        if(mainClassRootDir.toFile().exists())
            mainClassRootDir.toFile().delete();

        LOGGER.debug("Made it out of tearDown()");
    }

    @Test
    public void testCheckExecutable() {

        JarCreationResult jarResult = this.classUnderTest.createExecutable(mainClassRootDir, Optional.ofNullable(packageOfMainClass), nameOfMainClass);

        boolean isExecutable = this.classUnderTest.checkExecutable(jarResult.getPathToExe().orElse(Paths.get(nullObjFile.toURI())).toFile());

        assertTrue(isExecutable);
    }

    @Test
    public void testCheckUnExecutable() {

        boolean isExecutable = this.classUnderTest.checkExecutable(fileFinder.find(unExeCoordinates).getFile().orElse(nullObjFile));

        assertFalse(isExecutable);
    }

    @Test
    public void testCreateExecutableCanLocateInputs() {

        try {

            this.classUnderTest.createExecutable(mainClassRootDir, Optional.ofNullable(packageOfMainClass), nameOfMainClass);

        } catch (NonExistentLocationException nele) {

            LOGGER.error(nele.getMessage(), nele);

            fail();
        }
    }

    @Test
    public void testCreateExecutableCanCreateAnExecutable(){

        JarCreationResult actual;

        try {

            actual = this.classUnderTest.createExecutable(mainClassRootDir, Optional.ofNullable(packageOfMainClass), nameOfMainClass);

            actual.getPathToExe().ifPresent(exe -> assertNotEquals(mainClassRootDir, exe));

            assertEquals(0, actual.getStatus());

            assertTrue(actual.isExecutable());

            actual.getPathToExe().ifPresent( exe -> LOGGER.debug("Path to executable jar: '" + exe.toFile().getAbsolutePath()));

            actual.getPathToExe().ifPresent(exe -> assertTrue(this.classUnderTest.checkExecutable(exe.toFile())));

            actual.getOutPut().ifPresent(msg -> LOGGER.debug(msg));

        } catch(NonExistentLocationException nele){

            fail();
        }
    }
}
