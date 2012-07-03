package soot.dex.tags;


import soot.tagkit.Tag;

public class FloatOpTag implements Tag {
	public String getName() {
		return "FloatOpTag";
	}

	public byte[] getValue () {
		byte[] b = new byte[1];
		b[0] = 0;
		return b;
	}

}
