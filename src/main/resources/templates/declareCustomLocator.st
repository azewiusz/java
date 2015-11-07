declareCustomLocator(fieldName,fieldType,parametersListNames,package,locatorType) ::= "
package <package>;
import com.po.pageobjects.*;
import java.util.HashMap;
import org.openqa.selenium.By;

public class <fieldName>_<fieldType>\<T> {
    
    public T value;
    public By locator;
    public String locatorString;
    
    HashMap\<String,String> valuesMap = new HashMap\<String,String>();
    
    public <fieldName>_<fieldType>(String loc){ locatorString = loc;};
    
    public By getLocator( <parametersListNames:{x | String <x>};separator=\",\">)
    {
        valuesMap.clear();
        <parametersListNames:{x | valuesMap.put(\"$#<x>#$\",<x>);};separator=\"\r\n\">
        String tempLocator = STemplate.applyMapping(valuesMap,locatorString);
        locator = By.<locatorType>(tempLocator);
        return locator;
    }       
}
"