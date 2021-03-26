package bri;

import java.io.IOException;
import java.net.ServerSocket;

import bri.programmeur.ServiceLogin;
import bri.amateur.ServiceLauncher;

public class ServeurBri implements Runnable {
	private ServerSocket listen_socket;
	private int port;
	
	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBri(int port) {
		try {
			this.port = port;
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void run() {
		//TODO run ServeurBri
		try {
			while(true)
				if (port == 2600) {
					new ServiceLauncher(listen_socket.accept()).start();
				}
				else if (port == 2500) {
					System.out.println("Serveur lancé sur le port 2500");
					new ServiceLogin(listen_socket.accept()).start();				
				}
		}
		catch (IOException e) { 
			try {this.listen_socket.close();} catch (IOException e1) {}
			System.err.println("Pb sur le port d'ecoute :"+e);
		}
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();
		System.err.println("Serveur arreté ");} catch (IOException e1) {}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();		
	}
}
