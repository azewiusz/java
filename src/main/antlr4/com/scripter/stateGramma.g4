/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar stateGramma;


@parser::header{
                
  import java.util.HashMap;              
  import java.util.ArrayList; 
  import java.util.Set;
  import java.util.regex.Matcher;
  import java.util.regex.Pattern;
                }
@parser::members {
                  
                  public ArrayList<EnumDef> listofAllEnums = new ArrayList<EnumDef>();
                  
                  
                     public class ViewGroup
                    {
                      public ViewGroup(){};
                      public ArrayList<View> views = new ArrayList<View>();
                    }
                    
                    public class EnumItem {
                                           public String name;
                                           public String value;
                                           
                                           public EnumItem(String name,String value)
                                           {
                                            this.name = name;
                                            this.value = value;
                                            
                                            }
                                           @Override
                                           public String toString()
                                           {
                                            
                                            if (name==null)
                                              return value;
                                            if (value==null)
                                              return name;
                                            
                                            return null;
                                            
                                            }
                                           
                                           }
                    
                    
                    public class EnumDef {
                                          
                                      public EnumDef(){};
                                      public ArrayList<EnumItem> items = new ArrayList<EnumItem>();
                                         
                                      // final class name in external language   
                                      public String outputClass;
                                      public String relatedFieldName;
                                          }
                    
                    
                    public class Field{
                                       
                       public String name;
                       public HashMap<String,Object> attributes = new HashMap<String,Object>();
                       public String fieldType;
                       public String enumName=null;
                       public String outputType;
                       public String defaultLocationStrategy=null;
                       public ArrayList<String> locatorParameters;
                       
                       
                       public Field(){}
                                                                                                                         
                      }
                                        
                    public class View {
                                 
                      
                      public String name;
                      public ArrayList<Field> fields = new ArrayList<Field>();
                      // Optional view parameters, this can be used to define a one step
                      // function that will bring user to a specified view, it can be user's credentials, url address etc.
                      // These are generally optional parameters
                      public EnumDef viewParameters;
                      // List of classes from which this class will inherit fields (they are copied as multiple inheritance)
                      public ArrayList<String> inheritance = new ArrayList<String>();
                      
                      public View(){};                     
                    } 
                    
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
     * @param model
     * @param row
     * @param pattern
     * @return 
     */
    public static String applyMapping(HashMap<String,String> map, int row, String locator) {
        
        for(String k : map.keySet())
        {
           locator = locator.replace(k, map.get(k));
        }
        
        return locator;
    }
                    
                    
}

view_group returns [ViewGroup viewGroupElement]
@init{
      $viewGroupElement = new ViewGroup();
      }
    : (v+=view)+   {
                           
                       for(ViewContext ctx : $v)
                       {
                        if (ctx.viewElement != null)
                        $viewGroupElement.views.add(ctx.viewElement);
                        
                        }
                           
                           };

view returns [View viewElement]
    :  vd=view_declaration  OPEN_B (fieldArray+=field)* CLOSE_B {
                                                              
                                                             $viewElement = $vd.viewElement;                                                             
                                                              for(FieldContext f : $fieldArray)
                                                              {
                                                               $viewElement.fields.add(f.fieldElement);
                                                               }
                                                               
                                                              }
    ;

view_declaration returns [View viewElement]
@init{
      $viewElement = new View();
      } : VIEW_NAME vparams=assign_sublist[true]?  vi=view_inheritance {
                                      
                                      $viewElement.name = $VIEW_NAME.text;
                                      $viewElement.inheritance = $vi.inheritance;
                                      try{
                                      $viewElement.viewParameters = $vparams.enumElement;
                                      } catch(NullPointerException ex){}
                                      
                                      };

view_inheritance returns [ArrayList<String> inheritance]
@init{
      $inheritance = new ArrayList<String>();
}
    
    : (INHERITS first=VIEW_NAME (COMMA list+=VIEW_NAME)*)? 
                   {
                    
                    if ($first!=null)
                    {
                     $inheritance.add($first.getText());
                     }
                    
                    for(Token viewIn : $list)
                    {
                     
                     $inheritance.add(viewIn.getText()); 
                     
                     }
                    
                    };

