package miho.rest.fuzzer.compiler;

import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.model.fuzzablepara.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    public boolean isURLContainDynamicObj(JSONObject req) throws JSONException {
        return req.getJSONObject("id").getString("endpoint").contains("{");
    }

    public RestFuzzerEnum.ValueType getURLDynamicObjectType(JSONObject req, int objPos) throws JSONException {
        JSONObject obj = req.getJSONArray("path").getJSONObject(objPos);
        String type;
        if (obj.has("DynamicObject")) {
            type = obj.getJSONObject("DynamicObject").getString("primitiveType");
        } else if (obj.has("Custom")) {
            type = obj.getJSONObject("Custom").getString("primitiveType");
        } else {
            type = obj.getJSONObject("Fuzzable").getString("primitiveType");
        }
        return RestFuzzerEnum.ValueType.getTypeFromString(type);
    }

    public List<Parameter> getURLDynamicParas(JSONObject req) throws JSONException {
        List<Parameter> allMatches = new ArrayList<>();
        String endpoint = req.getJSONObject("id").getString("endpoint");
        Matcher m = Pattern.compile("\\{.\\w+}")
            .matcher(endpoint);
        while (m.find()) {
            int objPos = getDynamicObjPos(endpoint, m.group());
            String str = m.group().replaceAll("\\{", "");
            str = str.replaceAll("}", "");
            allMatches.add(new Parameter(str, getURLDynamicObjectType(req, objPos)));
        }
        return allMatches;
    }

    public int getDynamicObjPos(String endpoint, String objName) {
        List<Integer> dashPos = new ArrayList<>();
        for (int i = -1; (i = endpoint.indexOf("/", i + 1)) != -1; i++) {
            dashPos.add(i);
        }
        int pos = 0;
        for (int i = 0; i < dashPos.size() - 1; i++) {
            String str = endpoint.substring(dashPos.get(i) + 1, dashPos.get(i + 1));
            if (str.equals(objName)) {
                return i;
            }
        }
        return dashPos.size() - 1;
    }

    public boolean isHasBodyParaList(JSONObject req) throws JSONException {
        return req.getJSONArray("bodyParameters").getJSONArray(0)
            .getJSONObject(1).getJSONArray("ParameterList").length() > 0;
    }

    public FuzzablePara getReqBodyParaList(JSONObject req) throws JSONException {
        return getParaBlock(req.getJSONArray("bodyParameters").getJSONArray(0)
            .getJSONObject(1).getJSONArray("ParameterList").getJSONObject(0)
            .getJSONObject("payload"));
    }

    public boolean isHasQueryParaList(JSONObject req) throws JSONException {
        return req.getJSONArray("queryParameters").getJSONArray(0)
            .getJSONObject(1).getJSONArray("ParameterList").length() > 0;
    }

    public FuzzablePara getQueryParaList(JSONObject req) throws JSONException {
        return getParaBlock(req.getJSONArray("queryParameters").getJSONArray(0)
            .getJSONObject(1).getJSONArray("ParameterList").getJSONObject(0)
            .getJSONObject("payload"));
    }

    public FuzzablePara getParaBlock(JSONObject obj) throws JSONException {
        FuzzablePara paraBlock = null;
        Iterator keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.equals("InternalNode")) {
                paraBlock = extractInternalNode(obj);
            } else {
                paraBlock = extractLeafNode(obj);
            }
        }
        return paraBlock;
    }

    private FuzzablePara extractInternalNode(JSONObject obj) throws JSONException {
        FuzzablePara internalBlock = null;

        JSONObject internalNodeInfo = obj.getJSONArray("InternalNode").getJSONObject(0);
        JSONArray internalNodeData = obj.getJSONArray("InternalNode").getJSONArray(1);

        String paraName = internalNodeInfo.getString("name");
        boolean isRequired = internalNodeInfo.getBoolean("isRequired");
        switch (internalNodeInfo.getString("propertyType")) {
            case "Array" -> internalBlock = extractArrayPara(internalNodeData, paraName, isRequired);
            case "Object" -> internalBlock = extractObjPara(internalNodeData);
            case "Property" -> internalBlock = extractPropPara(internalNodeData, paraName, isRequired);
        }
        return internalBlock;
    }

    private ArrayPara extractArrayPara(JSONArray internalNodeData, String paraName, boolean isRequired) throws JSONException {
        List<FuzzablePara> tmp = new ArrayList<>();
        for (int j = 0; j < internalNodeData.length(); j++) {
            tmp.add(getParaBlock(internalNodeData.getJSONObject(j)));
        }
        ArrayPara array = new ArrayPara(paraName, isRequired);
        array.setContent(tmp);
        return array;
    }

    private ObjectPara extractObjPara(JSONArray internalNodeData) throws JSONException {
        List<FuzzablePara> tmp = new ArrayList<>();
        for (int j = 0; j < internalNodeData.length(); j++) {
            tmp.add(getParaBlock(internalNodeData.getJSONObject(j)));
        }
        ObjectPara objectParas = new ObjectPara();
        objectParas.setContent(tmp);
        return objectParas;
    }

    private PropertyPara extractPropPara(JSONArray internalNodeData, String paraName, boolean isRequired) throws JSONException {
        List<FuzzablePara> tmp = new ArrayList<>();
        if (internalNodeData.length() <= 1) {
            tmp.add(getParaBlock(internalNodeData.getJSONObject(0)));
        } else {
            System.out.println("PROPERTY LENGTH > 1");
        }
        PropertyPara property = new PropertyPara(paraName, isRequired);
        property.setContent(tmp);
        return property;
    }

    public FuzzablePara extractLeafNode(JSONObject obj) throws JSONException {
        Parameter newPara = new Parameter(
            obj.getJSONObject("LeafNode").getString("name"),
            getParaType(obj)
        );
        if (isHasValueRange(obj)) {
            newPara.setValueRange(getParaValueRange(obj));
        }
        newPara.setRequired(obj.getJSONObject("LeafNode").getString("isRequired"));
        return newPara;
    }

    private RestFuzzerEnum.ValueType getParaType(JSONObject obj) throws JSONException {
        String type;
        JSONObject payload = obj.getJSONObject("LeafNode").getJSONObject("payload");
        if (payload.has("Fuzzable")) {
            if (payload.getJSONObject("Fuzzable").get("primitiveType") instanceof JSONObject) {
                type = payload.getJSONObject("Fuzzable").getJSONObject("primitiveType").getJSONArray("Enum").getString(1);
            } else {
                type = payload.getJSONObject("Fuzzable").getString("primitiveType");
            }
        } else {
            type = payload.getJSONObject("DynamicObject").getString("primitiveType");
        }
        return RestFuzzerEnum.ValueType.getTypeFromString(type);
    }

    public boolean isHasValueRange(JSONObject obj) throws JSONException {
        JSONObject payload = obj.getJSONObject("LeafNode").getJSONObject("payload");

        if (payload.has("Fuzzable")) {
            if (payload.getJSONObject("Fuzzable").get("primitiveType") instanceof JSONObject) {
                return payload.getJSONObject("Fuzzable").getJSONObject("primitiveType").has("Enum");
            }
        }
        return false;
    }

    public List<String> getParaValueRange(JSONObject obj) throws JSONException {
        JSONObject payload = obj.getJSONObject("LeafNode").getJSONObject("payload");

        List<String> rs = new ArrayList<>();
        if (payload.has("Fuzzable")) {
            if (payload.getJSONObject("Fuzzable").get("primitiveType") instanceof JSONObject) {
                JSONArray valRange = payload.getJSONObject("Fuzzable").getJSONObject("primitiveType").getJSONArray("Enum").getJSONArray(2);
                for (int i = 0; i < valRange.length(); i++) {
                    rs.add(valRange.getString(i));
                }
            }
        }
        return rs;
    }
}



























