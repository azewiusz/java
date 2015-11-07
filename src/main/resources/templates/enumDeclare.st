enumDeclare(name,fieldsList,package) ::= "
package <package>;

 public enum <name>  {
        <fieldsList:{x | <x.name>(\"<x.value>\")};separator=\",\">;

            
        public String code;    

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