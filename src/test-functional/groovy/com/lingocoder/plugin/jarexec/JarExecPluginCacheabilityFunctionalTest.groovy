package com.lingocoder.plugin.jarexec



import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class JarExecPluginCacheabilityFunctionalTest extends Specification {

    // tag::clean-build-cache[]
    @Rule /*final */TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File inputDir
    File outputDir
    File localBuildCacheDirectory


    def setup() {
        localBuildCacheDirectory = testProjectDir.newFolder('local-cache')
        testProjectDir.newFile('settings.gradle') << """
            buildCache {
                local {
                    directory '${localBuildCacheDirectory.toURI()}'
                }
            }
        """
        buildFile = testProjectDir.newFile('build.gradle')
        
        cacheableIo()

    }
    // end::clean-build-cache[]

    // tag::functional-test-build-cache[]
    def "execjar is loaded from cache"() {
        given:
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
                .withArguments( '--build-cache', 'clean', 'execjar')
                .build()

        then:
        result.task(":execjar").outcome == SUCCESS

        when:
        new File(testProjectDir.root, 'build').deleteDir()
        result = runner()
                .withArguments( '-i', '-Dconsole=verbose', '--build-cache', 'clean', 'execjar')
                .build()

        then:
        result.task(":execjar").outcome == UP_TO_DATE
    }
    // end::functional-test-build-cache[]
    /*
     *  TODO – Test raml-to-jaxrs-cli for „FROM-CACHE“ outcome
     */

    def runner() {
        return GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .forwardOutput()
    }

    def cacheableIo() {
        
        inputDir = testProjectDir.newFolder("raml.in")
        outputDir = testProjectDir.newFolder("jaxrs.out")
        Path apiFile = Paths.get(".").resolve("src/test-functional/resources/types_user_defined.raml")
        Path pojoFile = Paths.get(".").resolve("src/test-functional/resources/ramltopojo.raml")
        Path jaxrsFile = Paths.get(".").resolve("src/test-functional/resources/ramltojaxrs.raml")
        Files.copy(apiFile, inputDir.toPath().resolve(apiFile.getFileName()), REPLACE_EXISTING)
        Files.copy(pojoFile, inputDir.toPath().resolve(pojoFile.getFileName()), REPLACE_EXISTING)
        Files.copy(jaxrsFile, inputDir.toPath().resolve(jaxrsFile.getFileName()), REPLACE_EXISTING)
    }

    def cleanLingo(){
        for(File f : outputDir.listFiles()){
            for(File g : f.listFiles()){
                assert g.delete()
            }
        }
        assert outputDir.listFiles()[0].delete()
        assert outputDir.delete()
    }
}
