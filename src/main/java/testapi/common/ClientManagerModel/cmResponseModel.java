package testapi.common.ClientManagerModel;


import java.util.ArrayList;

public class cmResponseModel {

    private  ArrayList<cmCliModel> content;
    private  String totalElements;

    public ArrayList<cmCliModel> getContent() {
        return content;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public void setContent(ArrayList<cmCliModel> content) {
        this.content = content;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }
}
