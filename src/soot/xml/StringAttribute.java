package soot.xml;

public class StringAttribute {

    private String info;
    private String analysisType;
    
    public StringAttribute(String info, String type){
        this.info = info;
        analysisType = type;
    }
    
    public String info(){
        return info;
    }

    public String analysisType(){
        return analysisType;
    } 
}
