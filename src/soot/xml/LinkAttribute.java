package soot.xml;

import soot.*;    

public class LinkAttribute {
    
    private String info;
    private int jimpleLink;
    private int javaLink;
    private String className;
    private boolean isJimpleLink;
    private boolean isJavaLink;
    private String analysisType;
    
    public LinkAttribute(String info, int jimpleLink, int javaLink, String className, String type){
        this.info = info;
        this.jimpleLink = jimpleLink;
        this.javaLink = javaLink;
        this.className = className; 
        isJimpleLink = true;
        isJavaLink = true;
        analysisType = type;
    }

    public String info(){
        return info;
    }

    public int jimpleLink(){
        return jimpleLink;
    }

    public int javaLink(){
        return javaLink;
    }

    public String className(){
        return className;
    }

    public boolean isJimpleLink(){
        return isJimpleLink;
    }

    public boolean isJavaLink(){
        return isJavaLink;
    }

    public String analysisType(){
        return analysisType;
    }
}   

