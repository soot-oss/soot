package soot.tagkit;

public class KeyTag implements Tag {

    private int red;
    private int green;
    private int blue;
    private String key;
    
    public KeyTag(int r, int g, int b, String k){
        red = r;
        green = g;
        blue = b;
        key = k;
    }

    public KeyTag(int color, String k){
        switch(color){
			case ColorTag.RED: {
				red = 255;
				green = 0;
				blue = 0;
				break;
			}
			case ColorTag.GREEN: {
				red = 45;
				green = 255;
				blue = 84;
				break;
			}
			case ColorTag.YELLOW: {
				red = 255;
				green = 248;
				blue = 35;
				break;
			}
			case ColorTag.BLUE: {
				red = 174;
				green = 210;
				blue = 255;
				break;
			}
			case ColorTag.ORANGE: {
				red = 255;
				green = 163;
				blue = 0;
				break;
			}
			case ColorTag.PURPLE: {
				red = 159;
				green = 34;
				blue = 193;
				break;
			}
			default: {
				red = 220;
				green = 220;
                blue = 220;
                break;
            }
                     
        }
        key = k;
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue() {
        return blue;
    }

    public String key(){
        return key;
    }

    public String getName(){
        return "KeyTag";
    }

    public byte[] getValue() {
        byte[] v = new byte[4];
        return v;
    }
    
}   
