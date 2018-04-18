package soot.jbco.name;

/**
 * Implementation that generates names consisting of hard recognizable symbols.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public class JunkNameGenerator extends AbstractNameGenerator implements NameGenerator {

  private static final char stringChars[][] = { { 'S', '5', '$' }, { 'l', '1', 'I' }, { '_' } };

  @Override
  protected char[][] getChars() {
    return stringChars;
  }

}
