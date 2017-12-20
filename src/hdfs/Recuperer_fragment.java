package hdfs;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;

import formats.FormatImpl;
import formats.KV;

/**
 * Recuperation des fragments.
 *
 * @author William Attache and Tom Ragonneau
 * @version 1.0
 */
public class Recuperer_fragment extends Thread {

	/**
	 * Le socket de connexion.
	 */
	public Socket socket;

	/**
	 * Le format du fichier.
	 */
	public FormatImpl format;

	/**
	 * Construction de la recuperation.
	 *
	 * @param s
	 *            Le socket.
	 * @param form
	 *            Le format.
	 */
	public Recuperer_fragment(Socket s, FormatImpl form) {
		this.socket = s;
		this.format = form;
	}

	/**
	 * Execution de la recuperation.
	 */
	@Override
	public void run() {

		/*
		 * tache vaut 1 si l'écriture a été faite, 0 sinon
		 ***********************/
		int tache = 0;
		String texte;

		try {

			while (tache != 1) {

				/* Récupération du contenu de fname */
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader plec = new BufferedReader(isr);
				String recu = plec.readLine();

				/* Ecriture du contenu dans le format */
				if (recu.equals("<-><->")) {
					System.out.println("On a rien pu récupérer !");
					System.out.println("-------------------------------------------");
					System.out.println("  ");
				} else {
					//System.out.println("On a recu : " + recu + "###########");
					String[] separation = recu.split("#");
					int nbLines = Integer.parseInt(separation[0]);
					String[] items = separation[1].split("%");
					for (int k = 0; k < nbLines; k++) {
						//System.out.println("L'item " + k + " vaut : " + items[k]);
						texte = items[k].split("<->")[0];
						//System.out.println("On a récupéré du serveur : " + texte);
						KV kv = new KV("1", texte);
						this.format.write(kv);
					}
					System.out.println("Le contenu récupéré a été écrit dans :" + format.getFname());
					System.out.println("-------------------------------------------");
					System.out.println("  ");

				}

				tache = 1;

			}

		} catch (Exception e) {
			e.printStackTrace();
			//System.err.print("Erreur de recuperation du fichier...");
		}
	}

}
