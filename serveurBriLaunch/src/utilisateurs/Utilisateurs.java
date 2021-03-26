package utilisateurs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;


public class Utilisateurs {
	
	private static Utilisateurs instance;
	private List<Programmeur> programmeurs;
	
	private Utilisateurs() {
		programmeurs = Collections.synchronizedList(new ArrayList<Programmeur>());
	}

	static {
		instance = new Utilisateurs();
	}
	
	public void addProgrammeur(String login, String password, String ftpURL) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		if(!programmeurs.isEmpty()) {
			writer.append("\n");
		}
		writer.append(login + "," + password + "," + ftpURL);
		writer.close();
		programmeurs.add(new Programmeur(login, password, ftpURL));
	}
	
	public void chargerDonneesProgrammeurs() throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream("users.txt")));
		String ligne;
		
		while ((ligne = buff.readLine()) != null) {
			
			String[] split = ligne.split(",");
			programmeurs.add(new Programmeur(split[0].trim(), split[1].trim(), split[2].trim()));
			
		}
		buff.close();
	}
	
	public Programmeur verifierProgrammeur(String login, String password) {
		synchronized (programmeurs) {
			for (Programmeur p : programmeurs) {
				if (p.getLogin().equals(login) && BCrypt.checkpw(password, p.getPassword())) {
					return p;
				}
			}
		}
		return null;
	}
	
	public static Utilisateurs getInstance() {
		return instance;
	}
	
}
