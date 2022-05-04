package miho.rest.fuzzer;

import miho.rest.fuzzer.parser.DependencyHandler;
import miho.rest.fuzzer.fuzzer.FuzzHandler;
import miho.rest.fuzzer.parser.GrammarHandler;
import org.json.JSONException;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws InterruptedException, IOException, JSONException {
//        SpecFileHandler specFileHandler = SpecFileHandler.getInstance();
//        specFileHandler.compile("DigitalDeltaEcoDiscovery.raml", RestFuzzerEnum.SpecType.RAML);
        
        GrammarHandler grammarHandler = GrammarHandler.getInstance();
        grammarHandler.getGrammarDict("D:\\My Desktop\\API Testing\\Restler\\Compile\\grammar.json");

        DependencyHandler dependencyHandler = DependencyHandler.getInstance();
        dependencyHandler.getDependency("D:\\My Desktop\\API Testing\\Restler\\Compile\\dependencies.json");
        dependencyHandler.updateReqDependency();

        FuzzHandler fuzzHandler = FuzzHandler.getInstance();
        fuzzHandler.prepareFuzzing("https://api.botify.com/v1/", "50");
    }
}
