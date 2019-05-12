package com.lingocoder.jar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.*;
import java.util.stream.Stream;

import com.lingocoder.file.ArtifactHelper;
import com.lingocoder.file.CachedArtifactFinder;
import com.lingocoder.file.GradleLocalFileFinder;
import com.lingocoder.file.NonExistentLocationException;
import com.lingocoder.process.io.ProcessIO;
import org.gradle.api.Project;

import static com.lingocoder.file.FSCommand.resolveToPath;

/**
 * Provides {@code jar}-related support to {@link com.lingocoder.plugin.jarexec.JarExecPlugin} and its related tests.
 */
public class JarHelper {


    private final CachedArtifactFinder finder;

    private Project project;

    /**
     * Create an instance with default state.
     */
    public JarHelper(){

        this.finder = new GradleLocalFileFinder();
    }

    /**
     * Create an instance that will be used within a task of a Gradle build script.
     * 
     * @param project The Gradle {@link Project} within which this instance will be used.
     */
    public JarHelper(Project project){ this(); this.project = project; }

    /**
     * Determine whether the given file refers to an executable {@code jar}.
     *
     * @param aJar A {@link File} that refers to a {@code jar} file.
     *
     * @return {@code true} if the {@code aJar} param refers to an executable {@code jar}; Otherwise, {@code false}.
     */
    public boolean checkExecutable(File aJar) {

        String fileName = aJar.getName();
        boolean isJar = fileName.endsWith(".jar") || fileName.endsWith(".zip");
        if(!isJar){ return isJar; }
        try (JarFile jarFile = new JarFile(aJar)) {
            isJar = this.hasMainClass(jarFile);
        } catch (IOException e) {
            /* TODO – Log it */
            isJar = false;
        }
        return isJar;
    }

    private boolean hasMainClass(JarFile jarFile){
    
        Manifest manifest = null;
        Attributes.Name mainClass = null;
        Attributes mainAttrs = null;  
        try{
          manifest = jarFile.getManifest(); 
          mainClass = Attributes.Name.MAIN_CLASS;
          mainAttrs = manifest.getMainAttributes();}
        catch(IOException ioe){ /* FIXME – Log it */ }

        return manifest != null && !mainAttrs.isEmpty() && mainAttrs.containsKey(mainClass) && mainAttrs.getValue(mainClass) != null;
    }

    /**
     * Produce an executable {@code jar}.
     *
     * @param mainClassRootDir The location of the {@code Main-Class} intended to be the entry point of the resulting {@code jar}.
     *
     * @param packageOfMainClass An {@link Optional} package the {@code Main-Class} MAY be a member of.
     *
     * @param nameOfMainClass The name of the {@code Main-Class}. The name can be given either with or without the <em>.class</em> extension.
     *
     * @return A composite result that contains — <em>among other things</em> — the location of the executable {@code jar}.
     */
    public JarCreationResult createExecutable(Path mainClassRootDir, Optional<String> packageOfMainClass, String nameOfMainClass) {

        JarCreationResult exeResult;

        nameOfMainClass = nameOfMainClass.endsWith(".class") ? nameOfMainClass : nameOfMainClass + ".class";

        Path pathToMainClass = resolveToPath(mainClassRootDir,packageOfMainClass, Optional.of(nameOfMainClass));

        if(mainClassRootDir.toFile().exists() && pathToMainClass.toFile().exists()) { exeResult = this.doCreateExe(mainClassRootDir, packageOfMainClass, nameOfMainClass); } else{ throw new NonExistentLocationException(mainClassRootDir.toString()); }

        return exeResult;
    }

    private JarCreationResult doCreateExe(Path rootDir, Optional<String> pkg, String simpleName) {

        JarCreationResult exeResult;

        String nameSansExt;

        String root = resolveToPath(rootDir, pkg, Optional.empty()).toString();

        /* FIXME – accept user-defined name for jar file */
        String jarName = "lingocoder.0.jar";

        Path path2exe = resolveToPath(rootDir, pkg, Optional.of(jarName));

        String pre9Cmds[] = {"cfe", path2exe.toString(), nameSansExt = simpleName.replace(".class", ""), "-C", root, nameSansExt+".class"};
        String jdk9Cmds[] = {"--create", "--file", jarName, "--main-class", nameSansExt = simpleName.replace(".class", ""), "-C", root, nameSansExt+".class"};

        exeResult = invokeJar(pre9Cmds, path2exe);

        return exeResult;
    }

    private JarCreationResult invokeJar(String[] cmdArray, Path path2exe) {

        JarCreationResult exeResult;

        JarTool jar = new JarTool();

        ProcessIO jarIO = jar.run(cmdArray);

        if( jarIO.isOk() ){

            exeResult = this.createResult(Optional.of(path2exe), Optional.of(jarIO.getStdout()), 0);

        } else{exeResult = this.createResult(Optional.empty(), Optional.of(jarIO.getStderr()), 1); }

        return exeResult;
    }

    /**
     * <p>Retrieve a previously-resolved jar dependency from the file system cache of a locally-installed
     * dependency management system. Maven or Gradle, for example.</p>
     *
     * <p>The prerequisite of „<em>previously-resolved</em>“ means this operation's <em>guaranteed</em> success in
     *  returning the requested dependency requires the module to have been configured as a dependency in the consumer
     *  project's build script.</p>
     *
     * @param coordinates A non-empty, colon-delimited string of the form: „<em>{@code group:artifact:version[:classifier]}</em>“.
     *                    The <em>{@code :classifier}</em> is optional.
     *
     * @return If found, the artifact specified by the given {@code coordinates}; wrapped in an {@link Optional}.
     *         Otherwise, {@link Optional#empty()}.
     */
    public Optional<File> fetch(String coordinates){

        return this.findInProject(coordinates).or(() -> this.finder.find(coordinates).getFile());
    }

    private JarCreationResult createResult(Optional<Path> jarPath, Optional<String> outPut, int status){

        return new DefaultJarCreationResult(jarPath, outPut, this.checkExecutable(jarPath.orElse(Paths.get("\\\\/dfasaf@-")).toFile()), status);
    }

    private Optional<File> findInProject(String coordinates){

        Optional<File> aJar;

        String gav[] = new ArtifactHelper().splitCoordinates(coordinates);

        Stream<File> rtClasspath = project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getFiles().parallelStream();

        aJar = rtClasspath.filter(f -> f.getName().endsWith(".jar")).filter(f -> f.getName().contains(gav[1])).filter(f-> f.getName().contains(gav[2])).findFirst().or(()-> Optional.empty());

        return aJar;

    }
}