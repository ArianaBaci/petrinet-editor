package application.model;

public class User {
	private final int ID;
	private final String name;
	private final String password;
	
	public User(int ID, String name, String password) {
		this.ID = ID;
		this.name = name;
		this.password = password;
	}

	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public boolean checkPassword(String psw) {
		return (this.password.equals(psw));
	}
}

