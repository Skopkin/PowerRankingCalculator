package dataTypes;

// This Query type will represent a row in the query for the Rankings tab
public class CharacterQuery extends Query {

	private String name;
	private String series;
	private boolean dlc;
	
	public CharacterQuery(String n, String s, boolean d) {
		setName(n);
		setSeries(s);
		setDlc(d);
	}
	
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getSeries() {
		return series;
	}
	private void setSeries(String series) {
		this.series = series;
	}
	public boolean isDlc() {
		return dlc;
	}
	private void setDlc(boolean dlc) {
		this.dlc = dlc;
	}
	public boolean compareCharacter(CharacterQuery other) {
		if (this.name != other.getName()) {
			return false;
		}
		else if (this.series != other.getSeries()) {
			return false;
		}
		else if (this.dlc != other.isDlc()) {
			return false;
		}
		else {
			return true;
		}
	}
}
