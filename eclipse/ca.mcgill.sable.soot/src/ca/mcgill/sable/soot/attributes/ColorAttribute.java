package ca.mcgill.sable.soot.attributes;

import soot.*;
import org.eclipse.swt.graphics.RGB;

public class ColorAttribute{
            
    private int red;
    private int green;
    private int blue;
    private int fg;
    private String type;

    public ColorAttribute(int red, int green, int blue, boolean fg){
        this.red = red;
        this.green = green;
        this.blue = blue;
        if (fg){
            this.fg = 1;
        }
        else {
            this.fg = 0;
        }
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue(){
        return blue;
    }

    public int fg(){
        return fg;
    }

	/**
	 * @return
	 */
	public String type() {
		return type;
	}

	/**
	 * @param string
	 */
	public void type(String string) {
		type = string;
	}

	public RGB getRGBColor(){
		return new RGB(red(), green(), blue());
	}
}
