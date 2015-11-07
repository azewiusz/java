package com.pageobjects2;

//~--- non-JDK imports --------------------------------------------------------

/*
* Copyright 2001-2005 The Apache Software Foundation.
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
import com.po.pageobjects.GenerateCode;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.Properties;
import java.util.Set;

/**
 * Mojo class, for compiling viewmodel language files
 * @author Tomek
 */
@Mojo(
    name         = "pageobjects2",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
public class PageObjects2Mojo extends AbstractMojo {

    /**
     * Location of the file. ${project.build.directory}
     */
    @Parameter(
        defaultValue = "${basedir}/target/generated-sources/viewmodels",
        property     = "outputDir",
        required     = false
    )
    private File outputDirectory;

    /**
     * Source directory where all language model files are stored
     */
    @Parameter(
        defaultValue = "${basedir}/src/main/viewmodels",
        property     = "sourceDir",
        required     = false
    )
    private File   sourceDirectory;
    @Parameter(
        property     = "defaultPackage",
        defaultValue = "com.pageobjects2",
        required     = true
    )
    private String defaultPackage;

    /**
     * Each viewmodel.txt file in the source path needs to be listed in a properties section of <files> node with key names set to
     * view model file name and value set to desired destination package
     * Example fragment:
     * <configuration>
     *               <files>
     *                   <property>
     *                       <name>viewmodel.txt</name>
     *                       <value>com.mypackage</value>
     *                   </property>
     *               </files>
     *           </configuration>
     */
    @Parameter(property = "files")
    private Properties files;

    public void execute() throws MojoExecutionException {
        Set<Object> keySet  = files.keySet();
        int         counter = 0;

        for (Object o : keySet) {
            String fileName = o.toString();
            String _package = files.getProperty(fileName);

            if (_package == null | _package.contentEquals("")) {
                _package = defaultPackage;
            }

            try {
                getLog().info("Generating sources for View Model File " + sourceDirectory.getAbsolutePath() + "/"
                              + fileName + System.lineSeparator() + "Package\t:\t" + _package + " Target Path : "
                              + outputDirectory.getAbsolutePath() + "/");
                GenerateCode.generateCode(sourceDirectory.getAbsolutePath() + "/" + fileName, _package,
                                          outputDirectory.getAbsolutePath() + "/", getLog());
                counter++;
            } catch (Exception ex) {
                getLog().error("Error while generating view model file : " + sourceDirectory.getAbsolutePath() + "/"
                               + fileName + " "+ex.getLocalizedMessage());

                throw new MojoExecutionException("Error while generating view model file : "
                                                 + sourceDirectory.getAbsolutePath() + "/" + fileName + " "+ex.getLocalizedMessage());
            }
        }

        getLog().info("Generated " + counter + " View Models succesfully.");
    }

    /**
     * @return the outputDirectory
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * @param outputDirectory the outputDirectory to set
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return the sourceDirectory
     */
    public File getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * @param sourceDirectory the sourceDirectory to set
     */
    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * @return the defaultPackage
     */
    public String getDefaultPackage() {
        return defaultPackage;
    }

    /**
     * @param defaultPackage the defaultPackage to set
     */
    public void setDefaultPackage(String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    /**
     * @return the myProperties
     */
    public Properties getMyProperties() {
        return files;
    }

    /**
     * @param myProperties the myProperties to set
     */
    public void setMyProperties(Properties myProperties) {
        this.files = myProperties;
    }
}
