package miho.rest.fuzzer.fuzzer;

import groovy.util.Eval;
import miho.rest.fuzzer.Evaluator;
import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.model.fuzzablepara.*;
import miho.rest.fuzzer.model.request.MyRequest;
import miho.rest.fuzzer.utility.RestFuzzerUtil;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHandler {
    private static HttpHandler httpHandler;
    
    private final OkHttpClient.Builder client = new OkHttpClient.Builder();
    
    MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String scheme = "http";
    private String host = "";
    private int port;
    private ArrayList<String> segmentList;
    
    public static HttpHandler getInstance() {
        if (httpHandler == null) {
            httpHandler = new HttpHandler();
        }
        return httpHandler;
    }
    
    public void setupURL(String url) {
        if (url.contains("https")) {
            scheme = "https";
            host = url.substring(url.indexOf("https") + 8, url.indexOf("/", url.indexOf("https") + 8));
        } else {
            host = url.substring(url.indexOf("http") + 7, url.indexOf("/", url.indexOf("http") + 7));
        }
        
        String segments = url.substring(scheme.length() + 3 + host.length());
        Matcher m = Pattern.compile("/\\w{1,}").matcher(segments);
        segmentList = new ArrayList<>();
        while (m.find()) {
            String a = m.group();
            segmentList.add(a.replace("/", ""));
        }
        
        if (host.contains(":")) {
            port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
            host = host.replace(host.substring(host.indexOf(":")), "");
        }
    }
    
    private HttpUrl.Builder getUrlBuilder() {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
        urlBuilder
            .scheme(this.scheme)
            .host(this.host);
        if (this.port != 0) urlBuilder.port(port);
        if (segmentList != null && !segmentList.isEmpty()) {
            for (String seg : segmentList) {
                urlBuilder.addPathSegments(seg);
            }
        }
        return urlBuilder;
    }
    
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
    
    private void authenticate() throws UnsupportedEncodingException {
        String username = "minhhoan119@gmail.com";
        String password = "helloMiho119";
        
        client.authenticator((route, response) -> {
            if (responseCount(response) >= 3) {
                return null;
            }
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder().header("Authorization", credential).build();
        });
        client.connectTimeout(20, TimeUnit.SECONDS);
        client.writeTimeout(10, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
    }
    
    public boolean makeRequest(MyRequest req, Map<String, JSONObject> responseSet) throws IOException, JSONException {
//        authenticate();
        HttpUrl.Builder urlBuilder = getUrlBuilder();
        
        urlBuilder.addPathSegments(configURL(req, responseSet));
        
        configQueryPara(urlBuilder, req, responseSet);
//        urlBuilder.addQueryParameter("access_token", "glpat-zzWVyo2f7b_icYdabUS4");
        
        RequestBody body = configBody(req, responseSet);
        
        Request.Builder reqBuilder = new Request.Builder()
            .method(req.getMethodEnum().name(), body)
            .url(urlBuilder.build())
//            .header("PRIVATE-TOKEN", "glpat-zzWVyo2f7b_icYdabUS4")
            ;
        
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

//        System.out.print(reqBuilder.build().headers());
//        if (body!=null) {
//            final Buffer buffer = new Buffer();
//            reqBuilder.build().body().writeTo(buffer);
//            System.out.println(buffer.readUtf8());
//        }
        
        try (Response response = client.build().newCall(reqBuilder.build()).execute()) {
//            System.out.println(req.getEndpoint() + " " + req.getMethodEnum().name());
            System.out.println(response + " " + req.getMethodEnum());
            Evaluator evaluator = Evaluator.getInstance();
            evaluator.totalReq += 1;
            evaluator.fuzzedPathSet.add(req.getEndpoint());
            evaluator.fuzzedOperPerPath.add(req.getEndpoint() + req.getMethodEnum().name());
            evaluator.statusCodePerOper.add(req.getMethodEnum().name() + " " + req.getEndpoint() + " " + response.code());
            if (!response.isSuccessful()
//                && !FuzzHandler.errList.toString().contains(RestFuzzerUtil.getReqInfo(req))
            ) {
                Matcher m1 = Pattern.compile("5..").matcher(String.valueOf(response.code()));
                Matcher m2 = Pattern.compile("4..").matcher(String.valueOf(response.code()));
                
                if (m1.find()) evaluator.numOf5xxErr += 1;
                if (m2.find()) evaluator.numOf4xxErr += 1;
                FuzzHandler.errList.append(RestFuzzerUtil.getReqInfo(req));
                FuzzHandler.errList.append(response).append("\n");
                return false;
            }
            
            String res = response.body().string();
            if (!res.contains("null") && !res.isEmpty()) {
                String resKey = req.getEndpoint() + "," + req.getMethodEnum().name();
//                System.out.println(res);
                JSONObject jsonBody = new JSONObject(res);
                responseSet.put(resKey, jsonBody);
            }
//            Headers responseHeaders = response.headers);
//            for (int i = 0; i < responseHeaders.size(); i++) {
//                System.out.println(responseHeaders.name(i) + ": " +
//                    responseHeaders.value(i));
//            }

//            System.out.println("Unexpected code " + response + " " + req.getMethodEnum());
//            System.out.println(response.code() + "\n" + response.body().string());
//            System.out.println(new JSONObject(response.body().string()));
            return true;
        }
    }
    
    private String configURL(MyRequest req, Map<String, JSONObject> responseSet) throws JSONException {
        String url = req.getEndpoint().replaceFirst("/", "");
        if (req.getDynamicSegmentList() != null) {
            for (Parameter para : req.getDynamicSegmentList()) {
                url = url.replaceAll("\\{" + para.getName() + "}",
                    getFuzzValue(req, para, responseSet));
            }
        }
        return url;
    }
    
    private void configQueryPara(HttpUrl.Builder urlBuilder, MyRequest req, Map<String, JSONObject> responseSet) throws JSONException {
        if (req.getQueryPara() != null) {
            for (Parameter para : req.getAllParameter(req.getQueryPara())) {
                if (para.isRequired() || (!para.isRequired() && new Random().nextBoolean())) {
                    urlBuilder.addQueryParameter(
                        para.getName(),
                        getFuzzValue(req, para, responseSet)
                    );
                }
            }
        }
    }
    
    StringBuilder stringBuilder = null;
    
    @Nullable
    private RequestBody configBody(MyRequest req, Map<String, JSONObject> responseSet) throws JSONException {
        RequestBody body = null;
        if (req.getBodyPara() != null) {
            stringBuilder = new StringBuilder();
            StringBuilder json = getBodyInString(req.getBodyPara(), req, responseSet);
            json = new StringBuilder(json.substring(0, json.lastIndexOf(",")));
            body = RequestBody.create(json.toString(), JSON);
//            System.out.println(json + "\n");
        }
        
        if (req.getMethodEnum().equals(RestFuzzerEnum.Method.POST) || req.getMethodEnum().equals(RestFuzzerEnum.Method.PUT)) {
            if (body == null) {
                body = RequestBody.create("{}", JSON);
            }
        }
        return body;
    }
    
    private StringBuilder getBodyInString(FuzzablePara para, MyRequest req, Map<String, JSONObject> responseSet) throws JSONException {
        switch (para.getParaType()) {
            case PROPERTY -> {
                PropertyPara prop = (PropertyPara) para;
                stringBuilder.append((!Objects.equals(prop.getName(), "")) ? prop.getName() + ": " : "").append("{").append("\n");
                for (FuzzablePara par : prop.getContent()) {
                    getBodyInString(par, req, responseSet);
                }
                if (stringBuilder.indexOf(",") != -1)
                    stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
                stringBuilder.append("},").append("\n");
            }
            case OBJECT -> {
                ObjectPara obj = (ObjectPara) para;
                stringBuilder.append(" {").append("\n");
                for (FuzzablePara par : obj.getContent()) {
                    getBodyInString(par, req, responseSet);
                }
                if (stringBuilder.indexOf(",") != -1)
                    stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
                stringBuilder.append("\n").append(" },").append("\n");
            }
            case ARRAY -> {
                ArrayPara array = (ArrayPara) para;
                stringBuilder.append((!Objects.equals(array.getName(), "")) ? '"' + array.getName() + '"' + ": " : "").append("[").append("\n");
                for (FuzzablePara par : array.getContent()) {
                    getBodyInString(par, req, responseSet);
                }
                if (stringBuilder.indexOf(",") != -1)
                    stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.lastIndexOf(",")));
                stringBuilder.append("\n").append("],").append("\n");
            }
            default -> {
                Parameter parameter = (Parameter) para;
                if (parameter.isRequired() || (!parameter.isRequired() && new Random().nextBoolean())) {
                    String value = getFuzzValue(req, parameter, responseSet);
                    if (parameter.getValueType() == RestFuzzerEnum.ValueType.STRING) {
                        value = '"' + value + '"';
                    }
                    stringBuilder.append((!Objects.equals(para.getName(), "")) ? '"' + para.getName() + '"' + ": " : "")
                        .append(value).append(",").append("\n");
                }
            }
        }
        return stringBuilder;
    }
    
    private String getFuzzValue(MyRequest req, Parameter para, Map<String, JSONObject> responseSet) throws JSONException {
        if (req.getProducerReqSet() != null && req.getProducerReqSet().size() != 0 && !responseSet.isEmpty()) {
            for (String producerKey : req.getProducerReqSet()) {
                if (responseSet.containsKey(producerKey)) {
                    JSONObject responseBody = responseSet.get(producerKey);
                    if (responseBody.has(para.getProducerName())) {
                        return responseBody.getString(para.getProducerName());
                    }
                }
            }
        }
        if (!para.isHasValueRange()) return para.getRandValueInRange();
        return RestFuzzerUtil.getRandomValueFromType(para.getValueType());
    }
    
}




















