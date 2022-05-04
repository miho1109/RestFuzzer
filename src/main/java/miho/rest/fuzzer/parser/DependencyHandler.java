package miho.rest.fuzzer.parser;

import miho.rest.fuzzer.model.request.MyRequest;
import miho.rest.fuzzer.utility.RestFuzzerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class DependencyHandler {
    private static DependencyHandler dependencyHandler;

    public static JSONObject dependencyCollection;

    public static DependencyHandler getInstance() {
        if (dependencyHandler == null) {
            dependencyHandler = new DependencyHandler();
        }
        return dependencyHandler;
    }

    public static boolean isDependenciesReady() {
        return dependencyCollection != null && dependencyCollection.length() > 0;
    }

    public void getDependency(String filePath) throws IOException, JSONException {
        dependencyCollection = RestFuzzerUtil.readDataFromJsonFile(filePath);
    }

    public void updateReqDependency() throws JSONException {
        for (MyRequest req : GrammarHandler.requestCollection) {
            if (dependencyCollection.has(req.getEndpoint())) {
                Iterator x = dependencyCollection.keys();
                while (x.hasNext()) {
                    String key = (String) x.next();
                    if (key.equals(req.getEndpoint())) {
                        JSONObject dependencyMethod = dependencyCollection.getJSONObject(key).getJSONObject(req.getMethodEnum().name());
                        Iterator y = dependencyMethod.keys();
                        while (y.hasNext()) {
                            String dependencyParaArrKey = (String) y.next();
                            JSONArray dependencyPara = dependencyMethod.getJSONArray(dependencyParaArrKey);
                            for (int i = 0; i < dependencyPara.length(); i++) {
                                if (!dependencyPara.getJSONObject(i).getString("producer_endpoint").equals("")) {
                                    String producerName = dependencyPara.getJSONObject(i).getString("producer_endpoint");
                                    String producerMethod = dependencyPara.getJSONObject(i).getString("producer_method");
                                    req.addNewProducer(producerName + "," + producerMethod);
                                    req.updateParaProducerName(dependencyParaArrKey, dependencyPara.getJSONObject(i));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}