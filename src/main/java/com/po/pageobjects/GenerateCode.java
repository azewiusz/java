package com.po.pageobjects;

//~--- non-JDK imports --------------------------------------------------------

import com.scripter.stateGrammaLexer;
import com.scripter.stateGrammaParser;
import com.scripter.stateGrammaParser.View_groupContext;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import org.apache.maven.plugin.logging.Log;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/07
 * @author         Tomasz Kosiński <azewiusz@gmail.com>    
 */
public class GenerateCode {

    /**
     * Generates source Java files from language model file to a specified package and directory
     * @param langModelFile
     * @param _package
     * @param outPath
     * @param log
     *     @throws java.io.IOException
     */
    public static void generateCode(String langModelFile, String _package, String outPath, Log log)
            throws IOException, Exception {
        ANTLRInputStream  input  = new ANTLRFileStream(langModelFile, "utf-8");
        stateGrammaLexer  tsl    = new stateGrammaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(tsl);
        stateGrammaParser par    = new stateGrammaParser(tokens);
        View_groupContext ctx    = par.view_group();

        System.out.println("Identified View declarations:");
        Generator.generateClasses(outPath, _package, par, ctx, log);
    }
}
