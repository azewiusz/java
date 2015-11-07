enumDeclareI(name,fieldsList,package) ::= "
package <package>;

public enum <name>  {
        <fieldsList:{x | I<x.name>(\"<x.value>\")};separator=\",\">;

        private String code;

        private <name>(String value) {
            this.code = code;
        }

        public Object getValue() {
            return code;
        }

        public void setValue(Object val) {
            this.code = (String) val;
        }
    }
"