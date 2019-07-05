package nl.utwente.di.SQL;

import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;

import DAO.*;

public class SQLThread extends Thread {
	private List<String> data;
	private List<JSONObject> dataJson;
	private String json = "[";
	private boolean done = false;
	private String goal = "";
	private int customer = -1;
	
	/**
	 * Initialization: needs to know what the goal table will be, because it parses data differently
	 * if it is actions or bookings
	 * @param goal  the destination table: bookings, actions, linestops or locations
	 * @param customer  customer id: saved in table for querying purposes
	 */
	public SQLThread(String goal, int customer) {
		this.data = new ArrayList<String>();
		this.dataJson = new ArrayList<JSONObject>();
		this.goal = goal;
		this.customer = customer;
	}
	
	/**
	 * puts all options after each other for the creation of a query
	 * eg INSERT INTO table (getLabels(opts)) values (a,b,c)
	 * @param opts
	 */
	private String getLabels(String opts) {
		String[] labels = opts.split(",");
		String res = "";
		for (int i = 0; i < labels.length; i ++) {
			res += labels[i].split(" ")[0] + ",";
		}
		return res.substring(0, res.length()-1);
	}
	
	/**
	 * locks the miltiThread and saves the given data locally so that when Thread.start() is called
	 * it can parse all data at once
	 * @param res
	 */
	public void parse(String res) {
		done = false;
		if (!json.equals("[")) {
			json += ",";
		}
		json +=  res.substring(1, res.length()-1);
		done = true;
	}
	
	/**
	 * first checks if thread is locked (meaning that data is being saved
	 * if not locked, it starts parsing and inserting in database
	 */
	public void run() {
		while (!done) {
			try {Thread.sleep(20);}catch(InterruptedException e) {}
		}
		if (goal.equals("actions")) {
			data.addAll(Database.parseActions(json + "]"));
			DAOinsert.insertAction(data, customer);
		} else if (goal.equals("bookings")) {
			dataJson.addAll(Database.parse(json + "]"));
			String command = "INSERT INTO bookings (id,"+ getLabels(Database.OPT_BOOK) + ") VALUES(" + customer + ",";
			String opts = Database.OPT_BOOK;
			DAOinsert.insertDatabase(dataJson, command, opts);
		}
		System.out.println("done");
	}
}
