/**
 * A Gradle plugin that executes Java Jar files.
 *
 * Copyright (C) 2019 lingocoder <plugins@lingocoder.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
