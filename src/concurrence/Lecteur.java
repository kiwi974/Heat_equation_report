package concurrence;

public class Lecteur extends Thread {

	private Gestionnaire g;
	
	public Lecteur(Gestionnaire gest) {
		this.g = gest;
	}

	public void run() {
		this.g.debuterLecture();
		System.out.println("Il y a " + g.getNbLecteurs() + " lecteurs");
		System.out.println("Lecture en cours");
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			System.out.println("Problème dans le sleep");
			e.printStackTrace();
		}
		this.g.terminerLecture();
		System.out.println("Il y a " + g.getNbLecteurs() + " lecteurs");
	}
}
