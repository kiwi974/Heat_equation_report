package ordo;

//import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Classe CallBack (En Construction)
 * @author Bonnet, Steux, Xambili
 *
 */
//public class CallBack implements Serializable {
public interface CallBack extends Remote {

    //private String machine;
    //public CallBack() {

    public void addMachineFinished() throws RemoteException;

    public boolean estFini() throws RemoteException;

}
