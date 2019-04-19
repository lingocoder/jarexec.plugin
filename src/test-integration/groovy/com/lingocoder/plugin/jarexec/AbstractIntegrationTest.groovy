/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lingocoder.plugin.jarexec

import java.io.*;

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.plugins.ExtensionAware

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification


abstract class AbstractIntegrationTest extends Specification {
    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir

    def setup() {
        projectDir = temporaryFolder.root
        System.setOut(new FilteredPrintStream(System.out))
    }

    private static class FilteredPrintStream extends PrintStream {
        FilteredPrintStream(PrintStream source) {
            super(source)
        }

        @Override
        void write(byte[] buf, int off, int len) {
            String string = new String(buf)

            if(!string.contains(" DEBUG ")) {
                super.write(buf, off, len)
            }
        }
    }
    
    void applyJarexecPlugin(Project project){
        project.apply(plugin: JarExecPlugin)
    }
 
    void taskExists(){
        project.tasks.findByName("execjar")
  
    }   
    
    void hasExpectedProperties(){
        taskExists()
        project.hasProperty("jarhelper")
        project.hasProperty("jarexec")
        project.jarexec.jar != null
        project.jarexec.args != null
        project.jarexec.hasProperty('classpath')
        project.jarexec.hasProperty('mainClass')
        project.jarexec.hasProperty('watchInFile')
        project.jarexec.hasProperty('watchInFiles')
        project.jarexec.hasProperty('watchOutDir')
    }
    
    void addDepToConfig(String whichDep, String whichConfig){
        project.getRepositories().add(project.getRepositories().mavenLocal())
        project.getRepositories().add(project.getRepositories().mavenCentral())
        project.getRepositories().add(project.getRepositories().jcenter())
        project.getConfigurations().create(whichConfig)
        project.getDependencies().add(whichConfig, project.getDependencies().create(whichDep))
        project.getConfigurations().getByName(whichConfig).resolve()
    }
    
    File assignDefaultJar(){
    
        ExtensionAware jarExecExtension = (ExtensionAware) project.extensions.getByType(JarExecExtension)
        File tmpJar = project.file("./a.jar")
        jarExecExtension.jar = tmpJar
        return tmpJar
    }
}
