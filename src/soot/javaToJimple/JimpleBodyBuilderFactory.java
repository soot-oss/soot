package soot.javaToJimple;

public class JimpleBodyBuilderFactory extends AbstractJBBFactory {

    protected AbstractJimpleBodyBuilder createJimpleBodyBuilder(){
        return new JimpleBodyBuilder();
    }

}
