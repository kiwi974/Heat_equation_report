package formats;

/**
 * On donne une KV et on écrit dans un format.
 *
 * @author  William Attache and Tom Ragonneau
 * @version 1.0
 */
public interface FormatWriter {

  /**
   * Ecrire dans un format.
   *
   * @param record le fichier de KV.
   */
  void write(KV record);

}