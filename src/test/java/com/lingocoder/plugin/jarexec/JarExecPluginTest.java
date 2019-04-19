package com.lingocoder.plugin.jarexec;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.Test;

import static org.junit.Assert.*;


// tag::test-plugin[]
public class JarExecPluginTest {

    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.lingocoder.jarexec");

        assertTrue(project.getTasks().getByPath(":execjar") instanceof ExecJar);
    }
}
// end::test-plugin[]