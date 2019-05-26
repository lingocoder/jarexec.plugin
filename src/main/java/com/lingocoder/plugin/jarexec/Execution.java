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