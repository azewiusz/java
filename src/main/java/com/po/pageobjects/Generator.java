
/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package com.po.pageobjects;

//~--- non-JDK imports --------------------------------------------------------

import com.scripter.stateGrammaParser;
import com.scripter.stateGrammaParser.EnumDef;
import com.scripter.stateGrammaParser.EnumItem;
import com.scripter.stateGrammaParser.Field;
import com.scripter.stateGrammaParser.View;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/07
 * @author         Tomasz Kosiński <azewiusz@gmail.com>    
 */
public class Generator {
    public static void generateEnums(String outputDir, String outPackage, stateGrammaParser results) throws Exception {
        String packageDir = outPackage.replace(".", "/");

        outputDir += "/" + packageDir + "/";

        boolean created;

        // if (!created) {
        // throw new Exception("Could not create package direcotries " + outPackage);
        // }
        String  templateFile = "enumDeclare";
        STGroup group        = new STGroupDir("templates");
        ST      classT       = group.getInstanceOf("enumDeclare");
        boolean runOnce      = true;

        // par.listofAllEnums
        for (EnumDef d : results.listofAllEnums) {
            if (runOnce) {
                created = new File(outputDir).mkdirs();
                runOnce = false;
            }

            templateFile = "enumDeclare";

            for (stateGrammaParser.EnumItem s : d.items) {
                if (((s.name.charAt(0) >= '0') && (s.name.charAt(0) <= '9'))) {
                    templateFile = "enumDeclareI";
                    classT       = group.getInstanceOf(templateFile);
                }

                if (s.name.contains("|")) {
                    s.name = s.name.replace("|", "_");
                }
            }

            if (d.items == null) {
                System.out.println("STOP");
            }

            if (d == null) {
                System.out.println("STOP");
            }

            classT.add("fieldsList", d.items);
            classT.add("name", d.relatedFieldName);
            classT.add("package", outPackage);
            IOUtils.write(classT.render(), new FileOutputStream(new File(outputDir + d.relatedFieldName + ".java")),
                          "utf-8");
            classT = group.getInstanceOf(templateFile);
        }
    }

