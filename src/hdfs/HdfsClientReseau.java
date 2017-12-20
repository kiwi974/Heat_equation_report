package hdfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import formats.Format;
import formats.FormatImpl;
import formats.KV;

/**
 * Le client.
 *
 * @author William Attache and Tom Ragonneau
 * @version 1.0
 */
public class HdfsClientReseau {

	/** Fichier de configuration ayant les noms des machines du cluster */
	private final static String configDaemons = "../config/daemons.txt";

	/** Tableau comportant les noms des machines du reseau */
	private ArrayList<String> clusterNames;

	/**
	 * Le tableau des numéro de port des serveurs sur les machines distantes
	 */
	private static int[] numPort = { 0, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8089, 8090 };

	/**
	 * Le client a un socket pour envoyer ses fichiers
	 */
	private static Socket s;

	/**
	 * Nombre d'enregistrements distants restant
	 */
	private static int nbRecordLeft = 20;

	/**
	 * Affichage du message d'utilisation.
	 */
	private static void usage() {
		System.out.println("Usage: java HdfsClientReseau read <file>");
		System.out.println("Usage: java HdfsClientReseau write  <file> <line|kv>");
		System.out.println("Usage: java HdfsClientReseau delete <file>");
		System.out.println();
	}

	/** Constructeur */
	public HdfsClientReseau() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(configDaemons)));
			this.clusterNames = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				this.clusterNames.add(line);
			}
			reader.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Permet de supprimer les fragments d'un fichier stocké dans HDFS
	 *
	 * @param fname
	 *            nom du fichier à supprimer
	 */
	public void HdfsDelete(String fname) {

		int nbHotes = this.clusterNames.size();

		for (int indHote = 1; indHote <= nbHotes; indHote++) {

			try {
				/* Ouverture de la connexion sur un socket */
				s = new Socket(InetAddress.getByName(this.clusterNames.get(indHote - 1)), numPort[indHote]);

				/* Demander au serveur la suppresion du fichier de nom fname */
				Envoyer_fragment t = new Envoyer_fragment(s, "rien", "frag" + indHote + "/" + fname, "d", "0");
				t.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Permet d'obtenir une machine sur laquelle enregistrer un fragment (cas de
	 * la duplication)
	 * 
	 * @param duplication
	 *            tableau contenant les machines encore disponibles pour
	 *            l'enregistrement
	 */
	public static int availableAdvice(int[] dupli) {
		Random r = new Random();
		int i = 1 + r.nextInt(10);
		if (nbRecordLeft != 0) {
			while (dupli[i] == 0) {
				i = 1 + r.nextInt(10);
			}
			nbRecordLeft--;
			return i;
		} else {
			return 0;
		}

	}

	/**
	 * Permet d'écrire un fichier dans HDFS. Le fichier localFSSourceFname est
	 * lu sur le système de fichier local, découpé en fragments (de taille fixe)
	 * et les fragments sont envoyés pour stockage sur les différentes machines
	 *
	 * @param fmt
	 *            format du fichier en entree
	 * @param fname
	 *            fichier sur le système de fichiers local
	 */
	public void HdfsWrite(int fmt, String fname) {

		int nbHotes = this.clusterNames.size();

		/*
		 * Création d'un format sur lequel on va effetuer la segmentation
		 ********/
		Format.Type ft = null;
		if (fmt == 0) {
			ft = Format.Type.LINE;
		} else {
			ft = Format.Type.KV;
		}

		FormatImpl fi = new FormatImpl(ft, 1, fname);

		/*
		 * Ouverture du fichier en lecture
		 ***************************************/
		fi.open(Format.OpenMode.R);

		/*
		 * On définit un tableau de 10 Threads, qui feront les 10 écritures
		 ******/
		Envoyer_fragment[] t = new Envoyer_fragment[11];

		/*
		 * On ecrit dans le fichier
		 **********************************************/
		try {
			/* lu sert à lire dans le format ouvert */
			KV lu;

			/*
			 * indHote permet de parcourir la liste des différentes machines du
			 * cluster
			 */
			int indHote = 1;
			while ((lu = fi.read()) != null && (indHote <= nbHotes)) {

				try {

					System.out.println("  ");
					System.out.println("------------------------------------------------");

					System.out.println(
							"Connection : " + this.clusterNames.get(indHote - 1) + " sur le port" + numPort[indHote]);

					/*
					 * Extraction de ce qui nous intéresse : la valeur du KV lu
					 */
					String texte = lu.v;

					/* Gestion du fragment par un thread pour paralléliser */

					s = new Socket(InetAddress.getByName(this.clusterNames.get(indHote - 1)), numPort[indHote]);
					t[indHote] = new Envoyer_fragment(s, texte, "frag" + indHote, "w", "0");

					System.out.println("Enregistrement du fragment sur l'entité distante. ");
					System.out.println("------------------------------------------------");
					System.out.println(" ");

					t[indHote].start();

					/*
					 * On passe à l'hôte, et donc au fragment, suivant
					 */
					indHote++;

				} catch (Exception e) {
					e.printStackTrace();
					// System.err.println("L'écriture du fichier a échouée...");
				}
				
			}
			for (int k = 1 ; k <= nbHotes ; k++) {
				t[k].join();
			}

		} catch (Exception e) {
			System.err.println("Aucun fichier n'a été trouvé...");
		}
	}

	/**
	 * Permet de lire un fichier de nom fname à partir de HDFS. Les fragments du
	 * fichier sont lus à partir des différentes machines, concaténés et stockés
	 * localement dans un fichier de nom localFSDestFname.
	 *
	 * @param fname
	 *            fichier dont on lit les fragments
	 */
	public void HdfsRead(String fname) {

		int nbHotes = this.clusterNames.size();

		/*
		 * 
		 * Création du fichier de concaténation sur le disque local
		 **************/
		FormatImpl fi = new FormatImpl(Format.Type.LINE, 1, "fragment-res-tmp");

		/*
		 * Ouverture du format en écriture
		 ***************************************/
		fi.open(Format.OpenMode.W);

		/*
		 * On regarde sur chacune des machines du cluster s'il y a un fichier de
		 * nom fname
		 */
		for (int indHote = 1; indHote <= 10; indHote++) {

			try {

				/* Ouverture de la connexionsur un socket */
				s = new Socket(InetAddress.getByName(this.clusterNames.get(indHote - 1)), numPort[indHote]);

				System.out.println("  ");
				System.out.println("-------------------------------------------");
				System.out.println(" On lit sur la machine " + this.clusterNames.get(indHote - 1));

				Envoyer_fragment t = new Envoyer_fragment(s, "rien", "frag" + indHote + "/" + fname, "r", "0");
				t.start();

				/*
				 * Récupération des fragments et mise dans le fichier local de
				 * sortie
				 */
				Recuperer_fragment t1 = new Recuperer_fragment(s, fi);
				t1.start();

				/* Attendre que t1 termine avant de faire le finally */
				try {
					t1.join();
				} catch (InterruptedException e) {
					System.err.print("La connexion a été interrompue...");
				}

			} catch (FileNotFoundException e) {
				System.err.print("Aucun fichier n'a été trouvé...");
			} catch (IOException e) {
				System.err.print("La commande a échouée...");
			}

		}

	}

	/**
	 * Programme qui effectue le decoupage, se connecte a des entites distantes
	 * et leur envoie les fichiers, avec toutes les informations necessaire a la
	 * recuperation et au traitement des fichiers par le Map-Reduce.
	 *
	 * @param args
	 *            Les arguments de la ligne de commande. args[0] correspond a
	 *            l'instruction. args[1] correspond au nom du fichier. args[2]
	 *            correspond au type du fichier.
	 */
	public static void main(String[] args) {
		
		HdfsClientReseau client = new HdfsClientReseau();

		if (args.length < 2) {
			usage();
			return;
		}

		switch (args[0]) {
		case "write":
			if (args.length != 3) {
				usage();
				return;
			}

			client.HdfsWrite(Integer.parseInt(args[2]), args[1]);
			break;

		case "read":
			client.HdfsRead(args[1]);
			break;

		case "delete":
			client.HdfsDelete(args[1]);
			break;

		default:
			usage();
			break;
		}
	}

}
