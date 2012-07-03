package soot.dex.tags;


import soot.tagkit.Tag;

public class DoubleOpTag implements Tag {
	public String getName() {
		return "DoubleOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
