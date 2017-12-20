package formats;

/**
 * On lit des données dans un format et on retourne un KV.
 *
 * @author  William Attache and Tom Ragonneau
 * @version 1.0
 */
public interface FormatReader {

  /**
   * Lecture des donnees.
   *
   * @return Un KV de lecture.
   */
  KV read();

}