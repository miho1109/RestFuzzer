package miho.rest.fuzzer.fuzzer;

import miho.rest.fuzzer.Evaluator;
import miho.rest.fuzzer.model.request.MyRequest;
import miho.rest.fuzzer.utility.RestFuzzerUtil;
import miho.rest.fuzzer.parser.DependencyHandler;
import miho.rest.fuzzer.parser.GrammarHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class FuzzHandler {
    private static FuzzHandler fuzzHandler;
    
    public static FuzzHandler getInstance() {
        if (fuzzHandler == null) {
            fuzzHandler = new FuzzHandler();
        }
        return fuzzHandler;
    }

    public int MAX_FUZZ_SECOND = 50;
    public int FUZZ_INTERVAL = 1;

    private List<List<MyRequest>> sequenceSet = new ArrayList<>();
    private final List<MyRequest> requestSet = GrammarHandler.requestCollection;
    private Map<String, JSONObject> responseSet = new HashMap<>();
    public static StringBuilder errList = new StringBuilder();
    
    HttpHandler httpHandler = HttpHandler.getInstance();
    
    public void prepareFuzzing(String url, String fuzzTime) {
        httpHandler.setupURL(url);
        this.MAX_FUZZ_SECOND = Integer.parseInt(fuzzTime);
        if (GrammarHandler.isRequestsReady() && DependencyHandler.isDependenciesReady()) {
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int i = MAX_FUZZ_SECOND;

                public void run() {
                    i--;
//                    System.out.println(i);
//                    uniqueRandWalk();
                    randWalk();
                    //                    RestFuzzerUtil.printRequestSeq(sequenceSet);
                    if (i < 0) {
                        timer.cancel();
                        try {
                            startFuzz();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 0, MAX_FUZZ_SECOND);
        }
    }

    private void startFuzz() throws IOException {
        int MIN_SEQ_LEN = requestSet.size();
        for (int i = 0; i < FUZZ_INTERVAL; i++) {
            System.out.println("FUZZ TIME (sec):" + i);
            System.out.println("SEQUENCE LENGTH:" + MIN_SEQ_LEN);
            List<List<MyRequest>> tmpSeq = new ArrayList<>(List.copyOf(sequenceSet));
            int seqNum = 0;
            for (List<MyRequest> seq : sequenceSet) {
                System.out.println("-----------------------------------------------------");
                try {
                    if (!executeReqSeq(seq)) tmpSeq.remove(seq);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                System.out.println("----------------------------------------------");
                seqNum++;
            }
            MIN_SEQ_LEN++;
        }
        RestFuzzerUtil.writeToFile("errList", errList.toString());
        Evaluator.getInstance().printStatistics();
        uniqueRandWalk();
    }

    private void uniqueRandWalk() {
        List<MyRequest> newReqSet = new ArrayList<>();
        List<MyRequest> tmpRequests = new ArrayList<>(List.copyOf(requestSet));
        for (int i = 0; i < requestSet.size(); i++) {
            MyRequest randReq = tmpRequests.get(new Random().nextInt(tmpRequests.size()));
            if (isReqValid(randReq, newReqSet)) {
                newReqSet.add(randReq);
                tmpRequests.remove(randReq);
            }
        }
        if (!newReqSet.isEmpty() && !sequenceSet.contains(newReqSet)) sequenceSet.add(newReqSet);
    }

    private void randWalk() {
        List<MyRequest> newReqSet = new ArrayList<>();
        for (int i = 0; i < requestSet.size(); i++) {
            MyRequest randReq = requestSet.get(new Random().nextInt(requestSet.size()));
            if (isReqValid(randReq, newReqSet)) {
                newReqSet.add(randReq);
            }
        }
        if (!newReqSet.isEmpty() && !sequenceSet.contains(newReqSet)) sequenceSet.add(newReqSet);
    }

    private boolean isReqValid(MyRequest req, List<MyRequest> preReqSeq) {
        if (req.getProducerReqSet().size() == 0) {
            return true;
        } else {
            if (preReqSeq.size() == 0) {
                return false;
            } else {
                int count = 0;
                for (String producer : req.getProducerReqSet()) {
                    for (MyRequest preReq : preReqSeq) {
                        if (preReq.getEndpoint().equals(req.getProducerName(producer)) &&
                            preReq.getMethodEnum().equals(req.getProducerMethod(producer))) {
                            count++;
                        }
                    }
                }
                return count == req.getProducerReqSet().size();
            }
        }
    }

    private boolean executeReqSeq(List<MyRequest> reqSeq) throws JSONException, IOException {
        responseSet = new HashMap<>();
    
        if (reqSeq != null && reqSeq.size() > 0) {
            for (MyRequest req : reqSeq) {
                if (httpHandler.makeRequest(req, responseSet)) return false;
            }
        }
        return true;
    }
}
