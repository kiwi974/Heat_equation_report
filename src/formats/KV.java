package formats;

/**
 * Le type des KV.
 *
 * @author  William Attache and Tom Ragonneau
 * @version 1.0
 */
public class KV {

  /**
   * Le separateur.
   */
  public static final String SEPARATOR = "<->";

  /**
   * La clef.
   */
  public String k;

  /**
   * La valeur.
   */
  public String v;



  /**
   * Construire une KV sans arguments.
   */
  public KV() { }

  /**
   * Constuire une KV avec une clef et une valeur.
   *
   * @param k La clef.
   * @param v La valeur.
   */
  public KV(String k, String v) {
    super();
    this.k = k;
    this.v = v;
  }

  /**
   * Affichage d'une KV.
   * @return
   */
  public String toString() {
    return "KV [k=" + k + ", v=" + v + "]";
  }

}