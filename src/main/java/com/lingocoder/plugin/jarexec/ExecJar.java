package com.lingocoder.plugin.jarexec;

import com.lingocoder.process.io.ProcessIO;
import com.lingocoder.jar.JarTool;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
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

    public ExecJar() {

        this.args = getProject().getObjects().listProperty(String.class);
        this.classpath = getProject().getObjects().property(FileCollection.class);
        this.jar = getProject().getObjects().property(File.class);
        this.mainClass = getProject().getObjects().property(String.class);
        this.watchInFile = getProject().getObjects().property(File.class);
        this.watchInFiles = getProject().getObjects().property(FileTree.class);
        this.watchOutDir = getProject().getObjects().property(File.class);
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

        List<String> argsList = new ArrayList<>(this.getArgs().size() + 1); argsList.add(0,this.getJar().getAbsolutePath()); argsList.addAll(this.getArgs());

        ProcessIO io = new JarTool().execute(argsList.toArray(new String[argsList.size()]));

        System.out.printf("%s%n", io);
    }

}
