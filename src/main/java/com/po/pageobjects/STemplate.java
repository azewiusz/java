/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.po.pageobjects;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tomek
 */
public class STemplate {

    /**
     * @param args the command line arguments
     */
    /*
     * public static void main(String[] args) { // TODO code application logic
     * here //STGroup group = new STGroupDir("c:\\test"); String fc=null; try {
     * fc = FileUtils.readFileAsString("C:\\test\\list_campaigns.st"); } catch
     * (IOException ex) {
     * Logger.getLogger(STemplate.class.getName()).log(Level.SEVERE, null, ex); }
     * ST st = new ST(fc,'`','`'); Map<String,Object> list = st.getAttributes();
     * //st.ge //Regexp r = new Regexp("[$##][a-zA-Z0-9_][##$]");
     *
     * Pattern p = Pattern.compile("([$]##)[a-zA-Z0-9]+(##[$])"); Matcher m =
     * p.matcher(fc); Matcher m2 = p.matcher("$##PersonName##$");
     * System.out.println(m2.matches()); // for(String s : list.keySet()) //
     * System.out.println(list.get(s)); while(m.find()) {
     * System.out.println(m.group()); } }
     *
     */
    public static ArrayList<String> getAllFields(String file_content) {
        Pattern p = Pattern.compile("([$]#)[a-zA-Z0-9_]+(#[$])");
        Matcher m = p.matcher(file_content);
        ArrayList<String> fields = new ArrayList<String>();
        HashMap<String, String> tab = new HashMap<String, String>();
   
        while (m.find()) {
       
            String ma = tab.get(m.group());
            if (ma == null) {
                ma = m.group().replaceAll("([$]#)", "").replaceAll("(#[$])", "");
                tab.put(m.group(), ma);
                fields.add(ma);
            } 
          
        }
        return fields;
    }

    /**
     * It tries to replace markers like ${ColumnName} with adequate value taken for specified row and $[ColumnName] with adequate value in column where all whitespaces are replaced with
     * underscore character
     * @param model
     * @param row
     * @param pattern
     * @return 
     */
    public static String applyMapping(HashMap<String,String> map, String locator) {
        
        for(String k : map.keySet())
        {
           locator = locator.replace(k, map.get(k));
        }
        
        return locator;
    }
}
