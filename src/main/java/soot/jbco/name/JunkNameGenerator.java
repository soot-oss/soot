package soot.jbco.name;

import soot.jbco.util.Rand;

/**
 * Implementation that generates names consisting of hard recognizable symbols.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public class JunkNameGenerator implements NameGenerator {

    private static final char stringChars[][] = {
            {'S', '5', '$'},
            {'l', '1', 'I'},
            {'_'}
    };

    @Override
    public String generateName(final int size) {
        if (size > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Cannot generate junk name: too long for JVM.");
        }

        final int index = Rand.getInt(stringChars.length);
        final int length = stringChars[index].length;

        char newName[] = new char[size];
        do {
            newName[0] = stringChars[index][Rand.getInt(length)];
        } while (!Character.isJavaIdentifierStart(newName[0]));

        // generate random string
        for (int i = 1; i < newName.length; i++) {
            int rand = Rand.getInt(length);
            newName[i] = stringChars[index][rand];
        }
        return String.valueOf(newName);
    }

}
