package application.model;

import java.util.Map;
import java.util.HashMap;

public class UserRepository {
	public User loggedUser;
	public int userCount;
	private final DatabaseManager dbManager;
	private Map <String, User> userCache;

	public UserRepository (DatabaseManager dbManager) {
		this.loggedUser = null;
		this.dbManager = dbManager;
		refreshUserCache();
		System.out.println(userCache);
		
		//Qua accumuliamo l'ID più grande per non avere conflitti quando inseriamo altri utenti.
		userCount = 0; 
		for (User u : userCache.values()) {
			if (u.getID() > userCount)
				userCount = u.getID();
		}
		System.out.println("User id set to: "+userCount);
	}

	
	private void refreshUserCache() {
		userCache = dbManager.getUsers();
	}
	
	//Qui chiaramente al posto delle println andranno lanciate eccezioni
	public String loginUser(String name, String password) {
		String res = "";
		if (userCache.containsKey(name)) {
			User u = userCache.get(name);
			if (u.checkPassword(password)) {
				loggedUser = u;
				Debugger.log(res);
			} else {
				res = "Wrong password!";
				System.out.println(res);
			}
		} else {
			res = "User not found!";
			System.out.println(res);
		}
		return res;
	}
	public String registerUser(String name, String password) {
		String res = "";
		
		if (name.equals("")||password.equals("")) {
			res += "Invalid username or password";
			return res;
		}

		if (!userCache.containsKey(name)) {
			User u = new User (userCount+1, name, password);
			boolean success = dbManager.addUser(u);
			
			if (success) {
				userCount++;
				userCache.put(u.getName(), u);
				res = "User "+name+" was registered successfully!";
			}
			
		} else {
			res = "The username "+name+" is already taken.";
		}
		
		return res;
	}
	
	public User getLoggedUser() {
		return this.loggedUser;
	}
	
	public void logout() {
		this.loggedUser = null;
	}
	
}
