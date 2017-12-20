package concurrence;

public class Redacteur extends Thread {
	
	private Gestionnaire g;

	public Redacteur(Gestionnaire gest) {
		this.g = gest;
	}

	public void run() {
		this.g.debuterEcriture();
		System.out.println("Ecriture en cours");
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			System.out.println("Problème dans le sleep");
			e.printStackTrace();
		}
		this.g.terminerEcriture();
	}
}

