package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;

/**
 * Interface Daemon permettant l'execution repartie des maps.
 * Un Daemon doit etre lance sur chaque machine distante (noeud).
 * @author Bonnet, Steux, Xambili
 *
 */
public interface Daemon extends Remote {
	/**
	 * Execute le map fourni par l'application.
	 * @param m le map fourni par l'application
	 * @param reader fragment locale du fichier source
	 * @param writer fragment locale du resultat
	 * @param cb callback
	 * @throws RemoteException
	 */
	public void runMap (Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException;
}
