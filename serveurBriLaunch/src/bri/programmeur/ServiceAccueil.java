package bri.programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import bri.Service;
import bri.ServiceRegistry;
import utilisateurs.Programmeur;

public class ServiceAccueil implements Service {

	private Socket client;
	private Programmeur programmeur;

	public ServiceAccueil(Socket socket, Programmeur p) {
		client = socket;
		programmeur = p;
	}

	public void run() {

		try {
			String nomProg = programmeur.getLogin(); // pour suivre les actions de chaque programmeur

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			// URLClassLoader sur ftp
			//String fileDirURL = "ftp://localhost:2121/classes/";
			URLClassLoader urlcl = new URLClassLoader(new URL[] { new URL(programmeur.getFtpURL()) }) {
			};

			out.println("##*******************************************************************************************##"
					+ "Bienvenue dans votre gestionnaire de services, entrez un numéro pour effectuer une action ## "
					+ "1 : Fournir un nouveau service##" + "2 : Mettre à jour un service##"
					+ "3 : Déclarer un changement d’adresse de son serveur ftp##" + "4 : Se deconnecter##" + ">");

			while (true) {

				switch (in.readLine()) {

				case "1":
					out.println("Nom de la classe :");
					String classeName = in.readLine();
					Class<? extends Service> classeChargee;
					try {
						classeChargee = urlcl.loadClass(programmeur.getLogin()+"."+classeName).asSubclass(Service.class);
						try {
							ServiceRegistry.getInstance().addService(classeChargee);
							out.println("Classe " + classeName + " chargée avec succès.");
							System.out.println(nomProg + " a chargé la classe " + classeName + "avec succès.");
						
						} catch (Exception e) {
							System.err.println("Erreur de" + nomProg + " : " + e.getMessage());
							out.println(e.getMessage() + "");
						}
						
					} catch (ClassNotFoundException | NoClassDefFoundError | ClassCastException e) {
						e.printStackTrace(); // FIXME (pr l'instant c que pr les tests)
						System.err.println("Erreur de" + nomProg + " : Classe non valide (" + e + ")");
						out.println("Veuillez entrer une classe valide.");
					}
					break;

				case "2":
					// TODO case 2
					break;

				case "3":
					out.println("Entrez l'adresse ip de votre serveur :");

					String fileDirURL = "ftp://" + in.readLine();
					out.println("Entrez le port:");
					fileDirURL += ":" + in.readLine() + "/classes/";

					programmeur.setFtpURL(fileDirURL);
					urlcl = new URLClassLoader(new URL[] { new URL(fileDirURL) }) {
					};
					
					out.println("Adresse ip du serveur correctement modifiée");

					break;

				case "4":
					out.println("Fin");
					urlcl.close();
					in.close();
					System.out.println(nomProg + " s'est déconnecté");
					client.close();
					break;

				default:
					out.println("Entrez un chiffre entre 1 et 3");
				}
			}
			

		} catch (IOException e) {
			System.err.println("client déconnecté");
		}

	}

	protected void finalize() throws Throwable {
		client.close();
		System.err.println("Service arreté");
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();
	}
}
