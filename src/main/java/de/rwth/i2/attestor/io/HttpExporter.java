package de.rwth.i2.attestor.io;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by christina on 18.01.18.
 */
public class HttpExporter {

    protected static final Logger logger = LogManager.getLogger("HttpExporter");

    HttpClient httpclient = HttpClientBuilder.create().build();

    /**
     * A POST request is send, that registers the currently running benchmark.
     * @param id the identifier of the benchmark
     * @param bName the name of the benchmark
     * @throws UnsupportedEncodingException
     */
    public void sendBenchmarkRegisterRequest(int id, String bName) throws UnsupportedEncodingException {

        HttpPost httppost = new HttpPost("http://localhost:9200/benchmark");
        httppost.addHeader("content-type", "application/json");

        // Make sure a name is set, if none available, use the benchmark identifier
        if(bName.isEmpty()){
            bName = String.valueOf(id);
        }
        // Request parameters
        StringEntity params = new StringEntity("{\"id\":\"" + id + "\",\"name\":\"" + bName + "\"} ");
        httppost.setEntity(params);

        //Execute
        try {
            httpclient.execute(httppost);
        } catch (IOException e) {
            logger.warn("Not able to register benchmark with the API.");
        }
    }

    /**
     * A POST request is send, that adds a messages log.
     * @param bid the identifier of the benchmark
     * @param level the type of the message
     * @param message the message content
     * @throws UnsupportedEncodingException
     */
    public void sendMessageRequest(int bid, String level, String message) throws UnsupportedEncodingException {

        HttpPost httppost = new HttpPost("http://localhost:9200/benchmark/messages?bid=" + bid);
        httppost.addHeader("content-type", "application/json");

        // Request parameters
        StringEntity params = new StringEntity("{\"level\":\"" + level + "\",\"message\":\"" + message + "\"} ");
        httppost.setEntity(params);

        //Execute
        try {
            httpclient.execute(httppost);
        } catch (IOException e) {
            logger.warn("Not able to register benchmark with the API.");
        }
    }

    public void sendSummaryRequest(int bid, String summary) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorOutput/analysisSummary.json", summary);
    }

    public void sendOptionsRequest(int bid, String options) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/options.json", options);
    }

    public void sendSummaryInitialHCsRequest(int bid, String initialHCs) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/initialHCsSummary.json", initialHCs);
    }

    public void sendInitialHCRequest(int bid, int hcID, String initialHC) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/hc_" + hcID + ".json", initialHC);
    }

    public void sendGrammarSummaryRequest(int bid, String grammarSummary)throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/grammarData/grammarSummary.json", grammarSummary);
    }

    public void sendRuleHCRequest(int bid, String ruleName, String hcJson) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/grammarData/" + ruleName, hcJson);
    }

    public void sendStateSpaceSummaryRequest(int bid, String statespaceSummary) throws UnsupportedEncodingException{
        putRequest("http://localhost:9200/benchmark" + bid + "/statespaceData/statespaceSummary.json", statespaceSummary);
    }

    public void sendStateSpaceHCRequest(int bid, int hcId, String hc) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/statespaceData/hc_" + hcId + ".json", hc);
    }

    public void sendCounterexampleSummaryRequest(int bid, int formulaID, String counterexampleSummary) throws UnsupportedEncodingException{
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorOutput/counterex" + formulaID + "/counterexampleSummary.json", counterexampleSummary);
    }

    public void sendCounterexampleConcreteHCRequest(int bid, int formulaID, String counterexampleSummary) throws UnsupportedEncodingException{
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorOutput/counterex" + formulaID + "/concreteHC.json", counterexampleSummary);
    }

    public void sendCounterexampleHCRequest(int bid,int formulaID, int hcId, String hc) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorOutput/counterex" + formulaID + "/hc_" + hcId + ".json", hc);
    }

    public void sendSettingsFileRequest(int bid, String settings) throws UnsupportedEncodingException {
        //putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/settings.json", settingsFile);

        putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/settings.json", settings);
    }

    public void sendProgramFileRequest(int bid, String program) throws UnsupportedEncodingException {
        putRequest("http://localhost:9200/benchmark" + bid + "/attestorInput/analysedClass.json", program);
    }

    private void putRequest(String url, File file) {
        HttpPut httpput = new HttpPut(url);
        httpput.addHeader("content-type", "application/json");

        // Creating entity from given file
        FileBody fileBody = new FileBody(file, ContentType.APPLICATION_JSON);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        /* Setting the HttpMultipartMode */
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addPart(file.getName(), fileBody);

        HttpEntity entity = builder.build();

        httpput.setEntity(entity);

        //Execute
        try {
            httpclient.execute(httpput);
        } catch (IOException e) {
            logger.warn("Not able to register benchmark with the API.");
        }
    }


    private void putRequest(String url,String contents) throws UnsupportedEncodingException {

        HttpPut httpput = new HttpPut(url);
        httpput.addHeader("content-type", "application/json");

        // Request parameters
        StringEntity params = new StringEntity(contents);
        httpput.setEntity(params);

        //Execute
        try {
            httpclient.execute(httpput);
        } catch (IOException e) {
            logger.warn("Not able to register benchmark with the API.");
        }


    }

}
