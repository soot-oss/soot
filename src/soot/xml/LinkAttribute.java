package soot.xml;

import soot.*;    

public class LinkAttribute {
    
    private String info;
    private int jimpleLink;
    private int javaLink;
    private String className;
    private boolean isJimpleLink;
    private boolean isJavaLink;
    
    /*public LinkAttribute(String info, int jimpleLink, String className){
        this.info = info;
        this.jimpleLink = jimpleLink;
        this.className = className; 
        isJimpleLink = true;
    }

    public LinkAttribute(String info, int javaLink, String className){
        this.info = info;
        this.javaLink = javaLink;
        this.className = className;
        isJavaLink = true;
    }*/

    public LinkAttribute(String info, int jimpleLink, int javaLink, String className){
        this.info = info;
        this.jimpleLink = jimpleLink;
        this.javaLink = javaLink;
        this.className = className; 
        isJimpleLink = true;
        isJavaLink = true;
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
}   

