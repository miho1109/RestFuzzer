package miho.rest.fuzzer.model.request;

import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.model.fuzzablepara.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyRequest {
    private final String endpoint;
    private final RestFuzzerEnum.Method methodEnum;
    private List<Parameter> dynamicSegmentList;
    private FuzzablePara queryPara;
    private FuzzablePara bodyPara;
    private FuzzablePara headerPara;
    private final Set<String> producerKeyList;

    public MyRequest(
        String endpoint,
        RestFuzzerEnum.Method methodEnum
    ) {
        this.endpoint = endpoint;
        this.methodEnum = methodEnum;
        this.producerKeyList = new HashSet<>();
    }

    public void setDynamicSegmentList(List<Parameter> dynamicSegmentList) {
        this.dynamicSegmentList = dynamicSegmentList;
    }

    public void setQueryPara(FuzzablePara queryPara) {
        this.queryPara = queryPara;
    }

    public void setBodyPara(FuzzablePara bodyPara) {
        this.bodyPara = bodyPara;
    }

    public void setHeaderPara(FuzzablePara headerPara) {
        this.headerPara = headerPara;
    }

    public void addNewProducer(String producer) {
        producerKeyList.add(producer);
    }

    public Set<String> getProducerReqSet() {
        return producerKeyList;
    }

    public String getProducerName(String producerKey) {
        return producerKey.substring(0, producerKey.indexOf(","));
    }

    public RestFuzzerEnum.Method getProducerMethod(String producerKey) {
        return RestFuzzerEnum.Method.getMethodFromString(producerKey.substring(producerKey.indexOf(",") + 1));
    }

    public String getEndpoint() {
        return endpoint;
    }

    public RestFuzzerEnum.Method getMethodEnum() {
        return methodEnum;
    }

    public List<Parameter> getDynamicSegmentList() {
        return dynamicSegmentList;
    }

    public FuzzablePara getQueryPara() {
        return queryPara;
    }

    public FuzzablePara getBodyPara() {
        return bodyPara;
    }

    public FuzzablePara getHeaderPara() {
        return headerPara;
    }

    public void updateParaProducerName(String paraListName, JSONObject jsonPara) throws JSONException {
        switch (paraListName) {
            case "Path":
                updateDynamicSegmentProducerName(jsonPara);
            case "Query":
                updateQueryParaProducerName(jsonPara);
            case "Body":
                updateBodyParaProducerName(jsonPara);
            default:
        }
    }

    private void updateDynamicSegmentProducerName(JSONObject jsonPara) throws JSONException {
        if (dynamicSegmentList != null) {
            for (Parameter para : dynamicSegmentList) {
                if (para.getName().equals(jsonPara.getString("consumer_param"))) {
                    para.setProducerName(jsonPara.getString("producer_resource_name"));
                }
            }
        }
    }

    private void updateQueryParaProducerName(JSONObject jsonPara) throws JSONException {
        if (queryPara != null) {
            List<Parameter> queryParameters = getAllParameter(queryPara);
            for (Parameter para : queryParameters) {
                if (para.getName().equals(jsonPara.getString("consumer_param"))) {
                    para.setProducerName(jsonPara.getString("producer_resource_name"));
                }
            }
        }
    }

    private void updateBodyParaProducerName(JSONObject jsonPara) throws JSONException {
        if (bodyPara != null) {
            List<Parameter> bodyParameters = getAllParameter(bodyPara);
            for (Parameter para : bodyParameters) {
                if (para.getName().equals(jsonPara.getString("consumer_param"))) {
                    para.setProducerName(jsonPara.getString("producer_resource_name"));
                }
            }
        }
    }

    public List<Parameter> getAllParameter(FuzzablePara para) {
        List<Parameter> parameterList = new ArrayList<>();

        switch (para.getParaType()) {
            case PROPERTY -> {
                PropertyPara prop = (PropertyPara) para;
                for (FuzzablePara par : prop.getContent()) {
                    getAllParameter(par);
                }
            }
            case OBJECT -> {
                ObjectPara obj = (ObjectPara) para;
                for (FuzzablePara par : obj.getContent()) {
                    getAllParameter(par);
                }
            }
            case ARRAY -> {
                ArrayPara array = (ArrayPara) para;
                for (FuzzablePara par : array.getContent()) {
                    getAllParameter(par);
                }
            }
            default -> parameterList.add((Parameter) para);
        }
        return parameterList;
    }

}
