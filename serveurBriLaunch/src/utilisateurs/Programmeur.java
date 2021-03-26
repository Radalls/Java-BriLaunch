package utilisateurs;

public class Programmeur {
	private String login;
	private String password;
	private String ftpURL;
	
	
	public Programmeur(String login, String password, String ftpURL) {
		this.login = login;
		this.password = password;
		this.ftpURL = ftpURL;
	}


	public String getFtpURL() {
		return ftpURL;
	}


	public void setFtpURL(String ftpURL) {
		this.ftpURL = ftpURL;
	}


	public String getLogin() {
		return login;
	}


	public String getPassword() {
		return password;
	}
	
}
