package miho.rest.fuzzer;

import java.util.Arrays;

public class RestFuzzerEnum {
    public enum Method {
        GET,
        PUT,
        POST,
        PATCH,
        DELETE;

        public static Method getMethodFromString(String str) {
            return switch (str.toLowerCase()) {
                case "post" -> POST;
                case "put" -> PUT;
                case "delete" -> DELETE;
                case "patch" -> PATCH;
                default -> GET;
            };
        }
    }

    public enum ValueType {
        INT,
        STRING;

        public static ValueType getTypeFromString(String str) {
            if ("int".equals(str.toLowerCase())) {
                return INT;
            }
            return STRING;
        }
    }

    public enum ParaType {
        PARAMETER,
        OBJECT,
        ARRAY,
        PROPERTY;

        public static ParaType getParaTypeFromStr(String str) {
            return switch (str.toLowerCase()) {
                case "object" -> OBJECT;
                case "array" -> ARRAY;
                case "property" -> PROPERTY;
                default -> PARAMETER;
            };
        }
    }
    
    public enum SpecType {
        Swagger,
        RAML,
        APIB;
    
        public static String[] getTypeAsStrArr() {
            return Arrays.stream(SpecType.values()).map(Enum::name).toArray(String[]::new);
        }
        
        public static SpecType getTypeFromExtension(String str) {
            return switch (str.toLowerCase()) {
                case "raml" -> RAML;
                case "apib" -> APIB;
                default -> Swagger;
            };
        }
    }
}






















