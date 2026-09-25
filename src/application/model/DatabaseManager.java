package application.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class DatabaseManager {
	private final String dbUrl = "jdbc:sqlite:src/resources/app.db";

	public DatabaseManager() {

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				var meta = conn.getMetaData();
				Debugger.log("First database connection successful, driver name: " + meta.getDriverName());

				//Checks if the database is empty and if so populates it.
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT COUNT(*) AS count FROM sqlite_master");
				if (rs.getInt(1) < 1) {
					CreateDatabase(conn);
				}
			}
		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
		}
	}

	private void CreateDatabase(Connection conn) throws SQLException {

		String createUserTable = "CREATE TABLE IF NOT EXISTS users ( \n" +
				"id INTEGER PRIMARY KEY, \n" +
				"name TEXT, \n" +
				"password TEXT " +
				"); ";

		Statement stmnt = conn.createStatement();
		Debugger.log("running:\n" + createUserTable);
		stmnt.execute(createUserTable);

		//Adds admin as a default user
		stmnt.execute("INSERT INTO users (id, name, password) VALUES ( 1, 'admin', 'admin')");

		//Creates a table for the Petri Nets
		String createNetTable = "CREATE TABLE IF NOT EXISTS nets ( \n" +
				"id INTEGER PRIMARY KEY, \n" +
				"name TEXT, \n" +
				"adminName TEXT, \n" +
				"initialPlace INTEGER, \n" +
				"finalPlace INTEGER, \n" +
				"dateCreated DATE, \n" +
				"published INTEGER"+
				" ); ";
		Debugger.log("running:\n" + createNetTable);
		stmnt.execute(createNetTable);

		String createPlaceTable = "CREATE TABLE IF NOT EXISTS places ( \n" +
				"id INTEGER , \n" +
				"netId INTEGER , \n" +
				"name TEXT, \n" +
				"PRIMARY KEY (id, netId)" +
				"); ";
		Debugger.log("running:\n" + createPlaceTable);
		stmnt.execute(createPlaceTable);

		String createTransitionTable = "CREATE TABLE IF NOT EXISTS transitions ( \n" +
				"id INTEGER , \n" +
				"netId INTEGER , \n" +
				"name TEXT, \n" +
				"userType INTEGER, \n" +
				"PRIMARY KEY (id, netId)" +
				"); ";
		Debugger.log("running:\n" + createTransitionTable);
		stmnt.execute(createTransitionTable);

		String createArcTable = "CREATE TABLE IF NOT EXISTS arcs ( \n" +
				"id INTEGER , \n" +
				"netId INTEGER , \n" +
				"sourceId INTEGER , \n" +
				"targetId INTEGER , \n" +
				"PRIMARY KEY (id, netId)" +
				"); ";
		Debugger.log("running:\n" + createArcTable);
		stmnt.execute(createArcTable);

		//Creates a table for the computations
		String createComputationTable = "CREATE TABLE IF NOT EXISTS computations ( \n" +
				"id INTEGER PRIMARY KEY, \n" +
				"petriNetId INTEGER, \n" +
				"username TEXT, \n" +
				"dateCreated DATE, \n" +
				"dateCompleted DATE, \n" +
				"status TEXT" +
				"); ";

		Debugger.log("running:\n" + createComputationTable);
		stmnt.execute(createComputationTable);

		//Creates a table for computations steps
		String createComputationStepsTable = "CREATE TABLE IF NOT EXISTS computationSteps ( \n" +
				"id INTEGER PRIMARY KEY, \n" +
				"computationId INTEGER, \n" +
				"transitionId INTEGER, \n" +
				"transitionName TEXT, \n" +
				"timestamp TEXT" +
				"); ";

		Debugger.log("running:\n" + createComputationStepsTable);
		stmnt.execute(createComputationStepsTable);

		//create table for computation steps markings (place,# tokens)
		String createMarkingTable = "CREATE TABLE IF NOT EXISTS markings ( \n" +
				"computationId INTEGER , \n" +
				"computationStepId INTEGER , \n" +
				"placeId INTEGER , \n" +
				"tokens INTEGER , \n" +
				"PRIMARY KEY (computationId, computationStepId, placeId)" +
				"); ";

		Debugger.log("running:\n" + createMarkingTable);
		stmnt.execute(createMarkingTable);
	}

	public Map<String, User> getUsers() {
		HashMap<String, User> res = new HashMap<String, User>();

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT * FROM users;");
				while (rs.next()) {
					int ID = rs.getInt("id");
					String name = rs.getString("name");
					String password = rs.getString("password");
					User newUser = new User(ID, name, password);
					res.put(name, newUser);
				}
			}

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
		}
		return res;
	}

	public boolean addUser(User u) {

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				String instruction = "INSERT OR REPLACE INTO users (id, name, password) VALUES (?, ?, ?)";
				PreparedStatement stmnt = conn.prepareStatement(instruction);
				stmnt.setObject(1, u.getID());
				stmnt.setObject(2, u.getName());
				stmnt.setObject(3, u.getPassword());
				stmnt.executeUpdate();
				return true;

			} else {
				return false;

			}

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	public List<PetriNet> getNets() {
		ArrayList<PetriNet> res = new ArrayList<PetriNet>();
		int nodeCount = 0;
		int arcCount = 0;

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT * FROM nets;");
				while (rs.next()) {
					int ID = rs.getInt("id");
					String name = rs.getString("name");
					String adminName = rs.getString("adminName");
					int initialPlace = rs.getInt("initialPlace");
					int finalPlace = rs.getInt("finalPlace");
					String dateString = rs.getString("dateCreated");
					LocalDate dateCreated = (dateString != null) ? LocalDate.parse(dateString) : null;
					boolean published = false;
					int pubint = rs.getInt("published");
					if (pubint!=0) {
						published = true;
					}
					PetriNet net = new PetriNet(ID, name, adminName, initialPlace, finalPlace, dateCreated, published);
					res.add(net);
				}

				for (PetriNet net : res) {
					Debugger.log("Loading net with id=" + net.getID());
					String instruction = "SELECT * FROM places WHERE netId=" + net.getID() + ";";
					Debugger.log("running: " + instruction);
					ResultSet rs2 = stmnt.executeQuery(instruction);
					while (rs2.next()) {
						int ID = rs2.getInt("id");
						String name = rs2.getString("name");
						Place p = new Place(ID, name, net.getID());
						net.addPlace(p);
						if (p.getID() > nodeCount)
							nodeCount = p.getID();
					}

					rs2 = stmnt.executeQuery("SELECT * FROM transitions WHERE netId =" + net.getID() + ";");
					while (rs2.next()) {
						int ID = rs2.getInt("id");
						String name = rs2.getString("name");
						UserType ut = UserType.ADMIN;
						if (rs2.getInt("userType") == 1) {
							ut = UserType.ENDUSER;
						}
						Transition t = new Transition(ID, name, net.getID(), ut);
						net.addTransition(t);
						if (t.getID() > nodeCount)
							nodeCount = t.getID();
					}

					rs2 = stmnt.executeQuery("SELECT * FROM arcs WHERE netId =" + net.getID() + ";");
					int sourceID = 0;
					int targetID = 0;
					while (rs2.next()) {
						int ID = rs2.getInt("id");
						sourceID = rs2.getInt("sourceId");
						targetID = rs2.getInt("targetId");
						Arc a = new Arc(ID, sourceID, targetID, net.getID());
						net.addArc(a);
						if (a.getID() > arcCount)
							arcCount = a.getID();
					}
					net.makeNodesThroughUnion();
				}
			}

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
		}
		Node.setCount(nodeCount);
		Arc.setCount(arcCount);
		return res;
	}

	public boolean saveNet(PetriNet n) {

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				String instruction = "DELETE FROM nets WHERE id=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM places WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM transitions WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM arcs WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);

				instruction = "INSERT OR REPLACE INTO nets (id, name, adminName, initialPlace, finalPlace, dateCreated, published) VALUES (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement stmnt = conn.prepareStatement(instruction);
				stmnt.setObject(1, n.getID());
				stmnt.setObject(2, n.getName());
				stmnt.setObject(3, n.getAdminName());
				stmnt.setObject(4, n.getInitialPlace());
				stmnt.setObject(5, n.getFinalPlace());
				stmnt.setObject(6, n.getDateCreated());
				if (n.getPublished()) {
					stmnt.setObject(7, 1);
				} else {
					stmnt.setObject(7, 0);
				}
				stmnt.executeUpdate();

				instruction = "INSERT OR REPLACE INTO places (id, netId, name) VALUES (?, ?, ?)";
				for (Place p : n.getPlaces()) {
					stmnt = conn.prepareStatement(instruction);
					stmnt.setObject(1, p.getID());
					stmnt.setObject(2, p.getPetriNetID());
					stmnt.setObject(3, p.getName());
					stmnt.executeUpdate();
				}

				instruction = "INSERT OR REPLACE INTO transitions (id, netId, name, userType) VALUES (?, ?, ?, ?)";
				for (Transition t : n.getTransitions()) {
					stmnt = conn.prepareStatement(instruction);
					stmnt.setObject(1, t.getID());
					stmnt.setObject(2, t.getPetriNetID());
					stmnt.setObject(3, t.getName());
					if (t.getUserType() == UserType.ENDUSER) {
						stmnt.setObject(4, 1);
					} else {
						stmnt.setObject(4, 0);
					}
					stmnt.executeUpdate();
				}

				instruction = "INSERT OR REPLACE INTO arcs (id, netId, sourceId, targetId) VALUES (?, ?, ?, ?)";
				for (Arc a : n.getArcs()) {
					stmnt = conn.prepareStatement(instruction);
					stmnt.setObject(1, a.getID());
					stmnt.setObject(2, a.getPetriNetID());
					stmnt.setObject(3, a.getSourceID());
					stmnt.setObject(4, a.getTargetID());
					stmnt.executeUpdate();
				}
				return true;

			} else {
				return false;

			}

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	public boolean deleteNet(PetriNet n) {

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				String instruction = "DELETE FROM nets WHERE id=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM places WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM transitions WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM arcs WHERE netId=" + n.getID() + ";";
				stmt.execute(instruction);
				return true;
			}
			return false;

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	public boolean saveComputationStep(ComputationStep cs) {
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				String instruction = "INSERT INTO computationSteps (id, computationId, transitionId,transitionName, timestamp) VALUES (?, ?, ?, ?,?)";
				PreparedStatement stmnt = conn.prepareStatement(instruction);
				stmnt.setObject(1, cs.getID());
				stmnt.setObject(2, cs.getComputationID());
				stmnt.setObject(3, cs.getTransitionID());
				stmnt.setObject(4, cs.getTransitionName());
				stmnt.setObject(5, cs.getTimestamp()); // l'alternativa è inserire in questo momento il valore timestampstmnt.setObject(4, cs.getTimestamp()); // l'alternativa è inserire in questo momento il valore timestamp
				stmnt.executeUpdate();

				instruction = "INSERT INTO markings (computationId, computationStepId, placeId, tokens) VALUES (?, ?, ?,?)";
				Marking marking = cs.getMarkingData();
				for(Integer placeID : marking.getMarking().keySet()) {
					stmnt = conn.prepareStatement(instruction);
					stmnt.setObject(1, cs.getComputationID());
					stmnt.setObject(2, cs.getID());
					stmnt.setObject(3, placeID);
					stmnt.setObject(4, marking.getMarking().get(placeID));
					stmnt.executeUpdate();
				}
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}
	public boolean saveComputation(Computation c){
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {

				Statement stmt = conn.createStatement();
				String instruction = "DELETE FROM computations WHERE id=" + c.getID() + ";";
				stmt.execute(instruction);

				instruction = "INSERT OR REPLACE INTO computations (id, petriNetId, username, dateCreated, dateCompleted,status) VALUES (?, ?, ?, ?,?,?)";
				PreparedStatement stmnt = conn.prepareStatement(instruction);
				stmnt.setObject(1, c.getID());
				stmnt.setObject(2, c.getPetriNet().getID());
				stmnt.setObject(3, c.getUser());
				stmnt.setObject(4, c.getStart_date());  // l'alternativa è inserire in questo momento il valore di date()
				stmnt.setObject(5, c.getEnd_date());
				stmnt.setObject(6, c.getStatus().toString());
				stmnt.executeUpdate();
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	public boolean deleteComputation(Computation c){
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				String instruction = "DELETE FROM computations WHERE id=" + c.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM computationSteps WHERE computationId=" + c.getID() + ";";
				stmt.execute(instruction);
				instruction = "DELETE FROM markings WHERE computationId=" + c.getID() + ";";
				stmt.execute(instruction);
				return true;
			}
			return false;

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	public List<ComputationStep> getComputationSteps(){
		ArrayList<ComputationStep> res = new ArrayList<ComputationStep>();
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT * FROM computationSteps;");
				while (rs.next()) {
					int id = rs.getInt("id");
					int computationId = rs.getInt("computationId");
					int transitionId = rs.getInt("transitionId");
					String transitionName = rs.getString("transitionName");
					String timestampString = rs.getString("timestamp");
					LocalDateTime timestamp = LocalDateTime.parse(timestampString);
					Marking markingData = getMarking(computationId, id);
					ComputationStep compStep = new ComputationStep(id,computationId,transitionId,transitionName,markingData,timestamp);
					res.add(compStep);
				}
			}
		}
		catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return null;
		}
		return res;
	}

	public Marking getMarking(int computationId, int computationStepId){
		Marking res = new Marking();
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT * FROM markings WHERE computationId ="+ computationId + " AND computationStepId =" + computationStepId + ";");
				while (rs.next()) {
					int placeId = rs.getInt("placeId");
					int tokens = rs.getInt("tokens");
					res.getMarking().put(placeId,tokens);
				}
			}
		}
		catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return null;
		}
		return res;
	}

	public List<Computation> getComputations() {
		ArrayList<Computation> res = new ArrayList<Computation>();

		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmnt = conn.createStatement();
				ResultSet rs = stmnt.executeQuery("SELECT * FROM computations;");
				while (rs.next()) {
					int id = rs.getInt("id");
					int petriNetId = rs.getInt("petriNetId");
					String username = rs.getString("username");
					String dateCreatedString = rs.getString("dateCreated");
					String dateCompletedString = rs.getString("dateCompleted");
					String statusString = rs.getString("status");
					LocalDate dateCreated = (dateCreatedString != null) ? LocalDate.parse(dateCreatedString) : null;
					LocalDate dateCompleted = (dateCompletedString != null) ? LocalDate.parse(dateCompletedString) : null;
					ComputationStatus status = ComputationStatus.valueOf(statusString);

					ArrayList<ComputationStep> steps = new ArrayList<ComputationStep>();

					for (ComputationStep cs : getComputationSteps())
						if (cs.getComputationID() == id)
							steps.add(cs);
					Computation c = new Computation(id,petriNetId, username, dateCreated, dateCompleted, steps, status);
					res.add(c);
				}
			}
		}
		catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return null;
		}
		return res;
	}

	public Computation getComputation (int id){
		for(Computation c : getComputations())
			if(c.getID() == id)
				return c;
		return null;
	}


	public ArrayList<ComputationStep> getComputationSteps(int computationId){
		ArrayList<ComputationStep> res = new ArrayList<ComputationStep>();
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				for(ComputationStep cs : getComputationSteps())
					if(cs.getComputationID() == computationId)
							res.add(cs);
			}
		}
		catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return null;
		}
		return res;
	}

	public boolean deleteComputationStep(ComputationStep c){
		try (Connection conn = DriverManager.getConnection(dbUrl)) {
			if (conn != null) {
				Statement stmt = conn.createStatement();

				String instruction = "DELETE FROM computationSteps WHERE computationStepId=" + c.getID() + ";";
				instruction="DELETE FROM markings WHERE computationId=" + c.getID() + ";";
				stmt.execute(instruction);
				return true;
			}
			return false;

		} catch (SQLException e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}
}