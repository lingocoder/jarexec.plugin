package com.lingocoder.plugin.jarexec

import com.lingocoder.jar.JarHelper
import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING


import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import com.nostacktrace.norman.richards.GenClass2

class JarExecPluginFunctionalTest extends Specification {

    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    File settingsFile
    File buildFile
    File mainClassDest
    File inputDir
    File outputDir
    File localBuildCacheDirectory

    def setup() {
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
        mainClassDest = testProjectDir.newFolder("com.nostacktrace.norman.richards")
        GenClass2.main(mainClassDest.getAbsolutePath())
        new JarHelper().createExecutable(mainClassDest.toPath(), Optional.empty(), "Code")
    }

    def teardown(){
        mainClassDest.deleteOnExit()
    }

    def "execjar task executes a confirmed-executable jar"() {
        given:
        settingsFile << "rootProject.name = 'jarexec'"
        buildFile << """
        plugins {
            id 'java'
            id 'com.lingocoder.jarexec'
        }

        assert project.jarexec != null
        assert project.extensions.jarhelper != null
        def testJar = file("./com.nostacktrace.norman.richards/lingocoder.0.jar")
        assert project.extensions.jarhelper.checkExecutable(testJar)

        jarexec{

          args = ["\\"Yeah! No. It really is executable!\\""]

          jar = testJar
        }        
    """

        when:
        def result = runner()
                .withArguments('execjar')
                .build()

        then:
        result.output.contains('Yeah! No. It really is executable!')
        result.task(":execjar").outcome == SUCCESS
    }

    def "execjar task should not break when given a non-executable jar"() {
        given:
        settingsFile << "rootProject.name = 'jarexec'"
        buildFile << """
        plugins {
            id 'java'
            id 'com.lingocoder.jarexec'
        }

        assert project.jarexec != null
        assert project.extensions.jarhelper != null
        def testJar = jarhelper.fetch("junit:junit:4.12").orElse(file("./com.nostacktrace.norman.richards/lingocoder.0.jar"))
        assert !project.extensions.jarhelper.checkExecutable(testJar)

        jarexec{

          args = ["\\"It is not executable. But that's OK!\\""]

          jar = testJar
        }        
    """

        when:
        def result = runner()
                .withArguments('execjar')
                .build()

        then:
        result.output.contains('no main manifest attribute')
        result.task(":execjar").outcome == SUCCESS
    }

    def "execjar task is cacheable"() {
        given:

        cacheableIo()

        buildFile << """
        plugins {
            id 'java'
            id 'com.lingocoder.jarexec'
        }

        repositories{
          mavenLocal()
          mavenCentral()
          jcenter()
        }

        dependencies {
         runtimeOnly group : 'org.raml.jaxrs', name: 'raml-to-jaxrs-cli', classifier: 'jar-with-dependencies', version : '3.0.5'
        }

        assert project.jarexec != null
        assert project.extensions.jarhelper != null
        def testJar = jarhelper.fetch("org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies").orElse(file("./com.nostacktrace.norman.richards/lingocoder.0.jar"))
        assert project.extensions.jarhelper.checkExecutable(testJar)

        jarexec{
        
          def raml = file("./raml.in/types_user_defined.raml")

          def jaxrs = file("./jaxrs.out")

          args = ["-r", "lingocoder", "-d", jaxrs.absolutePath, raml.absolutePath]

          jar = testJar

          watchInFile = raml

          watchOutDir = jaxrs        
        }        
    """

        when:
        def result = runner()
                .withArguments('execjar')
                .build()

        then:
        result.output.contains('ok=true')
        assert outputDir.listFiles().length > 0
        result.task(":execjar").outcome == SUCCESS
    }

    def cacheableIo() {

        localBuildCacheDirectory = testProjectDir.newFolder('local-cache')

        inputDir = testProjectDir.newFolder("raml.in")
        outputDir = testProjectDir.newFolder("jaxrs.out")
        Path apiFile = Paths.get(".").resolve("src/test-functional/resources/types_user_defined.raml")
        Path pojoFile = Paths.get(".").resolve("src/test-functional/resources/ramltopojo.raml")
        Path jaxrsFile = Paths.get(".").resolve("src/test-functional/resources/ramltojaxrs.raml")
        Files.copy(apiFile, inputDir.toPath().resolve(apiFile.getFileName()), REPLACE_EXISTING)
        Files.copy(pojoFile, inputDir.toPath().resolve(pojoFile.getFileName()), REPLACE_EXISTING)
        Files.copy(jaxrsFile, inputDir.toPath().resolve(jaxrsFile.getFileName()), REPLACE_EXISTING)
    }

    def GradleRunner runner() {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withGradleInstallation(new File(System.getenv("GRADLE_HOME")))
                .withPluginClasspath()
                .forwardOutput()
    }

}
