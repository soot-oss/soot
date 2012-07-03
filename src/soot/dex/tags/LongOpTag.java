package soot.dex.tags;


import soot.tagkit.Tag;

public class LongOpTag implements Tag {
	public String getName() {
		return "LongOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
