interface Percentage {
    public double calcPercentage( double value);
}

public class MethodAcceptingLamExpr {

    public void lambdaAsParamMethod(){
        Percentage percentageValue = (value -> value/100);
        System.out.println("Percentage : " + percentageValue.calcPercentage(45.0));
    }
}

