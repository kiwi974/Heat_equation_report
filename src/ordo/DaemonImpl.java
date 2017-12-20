package ordo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import map.Mapper;
import formats.Format;

/**
 * Classe DaemonImpl implemente l'interface Daemon.
 * Cet classe doit etre lancee sur chaque machine distante.
 * @author Bonnet, Steux, Xambili
 *
 */
public class DaemonImpl extends UnicastRemoteObject implements Daemon {
	
	/**
	 * Constructeur de DaemonImpl.
	 * @throws RemoteException
	 */
	protected DaemonImpl() throws RemoteException {
		super();
	}

	/**
	 * Ouvre/cree les fichiers puis execute le map localement avant de fermer les fichiers.
	 */
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb)
			throws RemoteException {

		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		m.map(reader, writer);
		reader.close();
		writer.close();

		// dire au client que c'est fini
		cb.addMachineFinished();
	}


	/**
	 * Methode principale de la classe DaemonImpl.
	 * Elle cree le RMI registry sur la machine s'il n'existe pas deje 
	 * puis enregistre une instance de DaemonImpl au registry.
	 * @param args args[0] correspond au numero du Daemon.
	 */
	public static void main(String args[]) {
		try {
			Registry registry = LocateRegistry.createRegistry(4000);
			System.out.println("Creation du registry sur le port 4000");
		} catch (RemoteException e1) {
			System.out.println("registry deja existant");
		}
		try {
			Daemon daemon = new DaemonImpl();
			Naming.rebind("//localhost:4000/Daemon" + args[0], daemon);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
			
	}
}
