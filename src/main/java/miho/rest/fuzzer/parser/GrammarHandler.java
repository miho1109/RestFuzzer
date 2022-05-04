package miho.rest.fuzzer.parser;

import miho.rest.fuzzer.Evaluator;
import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.model.request.MyRequest;
import miho.rest.fuzzer.utility.RestFuzzerUtil;
import miho.rest.fuzzer.compiler.RequestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GrammarHandler {
    private static GrammarHandler grammarHandler;

    public static List<MyRequest> requestCollection;

    public static GrammarHandler getInstance() {
        if (grammarHandler == null) {
            grammarHandler = new GrammarHandler();
        }
        return grammarHandler;
    }

    private InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    private void printFileContent(InputStream is) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            is.close();
        }
    }

    public static boolean isRequestsReady() {
        return requestCollection != null && requestCollection.size() > 0;
    }

    public void getGrammarDict(String filePath) throws JSONException, IOException {
        JSONObject data = RestFuzzerUtil.readDataFromJsonFile(filePath);

        JSONArray reqCollection = data.getJSONArray("Requests");

        RequestHandler requestHandler = new RequestHandler();
        Evaluator evaluator = Evaluator.getInstance();

        requestCollection = new ArrayList<>();

        MyRequest myRequest;
        for (int i = 0; i < reqCollection.length(); i++) {
            JSONObject req = reqCollection.getJSONObject(i);

            myRequest = new MyRequest(
                req.getJSONObject("id").getString("endpoint"),
                RestFuzzerEnum.Method.getMethodFromString(req.getString("method"))
            );

//            System.out.println(req.getJSONObject("id").getString("endpoint") + " " +
//                RestFuzzerEnum.Method.getMethodFromString(req.getString("method")));

            if (requestHandler.isURLContainDynamicObj(req)) {
                myRequest.setDynamicSegmentList(requestHandler.getURLDynamicParas(req));
            }

            if (requestHandler.isHasQueryParaList(req)) {
                myRequest.setQueryPara(requestHandler.getQueryParaList(req));
            }

            if (requestHandler.isHasBodyParaList(req)) {
                myRequest.setBodyPara(requestHandler.getReqBodyParaList(req));
            }

//            for (FuzzablePara para : myRequest.getBodyParaList()) {
//                System.out.println(para.getName() + " " + para.getType() + " " + para.isRequired());
//                System.out.println(para.getClass());
//            }
    
//            System.out.println(RestFuzzerUtil.getReqInfo(myRequest));
            
            requestCollection.add(myRequest);
            evaluator.totalPathInSpec.add(myRequest.getEndpoint());
            evaluator.totalOperPerPath.add(myRequest.getEndpoint() + myRequest.getMethodEnum().name());
        }
    }

}
