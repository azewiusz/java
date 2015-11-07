declareField(fieldName,fieldType,fieldLocator,locatorType,fieldValueType) ::= "
/** <fieldName> Locator Function: <fieldLocator> */
public <fieldType>\<<fieldValueType>> <fieldName> = new <fieldType>\<<fieldValueType>>(By.<locatorType>(\"<fieldLocator>\"));"

