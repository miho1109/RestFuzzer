package miho.rest.fuzzer.model.fuzzablepara;

import miho.rest.fuzzer.RestFuzzerEnum;

public class FuzzablePara {
    String name;
    RestFuzzerEnum.ParaType ParaType;
    boolean isRequired = false;

    protected FuzzablePara(
        String name,
        RestFuzzerEnum.ParaType ParaType
    ) {
        this.name = name;
        this.ParaType = ParaType;
    }

    protected FuzzablePara(
        String name,
        RestFuzzerEnum.ParaType ParaType,
        boolean isRequired
    ) {
        this.name = name;
        this.ParaType = ParaType;
        this.isRequired = isRequired;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RestFuzzerEnum.ParaType getParaType() {
        return ParaType;
    }

    public void setParaType(RestFuzzerEnum.ParaType ParaType) {
        this.ParaType = ParaType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(String required) {
        if (required.equals("false")) {
            this.isRequired = false;
        } else {
            this.isRequired = true;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
