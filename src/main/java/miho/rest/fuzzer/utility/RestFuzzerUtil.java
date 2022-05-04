package miho.rest.fuzzer.utility;

import com.goebl.david.Response;
import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.fuzzer.FuzzHandler;
import miho.rest.fuzzer.model.fuzzablepara.*;
import miho.rest.fuzzer.model.request.MyRequest;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestFuzzerUtil {
    private static final int MAX_INT = 100;
    private static final int MIN_INT = 0;
    private static final int STR_LEN = 7;

    public static String getRandomValueFromType(RestFuzzerEnum.ValueType valueType) {
        if (valueType == RestFuzzerEnum.ValueType.INT) {
            Random random = new Random();
            return String.valueOf(random.nextInt(MAX_INT - MIN_INT) + MIN_INT);
        }
        return new RandomString(STR_LEN, ThreadLocalRandom.current()).nextString();
    }

    public static JSONObject readDataFromJsonFile(String filePath) throws JSONException, IOException {
        Path path = Paths.get(filePath);
        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        lines.close();

        return new JSONObject(data);
    }
    
    public static boolean isSpecFileValid(String url) {
        String ext = FilenameUtils.getExtension(url);
        return ext.equals("json") || ext.equals("raml") || ext.equals("apib");
        
    }

    public static void writeToFile(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    public static JSONArray convertJsonObjToJsonArr(JSONObject obj) throws JSONException {
        Iterator x = obj.keys();
        JSONArray jsonArray = new JSONArray();
        while (x.hasNext()) {
            String key = (String) x.next();
            jsonArray.put(obj.get(key));
        }
        return jsonArray;
    }

    public static void printResponseList(Map<String, Response> map) {
        for (Map.Entry<String, Response> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "\n" + entry.getValue());
        }
    }

    public static void printRequestSeq(List<List<MyRequest>> sequenceSet) {
        for (List<MyRequest> list : sequenceSet) {
            System.out.println("______________________________________________________");
            for (MyRequest req : list) {
                System.out.println(req.getEndpoint() + " " + req.getMethodEnum().name());
            }
        }
    }

    public static StringBuilder getReqInfo(MyRequest req) {
        StringBuilder str = new StringBuilder();
        str.append("-------------------------------------").append("\n");
        str.append("METHOD: ").append(req.getMethodEnum().name()).append("\n");
        str.append("ENDPOINT: ").append(req.getEndpoint()).append("\n");
        str.append("QUERY: ");
        if (req.getQueryPara() != null) {
            str.append("\n");
            printFuzzablePara(req.getQueryPara(), str);
        } else {
            str.append("EMPTY").append("\n");
        }
    
        str.append("BODY: ");
        if (req.getBodyPara() != null) {
            str.append("\n");
            printFuzzablePara(req.getBodyPara(), str);
        } else {
            str.append("EMPTY").append("\n");
        }
        return str;
    }

    public static void printFuzzablePara(FuzzablePara para, StringBuilder str) {
        switch (para.getParaType()) {
            case PROPERTY -> {
                PropertyPara prop = (PropertyPara) para;
//                System.out.println(prop.getName());
                str.append(prop.getName()).append("\n");
                for (FuzzablePara par : prop.getContent()) {
                    printFuzzablePara(par, str);
                }
            }
//                System.out.println("},");
            case OBJECT -> {
                ObjectPara obj = (ObjectPara) para;
//                System.out.println(" {");
                str.append("{").append("\n");
    
                for (FuzzablePara par : obj.getContent()) {
                    printFuzzablePara(par, str);
                }
//                System.out.println("},");
                str.append("},").append("\n");
            }
            case ARRAY -> {
                ArrayPara array = (ArrayPara) para;
//                System.out.println(array.getName() + ": [");
                str.append(array.getName()).append(": [").append("\n");
                for (FuzzablePara par : array.getContent()) {
                    printFuzzablePara(par, str);
                }
//                System.out.println("],");
                str.append("],").append("\n");
            }
            default -> {
                Parameter parameter = (Parameter) para;
//                System.out.println(parameter.getName() + " " + parameter.getValueType() + ": " + parameter.getValueRange() + ",");
                str.append(parameter.getName()).append(" ").append(parameter.getValueType()).append(",").append("\n");
            }
        }
    }


    public static void main(String[] args) {
        System.out.print(getRandomValueFromType(RestFuzzerEnum.ValueType.STRING));
    }

}


