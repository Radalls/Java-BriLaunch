package bri.amateur;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

import bri.Service;
import bri.ServiceRegistry;

public class ServiceLauncher implements Runnable {

	private Socket client;

	public ServiceLauncher(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			
			out.println(ServiceRegistry.getInstance().toStringue() + "Tapez le numéro de service désiré :##>");
			int choix = Integer.parseInt(in.readLine().trim());

			// instancier le service numéro "choix" en lui passant la socket "client"
			try {
				Constructor<? extends Service> serviceConstr = ServiceRegistry.getInstance().getServiceClass(choix)
						.getDeclaredConstructor(java.net.Socket.class);
				Service service = serviceConstr.newInstance(client);
				
				// invoquer run() pour cette instance
				service.run();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}


		} catch (IOException e) {
			System.err.println("Service arreté ");
		}

		try {
			client.close();
			System.err.println("Service arreté ");
		} catch (IOException e2) {
		}
	}

	protected void finalize() throws Throwable {
		client.close();
		System.err.println("Service arreté ");
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}

}
