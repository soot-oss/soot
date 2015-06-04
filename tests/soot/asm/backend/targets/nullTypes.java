package soot.asm.backend.targets;

/**
 * @author Tobias Hamann
 */
public class nullTypes {

    Integer doStuff(Integer i) {
        if (i == null) {
            return null;
        }
        return 1;
    }

}
