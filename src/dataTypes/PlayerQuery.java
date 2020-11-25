package dataTypes;

import java.util.ArrayList;

// This Query type will represent a row in the query for the Rankings tab
public class PlayerQuery extends Query {
	
	private int id;
	private String firstName;
	private String lastName;
	private ArrayList<String> characters;
	private boolean active;
	
	public PlayerQuery(int i, String f, String l, ArrayList<String> c, boolean a) {
		setId(i);
		setFirstName(f);
		setLastName(l);
		setCharacters(c);
		setActive(a);
	}
	
	public int getId() {
		return id;
	}
	private void setId(int i) {
		this.id = i;
	}
	public String getFirstName() {
		return firstName;
	}
	private void setFirstName(String f) {
		this.firstName = f;
	}
	public String getLastName() {
		return lastName;
	}
	private void setLastName(String l) {
		this.lastName = l;
	}
	
	public ArrayList<String> getCharacters() {
		return characters;
	}
	private void setCharacters(ArrayList<String> characters) {
		this.characters = characters;
	}
	public int getCharacterCount() {
		return this.characters.size();
	}
	public boolean isActive() {
		return active;
	}
	private void setActive(boolean active) {
		this.active = active;
	}
}
