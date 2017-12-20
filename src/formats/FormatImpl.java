package formats;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.StringTokenizer;

/**
 * Classe FormatImpl implemente l'interface Format. Permet d'ouvrir, lire,
 * ecrire ou fermer des fichiers dans les differents formats.
 *
 * @author William Attache and Tom Ragonneau
 * @version 1.0
 */
public class FormatImpl implements Format {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Le type de format.
	 */
	private Format.Type type;

	/**
	 * L'index.
	 */
	private long index;

	/**
	 * Le nom du fichier.
	 */
	private String fname;

	/**
	 * Le fichier de nom fname.
	 */
	private File fichier;

	/**
	 * Le buffer de lecture.
	 */
	private BufferedReader br;

	/**
	 * Le buffer d'ecriture.
	 */
	private BufferedWriter bw;

	/**
	 * Construire un format.
	 *
	 * @param type
	 *            Le type de format.
	 * @param index
	 *            L'index initial.
	 * @param fname
	 *            Le nom du fichier.
	 */
	public FormatImpl(Format.Type type, long index, String fname) {
		this.type = type;
		this.index = index;
		this.fname = fname;
	}

	/**
	 * Ouvre le fichier. Le cree s'il n'existe pas.
	 *
	 * @param mode
	 *            Le mode d'ouverture.
	 */
	public void open(OpenMode mode) {
		this.fichier = new File(fname);
		this.fichier.setExecutable(false);

		if (mode == Format.OpenMode.R) { // Lecture

			this.fichier.setReadable(true);
			this.fichier.setWritable(false);

			try {
				this.br = new BufferedReader(new FileReader(new File(fname)));
			} catch (IOException e) {
				System.err.println("Erreur de lecture du fichier...");
				e.printStackTrace();
			}

		} else { // Ecriture

			try {
				this.fichier.createNewFile();
			} catch (IOException e) {
				System.err.print("Erreur d'ouverture du nouveau fichier...");
			}

			// this.fichier.setReadable(true);
			// this.fichier.setWritable(true);

			try {
				this.bw = new BufferedWriter(new FileWriter(new File(fname)));
			} catch (IOException e) {
				System.err.print("Erreur d'ecriture du fichier...");
			}
		}
	}

	/**
	 * Lecture des donnees.
	 *
	 * @return Un KV de lecture.
	 */
	public KV read() {

		KV kv = null;

		/*
		 * On choisit le nombre de caractères que l'on veut mettre dans un
		 * fragment
		 */
		int taille = 20000;

		try {
			if (this.type == Format.Type.LINE) { // Format LINE

				int compteurChar = 0;
				String contenu = null;

				String texte = null;
				while ((compteurChar < taille) && ((texte = br.readLine()) != null)) {
					if (contenu == null) {
						contenu = texte;
					} else {
						contenu = contenu + texte;
					}
					compteurChar = compteurChar + texte.length();
				}

				if (contenu != null) {
					kv = new KV(String.valueOf(index), contenu);
				}

			} else { // Format KV
				
				/* On considère qu'il y a une KV par ligne */
				String ligne = br.readLine();

				if (ligne != null) {
					String[] parties = ligne.split("<->");

					kv = new KV(parties[1], parties[0]);

				}
			}

			this.index++;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return kv;
	}

	/**
	 * Ecrire dans un format.
	 *
	 * @param record
	 *            le fichier de KV.
	 */
	public void write(KV record) {
		try {
			bw.write(record.k + KV.SEPARATOR + record.v, 0,
					record.k.length() + KV.SEPARATOR.length() + record.v.length());
			bw.flush();

			bw.newLine();

			this.index++;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ferme le fichier.
	 */
	public void close() {
		try {
			if (br != null) {
				br.close();
			}

			if (bw != null) {
				bw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne l'index.
	 *
	 * @return L'index
	 */
	public long getIndex() {
		return this.index;
	}

	/**
	 * Retourne le nom du fichier.
	 *
	 * @return Le nom du fichier
	 */
	public String getFname() {
		return this.fname;
	}

	/**
	 * Modifie le nom du fichier.
	 *
	 * @param fname
	 *            Le nom du fichier
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	public BufferedReader getBufferedReader() {
		return this.br;
	}

}
