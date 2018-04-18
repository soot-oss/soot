package soot.jbco.name;

/**
 * Generates names that are compatible with Java identifiers.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public interface NameGenerator {

    /**
     * According to JVM specification, the name is limited to 65535 characters by the 16-bit unsigned length item of
     * the CONSTANT_Utf8_info structure. As the limit is on the number of bytes and UTF-8 encodes some characters using
     * two or three bytes, we assume that in worst case number or characters is 65535 / 3
     */
    int NAME_MAX_LENGTH = 65_535 / 3;

    /**
     * Generates random name of required length that can be used as Java identifier.
     *
     * @param size the expected size
     * @return the name of expected length
     * @throws IllegalArgumentException when passed size is more than {@link NameGenerator#NAME_MAX_LENGTH}
     */
    String generateName(int size);

}
