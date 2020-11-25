package dataTypes;

import java.util.Date;

//This Query type will represent a row in the query for the Tourney tab
public class TourneyQuery extends Query{

	private Date date;
	private String tourney;
	private int entries;
	private double version;
	
	public TourneyQuery(Date d, String t, int e, double v) {
		setDate(d);
		setTourney(t);
		setEntries(e);
		setVersion(v);
	}

	public Date getDate() {
		return date;
	}

	void setDate(Date date) {
		this.date = date;
	}

	public String getTourney() {
		return tourney;
	}

	void setTourney(String tourney) {
		this.tourney = tourney;
	}

	public int getEntries() {
		return entries;
	}

	void setEntries(int entries) {
		this.entries = entries;
	}

	public double getVersion() {
		return version;
	}

	void setVersion(double version) {
		this.version = version;
	}
	
	public boolean compareQuery(TourneyQuery other) {
		if (this.date != other.getDate()) {
			return false;
		}
		if (this.tourney != other.getTourney()) {
			return false;
		}
		if (this.entries != other.getEntries()) {
			return false;
		}
		if (this.version != other.getVersion()) {
			return false;
		}
		
		return true;
	}
}
