package miho.rest.fuzzer;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.logging.log4j.util.Strings;

public class RestFuzzerSampler extends AbstractSampler {

    private static final String RESPONSE_TIME_PROPERTY = "Dummy.responseTime";
    private static final String LABEL = "Dummy.label";
    private static final String RESPONSE_CODE = "Dummy.responseCode";
    private static final String SUCCESS = "Dummy.Success";

    @Override
    public SampleResult sample(Entry entry) {
        return null;
    }

    public void setResponseTime(int responseTime) {
        setProperty(RESPONSE_TIME_PROPERTY, responseTime);
    }

    public Integer getResponseTime() {
        return getPropertyAsInt(RESPONSE_TIME_PROPERTY, 1000);
    }

    public void setLabel(String label) {
        setProperty(LABEL, label);
    }

    public String getLabel() {
        return getPropertyAsString(LABEL, Strings.EMPTY);
    }

    public void setResponseCode(String responseCode) {
        setProperty(RESPONSE_CODE, responseCode);
    }

    public String getResponseCode() {
        return getPropertyAsString(RESPONSE_CODE, "200");
    }

    public void setSuccessful(boolean success) {
        setProperty(SUCCESS, success);
    }

    public boolean getSuccessful() {
        return getPropertyAsBoolean(SUCCESS, true);
    }
}
