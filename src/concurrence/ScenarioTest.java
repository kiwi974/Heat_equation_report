package concurrence;

import formats.Format;
import formats.FormatImpl;

public class ScenarioTest {

	/** Un format a acceder en concurrence */
	static FormatImpl format = new FormatImpl(Format.Type.LINE,0,"aFragmenter");

	/** Un gestionnaire pour gérer les accès concurrents */
	private static Gestionnaire gest;

	
	public static void main(String[] args) {
		gest = new Gestionnaire(format,0);
		Lecteur l1 = new Lecteur(gest);
		l1.start();
		Lecteur l2 = new Lecteur(gest);
		l2.start();
		Redacteur r1 = new Redacteur(gest);
		r1.start();
		Lecteur l3 = new Lecteur(gest);
		l3.start();
	}

}


