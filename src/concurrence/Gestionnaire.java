package concurrence;

import java.util.concurrent.Semaphore;

import formats.Format;

public class Gestionnaire {

	/** Un gestionnaire possède un format a proteger */
	private Format format;

	/** Creation d'un semaphore pour proteger le format */
	private Semaphore semaphore;

	/** Un nombre de lecteur en train de lire */
	private int nbLecteurs;

	public Gestionnaire(Format f, int nbL) {
		this.format = f;
		this.semaphore = new Semaphore(1);
		this.nbLecteurs = nbL;
	}

	public void setNbLecteurs(int i) {
		this.nbLecteurs = i;
	}

	public int getNbLecteurs() {
		return this.nbLecteurs;
	}

	public void debuterLecture() {
		System.out.println("Debut de la lecture");
		this.nbLecteurs++;
	}

	public void terminerLecture() {
		System.out.println("Fin de la lecture");
		nbLecteurs--;
	}

	public void debuterEcriture() {
		while (nbLecteurs != 0) {
		}
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println("Problème lors de l'acquisition du sémaphore");
			e.printStackTrace();
		}
		System.out.println("Debut de l'ecriture");

	}

	public void terminerEcriture() {
		semaphore.release();
		System.out.println("Fin de la l'ecriture");
	}

}
