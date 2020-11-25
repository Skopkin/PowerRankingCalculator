package dataTypes;

// This Query type will represent a row in the query for the Rankings tab
public class RankQuery extends Query {
	
	private int rank;
	private String player;
	private String characters;
	
	public RankQuery(int rank, String player, String characters) {
		setRank(rank);
		setPlayer(player);
		setCharacters(characters);
	}
	
	void setRank(int i) {
		rank = i;
	}
	
	void setPlayer(String s) {
		player = s;
	}
	
	void setCharacters(String s) {
		characters = s;
	}
	
	public int getRank() {
		return rank;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public String getCharacters() {
		return characters;
	}
}

