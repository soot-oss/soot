package soot.dex.tags;


import soot.tagkit.Tag;

public class ObjectOpTag implements Tag {
	public String getName() {
		return "ObjectOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
