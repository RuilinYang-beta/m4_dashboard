package nl.utwente.di.SQL;

import java.util.List;

import org.json.JSONObject;

import java.util.ArrayList;

public class SQLThread extends Thread {
	private Database d;
	private List<String> data;
	private List<JSONObject> dataJson;
	private String json = "[";
	private boolean done = false;
	private String goal = "";
	private int customer = -1;
	
	public SQLThread(Database d, String goal, int customer) {
		this.d = d;
		this.data = new ArrayList<String>();
		this.dataJson = new ArrayList<JSONObject>();
		this.goal = goal;
		this.customer = customer;
	}
	
	private String getLabels(String opts) {
		String[] labels = opts.split(",");
		String res = "";
		for (int i = 0; i < labels.length; i ++) {
			res += labels[i].split(" ")[0] + ",";
		}
		return res.substring(0, res.length()-1);
	}
	
	public void parse(String res) {
		done = false;
		if (!json.equals("[")) {
			json += ",";
		}
		json +=  res.substring(1, res.length()-1);
		done = true;
	}
	
	
	public void run() {
		while (!done) {
			try {Thread.sleep(20);}catch(InterruptedException e) {}
		}
		if (goal.equals("actions")) {
			data.addAll(Database.parseActions(json + "]"));
			d.insertAction(data);
		} else if (goal.equals("bookings")) {
			dataJson.addAll(Database.parse(json + "]"));
			String command = "INSERT INTO bookings (id,"+ getLabels(Database.OPT_BOOK) + ") VALUES(" + customer + ",";
			String opts = Database.OPT_BOOK;
			d.insertDatabase(dataJson, command, opts);
			System.out.println("done");
		}
		
	}
}
