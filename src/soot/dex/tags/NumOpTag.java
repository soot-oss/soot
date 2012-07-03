package soot.dex.tags;


import soot.tagkit.Tag;

public class NumOpTag implements Tag {
	public String getName() {
		return "NumOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
