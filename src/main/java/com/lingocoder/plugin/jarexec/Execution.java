package com.lingocoder.plugin.jarexec;

import com.lingocoder.process.io.ProcessIO;
import com.lingocoder.jar.JarTool;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Does the heavy lifting concerning executing stuff.
 */
public class Execution {

    
    /**
     * Perform <em><a href="http://bit.ly/PreJ9Opts" target="_blank">supported commands</a></em> using the given jar with the given input.
     * 
     * @param jar A jar that contains a class with a <em><code>main</code></em> method.
     * @param classpath The classpath needed by the class with the <em><code>main</code></em> method.
     * @param args The arguments needed by the class with the <em><code>main</code></em> method.
     * @param mainClass The fully-qualified name of the class with the <em><code>main</code></em> method.
     *                  For example, <em><code>com.example.foo.Main</code></em>
     * 
     * @return <p>A composite result containing whatever the forked process that will be
     *         spawned by this method, may have written to {@code stdout} and/or {@code stderr}.</p>
     * 
     *         <p>The result will also hold an exit code of either <em>{@code 0}</em> to
     *         indicate a successful exit, or <em>{@code 1}</em> to indicate the
     *         process produced and error.</p>
     */
    protected ProcessIO execute( Property<File> jar,
            Property<FileCollection> classpath, ListProperty<String> args,
            Property<String> mainClass ) {
        
        JarTool call = new JarTool( );
        
        ProcessIO io = null;

        List<String> argsList = new ArrayList<>( args.get( ).size( ) + 1 );

        if ( classpath.isPresent( ) && mainClass.isPresent( ) ) {

            FileCollection cp = classpath.get( );

            String clsPath = cp.getAsPath( );

            argsList.add( 0, clsPath + ";" + jar.get( ).getAbsolutePath( ) );

            argsList.add( 1, mainClass.get( ) );

            argsList.addAll( args.get( ) );

            io = call.java( argsList.toArray( new String[ argsList.size( ) ] ) );
            
            System.out.printf( "%s%n", io.getStdout( ) );

        } else {

            argsList.add( 0, jar.get( ).getAbsolutePath( ) );

            argsList.addAll( args.get( ) );

            io = call.javaJar( argsList.toArray( new String[ argsList.size( ) ] ) );

            System.out.printf( "%s%n", io );
        }
        return io;
    }
}