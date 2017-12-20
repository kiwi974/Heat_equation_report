package ordo;

import map.MapReduce;
import ordo.Chrono;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;

import formats.Format;
import formats.FormatImpl;
import hdfs.HdfsClientReseau;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe Job implemente JobInterface.
 * Permet de lancer les map/reduce sur les machines distantes
 * (actuellement seulement en local sur la meme machine et un seul reduce et 4 maps).
 * @author Bonnet, Steux, Xambili
 *
 */
public class Job implements JobInterface {


	//private static final String listeMachine[] = {"yoda.enseeiht.fr", "vador.enseeiht.fr", "aragorn.enseeiht.fr", "gandalf.enseeiht.fr"};
	private ArrayList<String> daemons;
	private final static String configDaemons = "../config/daemons.txt";

	/** Nombre de reduces. */
	private int numberOfReduces;
	/** Nombre de maps. */
	private int numberOfMaps;
	/** Format d'entree. */
	private Format.Type inputFormat;
	/** Format de sortie/ */
	private Format.Type outputFormat;
	/** Nom du fichier source. */
	private String inputFname;
	/** Nom du fichier resultat. */
	private String outputFname;
	//private SortComparator sortComparator;

	private HashMap<Integer, CallBack> listeCallBack;
	
	/**
	 * Constructeur de Job.
	 */
	public Job() {
		this.numberOfReduces = 1;
		this.inputFormat = Format.Type.KV;
		this.outputFormat = Format.Type.KV;
		this.listeCallBack = new HashMap<Integer, CallBack>();
		try {
			initDaemons();
			this.numberOfMaps = daemons.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	
	public void setNumberOfReduces(int tasks) {
		this.numberOfReduces = tasks;
	}

	
	public void setNumberOfMaps(int tasks) {
		this.numberOfMaps = tasks;
	}

	
	public void setInputFormat(Format.Type ft) {
		this.inputFormat = ft;
	}

	
	public void setOutputFormat(Format.Type ft) {
		this.outputFormat = ft;
	}
	
	
	public void setInputFname(String fname) {
		this.inputFname = fname;
		this.outputFname = fname + "-res";
	}

	
	/*public void setOutputFname(String fname) {
		this.outputFname = fname;
	}*/

	
	/*public void setSortComparator(SortComparator sc) {
		this.sortComparator = sc;
	}*/

	
	public int getNumberOfReduces() {
		return this.numberOfReduces;
	}

	
	public int getNumberOfMaps() {
		return this.numberOfMaps;
	}

	
	public Format.Type getInputFormat() {
		return this.inputFormat;
	}

	
	public Format.Type getOutputFormat() {
		return this.outputFormat;
	}
	
	
	public String getInputFname() {
		return this.inputFname;
	}

	
	public String getOutputFname() {
		return this.outputFname;
	}

	
	/*public SortComparator getSortComparator() {
		return this.sortComparator;
	}*/
	
	/**
	 * Permet de lancer les maps sur les machines reparties
	 * (seulement en locale pour le moment) ainsi que d'executer le reduce.
	 * Le fichier source doit prealablement etre decouper en 4 parties.
	 */
	public void startJob(MapReduce mr) {
		
		HdfsClientReseau client = new HdfsClientReseau();
		
		/* Récupération du fichier et segmentation */
		client.HdfsWrite(0,"/home/wattache/nosave/grosFichierKaaris");

		
		
		RunMapThread t[] = new RunMapThread[numberOfMaps];

		/* On démarre le chrono avant le map */
		Chrono chrono = new Chrono();
		chrono.start();		

		for (int i=1 ; i<=numberOfMaps; i++) {

			
			try {
				Format readerCourant = new FormatImpl(inputFormat, 0,"frag" + i + "/aFragmenter-res-local"); //"/" + inputFname+"_part"+i);
				Format writerCourant = new FormatImpl(outputFormat,  0, "frag" + i + "/aFragmenter-map"); //outputFname+"-tmp_part"+i);

				CallBack cb = new CallBackImpl(i);
				listeCallBack.put(i, cb);


				//recuperation de l'objet
				//Daemon daemon = (Daemon) Naming.lookup("//localhost:4000/Daemon"+i);


				Daemon daemon = (Daemon) Naming.lookup("//" + daemons.get(i-1) + ":4000/Daemon"+i);

				// appel de RunMap
				t[i-1] = new RunMapThread(daemon, mr, readerCourant, writerCourant, cb);
				t[i-1].start();

			} catch (Exception ex) {
					ex.printStackTrace();
			}
		}

		System.out.println("Les jobs sont finis, on attend la terminaison des threads");
		
		// a modifier si duplication de machine
		for (int i=0 ; i<numberOfMaps; i++) {
			try {
				t[i].join();
				/*try {
					if (listeCallBack.get(i+1).estFini()) {
						System.out.println("daemon " + (i+1) + " fini !");
				     }
			     }catch (RemoteException e) {
					System.out.println("remote exception");
				}*/

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("----------------------------------");
		System.out.println("----------------------------------");
		System.out.println("On passe à la lecture des fichiers");
		System.out.println("----------------------------------");
		System.out.println("----------------------------------");
		
		//HdfsClient.HdfsRead(outputFname + "-tmp", outputFname + "-tmp", this.numberOfMaps);
		/* Ecriture de tous les fichier en local */
		client.HdfsRead("aFragmenter-map");
		
		System.out.println("----------------------------------");
		System.out.println("----------------------------------");
		System.out.println("---Les lectures sont terminées.---");
		System.out.println("----------------------------------");
		System.out.println("----------------------------------");		
		
		Format readerRes = new FormatImpl(outputFormat, 0, "fragment-res-tmp");
		Format writerRes = new FormatImpl(outputFormat, 0, "afragmenter-res");
		readerRes.open(Format.OpenMode.R);
		writerRes.open(Format.OpenMode.W);
		mr.reduce(readerRes, writerRes);
		/* Terminaison du chrono car le traitement est fini */
		chrono.stop();
		readerRes.close();
		writerRes.close();
		System.out.println("La tâche c'est effectuée en " + chrono.getDureeMs() + " ms");
	}
	
	private void initDaemons() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(configDaemons)));
		this.daemons = new ArrayList<String>();
	    String line;
	    while ((line = reader.readLine()) != null) {
	    	  daemons.add(line);
	    }
	    reader.close();
	}
}
