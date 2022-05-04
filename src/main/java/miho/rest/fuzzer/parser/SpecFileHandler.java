package miho.rest.fuzzer.parser;

import miho.rest.fuzzer.RestFuzzerEnum;
import miho.rest.fuzzer.utility.RestFuzzerUtil;

import java.io.*;

public class SpecFileHandler {
    private static SpecFileHandler specFileHandler;
    
    public static SpecFileHandler getInstance() {
        if (specFileHandler == null) {
            specFileHandler = new SpecFileHandler();
        }
        return specFileHandler;
    }
    
    public void compile(String filePath, RestFuzzerEnum.SpecType specType) throws IOException, InterruptedException {
        switch (specType) {
            case RAML -> {
//                convertRamlToOAS(filePath);
                filePath = "libs/specRaml.json";
            }
            case APIB -> {
                convertApibToOAS(filePath);
                filePath = "libs/specApib.json";
            }
        }

//            Thread.sleep(10000);
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(".libs/restler-bin/restler/Restler.exe compile --api_spec ");
//                stringBuilder.append("\"").append(filePath).append("\"");
//                Process process = new ProcessBuilder("cmd.exe", "/c", stringBuilder.toString()).start();
        String finalFilePath = filePath;
        Thread t = new Thread(() -> {
            try {
                Process process = new ProcessBuilder("libs/restler-bin/restler/Restler.exe", "compile --api_spec " + finalFilePath).start();
                process.waitFor();
                System.out.println("DONE");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }
    
    public void convertRamlToOAS(String filePath) throws InterruptedException, IOException {
        Thread t = new Thread(() -> {
            try {
                Process process = new ProcessBuilder("libs\\ramltooas.exe", filePath).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                StringBuilder rs = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    rs.append(line).append("\n");
                process.waitFor();
                RestFuzzerUtil.writeToFile("libs/specRaml.json", rs.toString());
                notify();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }
    
    //THIS FUNC IS UNSTABLE
    public void convertApibToOAS(String filePath) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("libs\\apib2oas.exe", filePath).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        String line;
        StringBuilder rs = new StringBuilder();
        while ((line = reader.readLine()) != null) {
//            line = line.translateEscapes();
//            System.out.println(line);
//            line = line.replaceAll("'\n'", "");
//            line = line.trim();
//            line = line.replaceAll("'\\n' +", "");
//            line = line.replaceAll("\\+", "");
            rs.append(line).append("\n");
        }
        process.waitFor();
//        System.out.println(rs);
        RestFuzzerUtil.writeToFile("libs\\specApib.json", rs.toString());
    }
}
