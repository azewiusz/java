classTemplate(className,fieldsList,package,enumsC,customsC,viewParameters) ::= "
package <package>.views;
<if(enumsC)>import <package>.enums.*;<endif>
<if(customsC)>import <package>.custom.*;<endif>
import com.po.pageobjects.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
* View Declaration
*
**/

public class <className> {

 <fieldsList:{x | <x> };separator=\"\r\n\">;

 public <className>(){};


/**
* Opens specified view using declared parameters
*/
 public void openView( WebDriver driver <if(viewParameters)>,<endif> <viewParameters:{x | Object <x.value> };separator=\",\r\n\">)
{



}

}
"