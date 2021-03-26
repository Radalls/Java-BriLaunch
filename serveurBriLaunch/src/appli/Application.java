package appli;

import java.io.IOException;

import bri.ServeurBri;
import utilisateurs.Utilisateurs;

public class Application {
	public static final int PORT_PROG = 2500;
	public static final int PORT_AMA = 2600;
	
	public static void main(String[] args) {
		
		try {
			Utilisateurs.getInstance().chargerDonneesProgrammeurs();
			System.out.println("lecture de données terminée");
			new Thread(new ServeurBri(PORT_PROG)).start();
			new Thread(new ServeurBri(PORT_AMA)).start();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Erreur lecture de données");
		}
			
	}

}

