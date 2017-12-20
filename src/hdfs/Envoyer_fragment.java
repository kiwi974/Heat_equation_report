package hdfs;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Permet d'envoyer un fragment.
 *
 * @author  William Attache and Tom Ragonneau
 * @version 1.0
 */
class Envoyer_fragment extends Thread {

  /**
   * Le socket d'envoi.
   */
  private Socket socket;

  /**
   * L'extrait a envoyer.
   */
  private String extrait;

  /**
   * Le nom du fichier.
   */
  private String fileName;

  /**
   * La commande.
   */
  private String commande;

  /**
   * Le type du fichier.
   */
  private int type;



  /**
   * Construire un envoi de fragment.
   *
   * @param s Le socket d'envoi.
   * @param fragment Le fragment a envoyer.
   * @param fileName Le nom du fichier.
   * @param commande La commande.
   * @param tp Le type du fichier.
   */
  public Envoyer_fragment(Socket s, String fragment, String fileName,
                          String commande, String tp) {
    this.socket = s;
    this.fileName = fileName;
    this.commande = commande;
    this.type = Integer.parseInt(tp);
    this.extrait = this.commande + "<->" + this.fileName + "<->" + fragment
            + "<->" + this.type;
  }



  /**
   * L'execution de l'envoi.
   */
  @Override
  public void run(){
    try{
      int tache = 0;

      while(tache != 1){

        OutputStreamWriter osw;
        osw = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pred = new PrintWriter(bw, true);
        pred.println(this.extrait);

        tache = 1;
      }
    } catch (Exception e) {
      System.err.print("Echec de l'envoi...");
    }
  }
}
