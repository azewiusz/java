package com.po.pageobjects;

//~--- JDK imports ------------------------------------------------------------

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class description
 * Helper class used in auto-generated code
 * DO not modify!, this class is auto-generated each time you compile project.
 *
 * @version        1.0, 15/11/07
 * @author         Tomasz Kosiński <azewiusz@gmail.com>
 */
public class STemplate {
    public static ArrayList<String> getAllFields(String file_content) {
        Pattern                 p      = Pattern.compile("([$]#)[a-zA-Z0-9_]+(#[$])");
        Matcher                 m      = p.matcher(file_content);
        ArrayList<String>       fields = new ArrayList<String>();
        HashMap<String, String> tab    = new HashMap<String, String>();

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
    public static String applyMapping(HashMap<String, String> map, String locator) {
        for (String k : map.keySet()) {
            locator = locator.replace(k, map.get(k));
        }

        return locator;
    }
}
