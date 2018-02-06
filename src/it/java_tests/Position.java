public class Position {

    int line;
    int col;
    
    public Position(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public int line(){
        return line;
    }
    public int col(){
        return col;
    }
    public static void main(String [] args){
        Position p = new Position(3, 4);
    }
}
