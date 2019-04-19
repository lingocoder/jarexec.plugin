package com.lingocoder.plugin.jarexec


import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.plugins.ExtensionAware

class JarExecPluginIntegrationTest extends AbstractIntegrationTest {

    private static final String RT_CLASSPATH = "runtimeClasspath"
    private static final String RT_DEP = "org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies"

    Project project

        def setup() {
            project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        }

        def "Creates the execjar task when the jarexec plugin is applied"() {
            when:
                applyJarexecPlugin(project)
            then:
                taskExists()
        }

        def "When the execjar task is applied, the task has the expected properties"() {
            when:
                applyJarexecPlugin(project)
                assignDefaultJar()

            then:
                hasExpectedProperties()
        }

        def "JarHelper can determine that a non-executable jar is non-executable"() {
            when:
                applyJarexecPlugin(project)
                assignDefaultJar()                 

            then:
                !project.jarhelper.checkExecutable(project.jarexec.jar)
        }

        def "JarHelper can find a resolved executable dependency in the runtimeClasspath"() {
            when:
                applyJarexecPlugin(project)
                assignDefaultJar()
                addDepToConfig(RT_DEP, RT_CLASSPATH)

            then:
                def rtDpndnc = project.jarhelper.fetch(RT_DEP).orElse(null)
                rtDpndnc != null                
                rtDpndnc.exists()
                project.jarhelper.checkExecutable(rtDpndnc)
        }
}