    public static void generateClasses(String outputDir, String outPackage, stateGrammaParser results,
                                       stateGrammaParser.View_groupContext ctx, Log log)
            throws Exception {
        String                packageDir        = outPackage.replace(".", "/");
        HashMap<String, View> views             = new HashMap<String, View>();
        ArrayList<String>     fieldDeclarations = new ArrayList<String>();
        int                   customsCounter    = 0;

        /**
         * First generate package with required-classes, copy them from this project's resource path
         */
        new File(outputDir + "/com/po/pageobjects").mkdirs();

        File output = new File(outputDir + "/com/po/pageobjects");

        // Byte buffer 1MB should be enough
        byte[]         buffer        = new byte[1024 * 1024];
        final String[] files_to_copy = {
            "required-classes/BTN.java", "required-classes/CHB.java", "required-classes/DDL.java",
            "required-classes/LBL.java", "required-classes/RBN.java", "required-classes/STemplate.java",
            "required-classes/TXT.java"
        };
        final String[] targets = {
            output.getAbsolutePath() + "/BTN.java", output.getAbsolutePath() + "/CHB.java",
            output.getAbsolutePath() + "/DDL.java", output.getAbsolutePath() + "/LBL.java",
            output.getAbsolutePath() + "/RBN.java", output.getAbsolutePath() + "/STemplate.java",
            output.getAbsolutePath() + "/TXT.java"
        };
        ClassLoader classLoader = Generator.class.getClassLoader();

        for (int i = 0; i < targets.length; i++) {
            InputStream resourceAsStream = classLoader.getResourceAsStream(files_to_copy[i]);
            
            log.info("Debug : File location is : "+new File(targets[i]).getAbsolutePath() + " RS "+resourceAsStream);
            IOUtils.copy(resourceAsStream, new FileOutputStream(new File(targets[i])));
            log.info("Written fully");
        }

        generateEnums(outputDir, outPackage + ".enums", results);
        outputDir += "/" + packageDir + "/";

        boolean runOnceCustoms   = true;
        String  outputDirCustoms = outputDir + "/custom/";
        String  outputDirClasses = outputDir + "/views/";
        boolean created          = new File(outputDir).mkdirs();
        boolean createdCustom;
        boolean createdViewDirs = new File(outputDirClasses).mkdirs();

        for (View v : ctx.viewGroupElement.views) {
            views.put(v.name, v);
        }

        ArrayList<Field> allFields = new ArrayList<Field>();

        for (View v : views.values()) {
            allFields.clear();
            fieldDeclarations.clear();

            for (int i = 0; i < v.inheritance.size(); i++) {
                View vi = views.get(v.inheritance.get(i));

                if (vi != null) {
                    for (Field f : vi.fields) {
                        allFields.add(f);
                    }
                }
            }

            allFields.addAll(v.fields);

            // if (!created) {
            // throw new Exception("Could not create package direcotries " + outPackage);
            // }
            String    templateFile = "enumDeclare";
            STGroup   group        = new STGroupDir("templates");
            ST        classT;
            STemplate st;

            // Preprocess all Fields, create necessary types
            for (Field d : allFields) {

                // Case when field has a parametrized locator
                if ((d.locatorParameters != null) && (d.locatorParameters.size() > 0)) {
                    customsCounter++;

                    if (runOnceCustoms) {

                        // This lines are to avoid creation of unnecessary package folders
                        createdCustom  = new File(outputDirCustoms).mkdirs();
                        runOnceCustoms = false;
                    }

                    // ParametrizedLocator field, requires adding custom type
                    classT = group.getInstanceOf("declareCustomLocator");

                    // fieldName,fieldType,parametersListNames,package,locatorType
                    classT.add("fieldName", d.name);
                    classT.add("fieldType", d.fieldType);
                    classT.add("parametersListNames", d.locatorParameters);
                    classT.add("package", outPackage + ".custom");
                    classT.add("locatorType", d.defaultLocationStrategy);

                    String result = classT.render();

                    IOUtils.write(result,
                                  new FileOutputStream(new File(outputDirCustoms + d.name + "_" + d.fieldType
                                      + ".java")), "utf-8");

                    // fieldName,fieldType,fieldLocator,locatorType
                    classT = group.getInstanceOf("declareFieldCustom");
                    classT.add("fieldName", d.name);
                    classT.add("fieldValueType", (d.enumName == null)
                                                 ? "java.lang.String"
                                                 : d.enumName);
                    classT.add("fieldType", d.fieldType);
                    classT.add("locatorType", d.defaultLocationStrategy);

                    String att = d.attributes.get(d.defaultLocationStrategy).toString();

                    att = att.replaceAll("^\'\'", "").replaceAll("\'\'$", "");
                    classT.add("fieldLocator", att);

                    // System.out.println(classT.render());
                    fieldDeclarations.add(classT.render());
                } else {

                    // Here we deal with regular filed declaration
                    classT = group.getInstanceOf("declareField");
                    classT.add("fieldName", d.name);
                    classT.add("fieldValueType", (d.enumName == null)
                                                 ? "java.lang.String"
                                                 : d.enumName);
                    classT.add("fieldType", d.fieldType);
                    classT.add("locatorType", d.defaultLocationStrategy);

                    if (d.defaultLocationStrategy == null) {
                        System.out.println("STOP");
                    }

                    String att = d.attributes.get(d.defaultLocationStrategy).toString();

                    att = att.replaceAll("^\'\'", "").replaceAll("\'\'$", "");
                    classT.add("fieldLocator", att);

                    // System.out.println(classT.render());
                    fieldDeclarations.add(classT.render());
                }

                for (String f : fieldDeclarations) {
                    System.out.println(f);
                }
            }

            classT = group.getInstanceOf("classTemplate");
            classT.add("package", outPackage);
            classT.add("fieldsList", fieldDeclarations);
            classT.add("enumsC", results.listofAllEnums.size() > 0);
            classT.add("customsC", customsCounter > 0);
            classT.add("viewParameters", ((v.viewParameters != null)
                                          ? v.viewParameters.items
                                          : null));

            String className = v.name.replaceAll("\\(", "").replaceAll("\\)", "");

            classT.add("className", className);
            IOUtils.write(classT.render(), new FileOutputStream(new File(outputDirClasses + className + ".java")),
                          "utf-8");
        }
    }
}
