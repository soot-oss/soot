package soot.defaultInterface;

public class SampleImpl extends SampleAbstract implements SampleInterface {
	
	public static void main(String[] args) {
		SampleImpl sampleImpl = new SampleImpl();
		String textRecieved = sampleImpl.getText("Sample Method Check");
		System.out.println("The recieved Text is " + textRecieved);
	}

}
