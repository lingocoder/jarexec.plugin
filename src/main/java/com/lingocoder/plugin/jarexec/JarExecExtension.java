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

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.io.File;
import java.util.List;

/**
 * Provides properties for configuring the {@link ExecJar} Gradle task;
 */
public class JarExecExtension {


    private final ListProperty<String> args;

    private final Property<FileCollection> classpath;

    private final Property<File> jar;

    private final Property<String> mainClass;

    private final Property<File> watchInFile;

    private final Property<FileTree> watchInFiles;

    private final Property<File> watchOutDir;


    public JarExecExtension(Project project) {

        this.args = project.getObjects().listProperty(String.class);
        this.classpath = project.getObjects().property(FileCollection.class);
        this.jar = project.getObjects().property(File.class);
        this.mainClass = project.getObjects().property(String.class);
        this.watchInFile = project.getObjects().property(File.class);
        this.watchInFiles = project.getObjects().property(FileTree.class);
        this.watchOutDir = project.getObjects().property(File.class);
    }
    public List<String> getArgs() { return args.get(); }

    public ListProperty<String> getArgsProvider() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args.set(args);
    }

    public FileCollection getClasspath() {
        return classpath.get();
    }

    public Property<FileCollection> getClasspathProvider() {
        return classpath;
    }

    public void setClasspath(FileCollection classpath) {
        this.classpath.set(classpath);
    }

    public File getJar() { return jar.get();  }

    public Property<File> getJarProvider() { return jar; }

    public void setJar(File jar) { this.jar.set(jar); }

    public String getMainClass() { return mainClass.get(); }

    public Property<String> getMainClassProvider() { return mainClass; }

    public void setMainClass(String mainClass) { this.mainClass.set(mainClass); }

    public File getWatchInFile() { return watchInFile.get(); }
    
    public Property<File> getWatchInFileProvider() { return watchInFile; }

    public void setWatchInFile(File watchInFile) { this.watchInFile.set(watchInFile); }


    public FileTree getWatchInFiles() { return watchInFiles.get();  }

    public Property<FileTree> getWatchInFilesProvider() { return watchInFiles; }

    public void setWatchInFiles(FileTree watchInFiles) { this.watchInFiles.set(watchInFiles); }

    public File getWatchOutDir() { return watchOutDir.get(); }

    public Property<File> getWatchOutDirProvider() { return watchOutDir; }

    public void setWatchOutDir(File watchOutDir) { this.watchOutDir.set(watchOutDir); }


}
