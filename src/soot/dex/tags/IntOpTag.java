package soot.dex.tags;


import soot.tagkit.Tag;

public class IntOpTag implements Tag {
	public String getName() {
		return "IntOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
