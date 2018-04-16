package soot.jbco.name;

import soot.jbco.util.Rand;

/**
 * Abstract class that implements {@link NameGenerator#generateName(int)}.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public abstract class AbstractNameGenerator implements NameGenerator {

    @Override
    public String generateName(final int size) {
        if (size > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Cannot generate junk name: too long for JVM.");
        }

        final char[][] chars = getChars();

        final int index = Rand.getInt(chars.length);
        final int length = chars[index].length;

        char newName[] = new char[size];
        do {
            newName[0] = chars[index][Rand.getInt(length)];
        } while (!Character.isJavaIdentifierStart(newName[0]));

        // generate random string
        for (int i = 1; i < newName.length; i++) {
            int rand = Rand.getInt(length);
            newName[i] = chars[index][rand];
        }
        return String.valueOf(newName);
    }

    protected abstract char[][] getChars();

}
