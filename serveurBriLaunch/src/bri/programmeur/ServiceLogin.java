package bri.programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import bri.Service;
import utilisateurs.Programmeur;
import utilisateurs.Utilisateurs;

public class ServiceLogin implements Service {

	private Socket client;
	private Programmeur programmeur;

	public ServiceLogin(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			System.out.println("Service Login lancé");

			out.println("Bienvenue ##" + "Voulez vous vous inscrire(1), vous connecter(2) ou quitter(3) ? ##" + ">");

			while (true) {
				switch (in.readLine()) {
				case "1":
					out.println("Veuillez entrer votre login :");

					String newLogin = in.readLine();
					out.println("Veuillez entrer votre mot de passe :");
					String newPassword = in.readLine();
					out.println("Veuillez entrer l'adresse ip de votre serveur :");
					String newFtpURL = "ftp://" + in.readLine();
					out.println("Veuillez entrer votre port ");
					newFtpURL += ":" + in.readLine() + "/classes/";
					try {
						Utilisateurs.getInstance().addProgrammeur(newLogin, newPassword, newFtpURL);
						out.println("Inscription effectuée avec succes.");
					} catch (IOException e) {
						out.println("Erreur lors de l'inscription, veuillez réessayer");
						System.err.println("erreur inscription");
					}
					break;

				case "2":
					out.println("Veuillez entrer votre login :");

					String login = in.readLine();
					out.println("Veuillez entrer votre mot de passe :");
					String password = in.readLine();
					if (Utilisateurs.getInstance().verifierProgrammeur(login, password) == null) {
						out.println("Identifiants invalides");
					} else {
						this.programmeur = Utilisateurs.getInstance().verifierProgrammeur(login, password);
						new ServiceAccueil(client, programmeur).run();
					}
					break;

				case "3":
					out.println("Fin");
					in.close();
					client.close();
					break;

				default:
					out.println("Entrez un nombre entre 1 et 3");
				}
			}

		} catch (IOException e) {
			System.err.println("probleme communication client : " + e);
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
