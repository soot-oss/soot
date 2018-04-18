package soot.jbco.name;

/**
 * Implementation that generates names consisting of hard recognizable symbols.
 *
 * @author p.nesterovich
 * @since 21.03.18
 */
public class HorrorNameGenerator extends AbstractNameGenerator implements NameGenerator {

  private static final char stringChars[][] = { { 'S', '5', '$' }, // latin s, five, dollar sign
      { 'l', '1', 'I', 'Ι' }, // l, one, I, greek iota
      { '0', 'O', 'О', 'Ο', 'Օ' }, // zero, english O, russian O, greek omicron, armenian Օ
      { 'o', 'о', 'ο' }, // english o, russian o and greek omicron
      { 'T', 'Т', 'Τ' }, // english T, russian T, greek tau
      { 'H', 'Н', 'Η' }, // english, russian, greek
      { 'E', 'Е', 'Ε' }, // english, russian, greek
      { 'P', 'Р', 'Ρ' }, // english, russian, greek
      { 'B', 'В', 'Β' }, // english, russian, greek
      { '_' } };

  @Override
  protected char[][] getChars() {
    return stringChars;
  }

}
