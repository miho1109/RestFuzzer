package miho.rest.fuzzer.model.fuzzablepara;

import miho.rest.fuzzer.RestFuzzerEnum;

import java.util.List;

public class ArrayPara extends FuzzablePara {
    public ArrayPara(String name, boolean isRequired) {
        super(name, RestFuzzerEnum.ParaType.ARRAY, isRequired);
    }

    private List<FuzzablePara> content;

    public List<FuzzablePara> getContent() {
        return content;
    }

    public void setContent(List<FuzzablePara> content) {
        this.content = content;
    }
}
