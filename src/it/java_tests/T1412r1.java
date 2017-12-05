
class T1412r1 {
    static int i = -2;
    public static void main(String[] args) {
	do
            test();
	while (false);
	System.out.print(i);
    }
    static boolean test() {
	try {
	    do {
		if (i++ == 0)
		    return true;
	    } while (true);
        } finally {
	    i++;
	}
    }
}
    