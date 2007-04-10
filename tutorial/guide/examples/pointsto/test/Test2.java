public class Test2 {

	public void go() {
		Container c1 = new Container();
		Item i1 = new Item();
		c1.setItem(i1);
		
		Container c2 = new Container();
		Item i2 = new Item();
		c2.setItem(i2);	
		
		Container c3 = new Container();
		Item i3;
		if ("1".equals(new Integer(1).toString()))
			i3 = i1;
		else
			i3 = i2;
		c3.setItem(i3);	
	}
	
	public static void main(String[] args) {
		new Test2().go();
	}

}