field returns [Field fieldElement]
@init{
      $fieldElement = new Field();
      }
    
    : NAME DDOT ft=field_type OPEN_T al=assign_list CLOSE_T SEMI 
                {
                 $fieldElement.attributes = $al.attributesElement;
                 $fieldElement.fieldType = $ft.text;   
                 $fieldElement.name = $NAME.getText();
                 
                 for(String k : $fieldElement.attributes.keySet() )
                 {
                  
                  Object o = $fieldElement.attributes.get(k);
                  if (o instanceof EnumDef)
                  {
                    ((EnumDef)o).relatedFieldName = $NAME.text + "Enum";
                    $fieldElement.enumName = ((EnumDef)o).relatedFieldName;
                   }
                  }
                 
                 if ($fieldElement.attributes.containsKey("id"))
                 {
                  
                  $fieldElement.defaultLocationStrategy="id";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("id").toString());
                  
                  }
                 
                 if ($fieldElement.attributes.containsKey("cssSelector"))
                 {
                  
                  $fieldElement.defaultLocationStrategy="cssSelector";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("cssSelector").toString());                  
                  
                  }
                 
                  if ($fieldElement.attributes.containsKey("xpath"))
                 {                 
                  $fieldElement.defaultLocationStrategy="xpath";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("xpath").toString());
                  }
                 
                 if ($fieldElement.attributes.containsKey("value"))
                 {                 
                  $fieldElement.defaultLocationStrategy="xpath";
                  String temp = $fieldElement.attributes.get("value").toString();
                  $fieldElement.attributes.remove("value");
                  temp =  temp.replaceAll("^\'\'", "").replaceAll("\'\'$", "");
                  $fieldElement.attributes.put("xpath","//*[@value='"+temp+"']");
                  
                  }
                 
                  if ($fieldElement.attributes.containsKey("className"))
                 {                 
                  $fieldElement.defaultLocationStrategy="className";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("className").toString());
                  }
                 
                  if ($fieldElement.attributes.containsKey("linkText"))
                 {                 
                  $fieldElement.defaultLocationStrategy="linkText";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("linkText").toString());
                  }
                 
                  if ($fieldElement.attributes.containsKey("name"))
                 {                 
                  $fieldElement.defaultLocationStrategy="name";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("name").toString());
                  }   
                 
                  if ($fieldElement.attributes.containsKey("partialLinkText"))
                  {                 
                  $fieldElement.defaultLocationStrategy="partialLinkText";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("partialLinkText").toString());
                  } 
                   if ($fieldElement.attributes.containsKey("tagName"))
                  {                 
                  $fieldElement.defaultLocationStrategy="tagName";
                  $fieldElement.locatorParameters = getAllFields($fieldElement.attributes.get("tagName").toString());
                  }
                 
                };

field_type: NAME | DDL | LBL | TXT | BTN | CHB | RBN;      

assign_list returns [HashMap<String,Object> attributesElement] 
@init{
$attributesElement = new HashMap<String,Object>();
}
            : ( (COMMA? listName1+=NAME EQ listA+=SIMPLE_VALUE) | (COMMA? listName2+=NAME EQ listB+=assign_sublist[false])             
             )+   {

                    // Here we merge results from two independant lists
                    int i=0;
                    for(Token name : $listName1)
                    {
                       $attributesElement.put(name.getText(),$listA.get(i).getText()); 
                    }

                    i=0;
                    for(Token name : $listName2)
                    {
                       $attributesElement.put(name.getText(),$listB.get(i).enumElement);
                      
                    }    

                    };

assign_sublist [boolean viewMode] returns [EnumDef enumElement] 
@init{
      $enumElement = new EnumDef();
      }
    
    :   OPEN_T (COMMA? enu+=NAME)+? CLOSE_T {
                                             
                                             for(Token nc : $enu)
                                             {
                                              $enumElement.items.add(new EnumItem(nc.getText(),nc.getText()));
                                              
                                              }
                                            // Dont recognize this element as EnumDeclaration if it appears in View Input Parameters declaration
                                             if (!$viewMode)
                                             listofAllEnums.add($enumElement);
                                             };

SIMPLE_VALUE: STAG PT*? STAG;

VIEW_NAME : OPEN_T NAME+? CLOSE_T;

INHERITS : 'inherits';
NAME : ( 'a'..'z' | 'A'..'Z' | '0'..'9' | '_' | '|')+;
 fragment PT: ('\u0000'..'\u0009' | '\u000B'..'\u000C' | '\u000E'..'\uFFFE')+;
//fragment PT: ('\u0000'..'\uFFFE')+;
DDL: 'DDL';
LBL: 'LBL';
TXT: 'TXT';
BTN: 'BTN';
CHB: 'CHB';
RBN: 'RBN';
EQ : '=';
STAG : '\'\'';
OPEN_T : '(';
CLOSE_T : ')';
OPEN_B : '{';
CLOSE_B: '}';
DDOT:':';
WS : (' ' | '\t' | '\r' | '\n')+ -> skip;
SEMI : ';';
COMMA : ',';


// A COMMENT, CAN APPEAR EVERYWHERE IN THE CODE
COMMENT : '/**' .*? '**/' -> skip;