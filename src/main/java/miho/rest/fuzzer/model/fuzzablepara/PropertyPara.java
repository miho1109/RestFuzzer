package miho.rest.fuzzer.model.fuzzablepara;

import miho.rest.fuzzer.RestFuzzerEnum;

import java.util.List;

public class PropertyPara extends FuzzablePara {
    public PropertyPara(String name, boolean isRequired) {
        super(name, RestFuzzerEnum.ParaType.PROPERTY, isRequired);
    }

    private List<FuzzablePara> content;

    public List<FuzzablePara> getContent() {
        return content;
    }

    public void setContent(List<FuzzablePara> content) {
        this.content = content;
    }
}