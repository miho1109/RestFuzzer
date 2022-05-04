package miho.rest.fuzzer;

import groovyjarjarantlr4.runtime.tree.Tree;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Evaluator {
    private static Evaluator evaluator;
    
    public static Evaluator getInstance() {
        if (evaluator == null) evaluator = new Evaluator();
        return evaluator;
    }
    public Set<String> totalPathInSpec = new HashSet<>();
    public Set<String> fuzzedPathSet = new HashSet<>();
    
    public Set<String> totalOperPerPath = new HashSet<>();
    public Set<String> fuzzedOperPerPath = new HashSet<>();
    
    public Set<String> statusCodePerOper = new HashSet<>();
    
    public int numOf5xxErr = 0;
    public int numOf4xxErr = 0;
    
    public int totalReq = 0;
    
    public void printStatistics() {
        System.out.println("Total request has made: " + totalReq);
        System.out.println("Path coverage: " + getPathCoveragePercent() + "%");
        System.out.println("Operation coverage: " + getOperationCoveragePercent() + "%");
        printStatusCodeCoverage();
        System.out.println("Number of 5xx code: " + numOf5xxErr);
        System.out.println("Number of 4xx code: " + numOf4xxErr);
        System.out.println("See detail in errList file.");
    }
    
    private double getPathCoveragePercent() {
        double rs = (double) fuzzedPathSet.size() / totalPathInSpec.size();
        return Double.parseDouble(new DecimalFormat("###.#").format(rs*100));
    }
    
    private double getOperationCoveragePercent() {
        double rs = (double) fuzzedOperPerPath.size()/totalOperPerPath.size();
        return Double.parseDouble(new DecimalFormat("###.#").format(rs*100));
    }
    
    private void printStatusCodeCoverage() {
        Object[] tmp = new TreeSet<>(statusCodePerOper).toArray();
    
        double percent = 1;
        int [] fr = new int [tmp.length];
        int visited = -1;
        for(int i = 0; i < tmp.length; i++){
            int count = 1;
            String str = tmp[i].toString().substring(0, tmp[i].toString().length()-4);
            for(int j = i+1; j < tmp.length; j++){
                String str1 = tmp[j].toString().substring(0, tmp[j].toString().length()-4);
                if(str.equals(str1)){
                    count++;
                    fr[j] = visited;
                }
            }
            if(fr[i] != visited) {
                fr[i] = count;
            }
        }
    
        for(int i = 0; i < fr.length; i++){
            if(fr[i] != visited) {
//                System.out.println("    " + tmp[i] + "    |    " + fr[i]);
                percent += ((double) fr[i] /3);
            }
        }
        
        percent = Double.parseDouble(new DecimalFormat("###.#").format((percent/tmp.length)*100));
    
        System.out.println("Status code coverage: " + percent + "%");
        for (Object o : tmp) {
            System.out.println(o.toString());
        }
    
    }
    
}
