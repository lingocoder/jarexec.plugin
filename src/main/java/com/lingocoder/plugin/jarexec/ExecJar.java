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

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.List;

import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

/**
 * An implementation of Gradle {@link DefaultTask} that provides the functionality for the {@link JarExecPlugin}.
 */
@CacheableTask
public class ExecJar extends DefaultTask {

    private final ListProperty<String> args;

    private final Property<FileCollection> classpath;

    private final Property<File> jar;

    private final Property<String> mainClass;
    
    private final Property<File> watchInFile;

    private final Property<FileTree> watchInFiles;

    private final Property<File> watchOutDir;

    private final Execution exe;

    public ExecJar() {

        this.args = getProject().getObjects().listProperty(String.class);
        this.classpath = getProject().getObjects().property(FileCollection.class);
        this.jar = getProject().getObjects().property(File.class);
        this.mainClass = getProject().getObjects().property(String.class);
        this.watchInFile = getProject().getObjects().property(File.class);
        this.watchInFiles = getProject().getObjects().property(FileTree.class);
        this.watchOutDir = getProject( ).getObjects( ).property( File.class );
        this.exe = new Execution( );
    }

    @Input
    public List<String> getArgs() {
        return args.get();
    }

    public void setArgs(ListProperty<String> args) {
        this.args.set(args);
    }

    @Classpath
    @Optional
    public FileCollection getClasspath() {
        return classpath.getOrNull();
    }

    public void setClasspath(Provider<FileCollection> classpath) {
        this.classpath.set(classpath);
    }

    @InputFile
    @PathSensitive(RELATIVE)
    public File getJar() {
        return jar.get();
    }

    public void setJar(Provider<File> jar) {
        this.jar.set(jar);
    }

    @Input
    @Optional
    public String getMainClass() { return mainClass.getOrElse(""); }

    public void setMainClass(Provider<String> mainClass) {
        this.mainClass.set(mainClass);
    }
    
    @InputFile
    @PathSensitive(RELATIVE)
    @Optional
    public File getWatchInFile() {
        return watchInFile.getOrNull();
    }

    public void setWatchInFile(Provider<File> watchInFile) {
        this.watchInFile.set(watchInFile);
    }

    @InputFiles
    @PathSensitive(RELATIVE)
    @Optional
    public FileTree getWatchInFiles() {
        return watchInFiles.getOrNull();
    }

    public void setWatchInFiles(Provider<FileTree> watchInFiles) {
        this.watchInFiles.set(watchInFiles);
    }

    @OutputDirectory
    @PathSensitive(RELATIVE)
    @Optional
    public File getWatchOutDir(){ return watchOutDir.getOrNull(); }

    public void setWatchOutDir(Provider<File> watchOutDir){ this.watchOutDir.set(watchOutDir);}


    @TaskAction
    void executeJar() {

        this.exe.execute( this.jar, this.classpath, this.args, this.mainClass );
    }
}
