package miho.rest.fuzzer.model.fuzzablepara;

import miho.rest.fuzzer.RestFuzzerEnum;

import java.util.List;
import java.util.Random;

public class Parameter extends FuzzablePara {
    private String producerName;
    private List<String> valueRange;
    private RestFuzzerEnum.ValueType valueType;

    public Parameter(
        String name,
        RestFuzzerEnum.ValueType valueType
    ) {
        super(name, RestFuzzerEnum.ParaType.PARAMETER, false);
        this.valueType = valueType;
        this.producerName = "";
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public RestFuzzerEnum.ValueType getValueType() {
        return valueType;
    }

    public void setValueType(RestFuzzerEnum.ValueType valueType) {
        this.valueType = valueType;
    }

    public void setValueRange(List<String> valueRange) {
        this.valueRange = valueRange;
    }

    public List<String> getValueRange() {
        return valueRange;
    }

    public boolean isHasValueRange() {
        return valueRange == null;
    }

    public String getRandValueInRange() {
        Random rand = new Random();
        return valueRange.get(rand.nextInt(valueRange.size()));
    }
}
