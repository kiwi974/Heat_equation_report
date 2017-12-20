package hdfs;

import java.io.*;
import java.net.*;
import java.time.format.FormatStyle;

import formats.Format;
import formats.FormatImpl;
import formats.KV;

/**
 * Le serveur.
 *
 * @author William Attache and Tom Ragonneau
 * @version 1.0
 */
public class HdfsServeur {

	private static ServerSocket s;

	/**
	 * Programme main qui recupere le fragments depuis le hdfsClient distant et
	 * les enregistre sur la machine local sur lequel il est lance.
	 *
	 * @param args
	 *            Les arguments de la ligne de commande.
	 */
	public static void main(String[] args) {

		int port = Integer.parseInt(args[0]);

		try {

			s = new ServerSocket(port);

			/*
			 * Tant qu'il y a des connexions, on les traite
			 ************************/
			while (true) {

				/* On accepte la connexion dans condition */
				Socket socket = s.accept();

				/* On lit le message qui arrive */
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader plec = new BufferedReader(isr);
				String texte = plec.readLine();

				/* On récupère le contenu à enregistrer dans le fichier créé */
				String[] contenu = texte.split("<->");

				switch (contenu[0]) {
				case "w":
					/*
					 * Création d'un fichier de type format dans lequel écrire
					 * ce qui est reçu
					 */
					File f = new File(contenu[1] + "/aFragmenter-res-local");
					FileWriter fw = new FileWriter(f, true);
					BufferedWriter bw = new BufferedWriter(fw);

					/* Ecriture du fragment dans le fichier de sortie créé */
					bw.write(contenu[2]);
					bw.flush();
					bw.close();
					
					
					
					System.out.println("On a écrit " + contenu[2] + " dans le fichier aFragmenter-res-local");

					break;

				case "r":
					String contenuFichier = null;
					/* Récupération des données dans le fichier fname */
					String fname = contenu[1];
					System.out.println("On lit dans " + fname);
					File f_lect = new File(fname);
					if (f_lect.length() == 0) {
						System.out.println("Le fichier souhaité n'est pas là");
						contenuFichier = "<-><->";
						OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
						BufferedWriter bwr = new BufferedWriter(osw);
						PrintWriter pred = new PrintWriter(bwr, true);
					    System.out.println("On envoie " + contenuFichier + " au client!");
						pred.println(contenuFichier);
					} else {
						/* Ouverture du format de nom fname en lecture */
						FormatImpl f_lecture = new FormatImpl(Format.Type.KV, 0, fname);
						f_lecture.open(Format.OpenMode.R);
						String ligne;
						String lu = null;
						int nbLignesLues = 0;
						while ((ligne = f_lecture.getBufferedReader().readLine()) != null) {
							if (lu == null) {
								lu = ligne;
							} else {
								lu = lu + "%" + ligne;
							}
							nbLignesLues++;
						}
						// ligne = null
						OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
						BufferedWriter bwr = new BufferedWriter(osw);
						PrintWriter pred = new PrintWriter(bwr, true);
						String aEnvoyer = nbLignesLues + "#" + lu;
						pred.println(aEnvoyer);
					    //System.out.println("On envoie " + aEnvoyer + " au client!");
						
					}
					/*
					 * Envoie des données du fichier fname sur le socket du
					 * client
					 */
					
					break;
				case "d":
					/* On supprime le fichier sur la machine */
					File fl = new File(contenu[1]);
					if (fl.exists()) {
						fl.delete();
						System.out.println("Suppression : " + contenu[1]);
					} else {
						System.out.println("Le fichier qui n'existe pas...");
					}
					break;

				default:
					System.err.println("Commande inconnue...");
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			// System.err.println("Le serveur a échoué...");
		}
	}
}