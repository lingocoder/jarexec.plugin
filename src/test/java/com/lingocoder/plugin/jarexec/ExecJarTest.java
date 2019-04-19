package com.lingocoder.plugin.jarexec;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExecJarTest {

    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        Task task = project.getTasks().create("execjar", ExecJar.class);
        assertTrue(task instanceof ExecJar);
    }
}
