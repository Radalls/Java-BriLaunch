package clientama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Application {
		private final static String nom = "client1";
		private final static int PORT = 2600;
		private final static String HOST = "localhost"; 
	
	public static void main(String[] args) {
		Socket clientSocket = null;		
	try {
			
			clientSocket = new Socket(HOST,PORT);
			final BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			final PrintWriter socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
			final BufferedReader clavierIn = new BufferedReader(new InputStreamReader(System.in));
			String lineRecu = "";
			

			//TODO Appli Amateur
			while (true) {
				
				// recevoir
				lineRecu = socketIn.readLine();
				if (lineRecu ==  null || lineRecu.equals("Fin"))
					break;
				
				System.out.println(lineRecu.replaceAll("##", "\n"));

				// taper au clavier
				final String lineEnvoyee = clavierIn.readLine();

				// envoyer
				socketOut.println(lineEnvoyee);

			}
			
			// fermer socket
			clientSocket.close();
			System.out.println(nom + " a terminé");

		} catch (IOException e) {
			System.err.println("Fin du service");
			
		} finally {
			if (clientSocket != null) {
				try { 
					clientSocket.close(); 
				} 
			    catch (IOException e2) {
					System.err.println("Err fermeture socket" + e2);
				}
			}
		}
	}
}
