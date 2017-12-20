package formats;

import java.io.Serializable;

/**
 * Permet d'ouvrir, lire, ecrire ou fermer des fichiers dans les differents
 * formats.
 *
 * @author  William Attache and Tom Ragonneau
 * @version 1.0
 */
public interface Format extends FormatReader, FormatWriter, Serializable {

  /**
   * Les differents types de format.
   */
  enum Type {LINE, KV};

  /**
   * Les modes d'ouverture de fichier.
   */
  enum OpenMode {R, W};



  /**
   * Ouvre le fichier. Le cree s'il n'existe pas.
   *
   * @param mode Le mode d'ouverture.
   */
  void open(OpenMode mode);

  /**
   * Ferme le fichier.
   */
  void close();

  /**
   * Retourne l'index.
   *
   * @return L'index
   */
  long getIndex();

  /**
   * Retourne le nom du fichier.
   *
   * @return Le nom du fichier
   */
  String getFname();

  /**
   * Modifie le nom du fichier.
   *
   * @param fname Le nom du fichier
   */
  void setFname(String fname);

Object getBufferedReader();

}