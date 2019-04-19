package com.lingocoder.plugin.jarexec;

import com.lingocoder.jar.JarHelper;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * An implementation of Gradle {@link Plugin} that executes a {@code Jar.}
 */
public class JarExecPlugin implements Plugin<Project> {

    public void apply(Project project) {

        project.getExtensions().create("jarhelper", JarHelper.class, project);

        JarExecExtension extension = project.getExtensions().create("jarexec", JarExecExtension.class, project);

        ExecJar task = project.getTasks().create("execjar", ExecJar.class);

        task.setArgs(extension.getArgsProvider());

        task.setClasspath(extension.getClasspathProvider());

        task.setJar(extension.getJarProvider());

        task.setMainClass(extension.getMainClassProvider());
        
        task.setWatchInFile(extension.getWatchInFileProvider());

        task.setWatchInFiles(extension.getWatchInFilesProvider());

        task.setWatchOutDir(extension.getWatchOutDirProvider());
    }
}
