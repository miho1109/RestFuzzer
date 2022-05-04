package miho.rest.fuzzer.model.fuzzablepara;

import miho.rest.fuzzer.RestFuzzerEnum;

import java.util.List;

public class ObjectPara extends FuzzablePara {
    public ObjectPara() {
        super("", RestFuzzerEnum.ParaType.OBJECT);
    }

    private List<FuzzablePara> content;

    public List<FuzzablePara> getContent() {
        return content;
    }

    public void setContent(List<FuzzablePara> content) {
        this.content = content;
    }
}
