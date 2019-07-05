package resources;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.*;


//This code executes when the apache server starts
public class OnStartup implements javax.servlet.ServletContextListener {
	private static final int UPDATE_WAIT_TIME = 864000; //time in seconds between updates
	
	/**
	 * if server starts: add thread to start autoUpdate
	 */
	public void contextInitialized(final ServletContextEvent event) {
		updateInitThread t = new updateInitThread();
		t.start();
    }
	
	/**
	 * upon shutdown: nothing has to happen, since all connections to database are closed immediately after use 
	 */
	public void contextDestroyed(final ServletContextEvent event) {
		System.out.println("server stopped or crashed");
	}
	
	/**
	 * multithread the starting of the autoUpdate, since it has to wait for http request
	 * while server is not yet started, giving an error
	 */
	private class updateInitThread extends Thread {
		public void run() {
			System.out.println("waiting");
			try {Thread.sleep(3000);}catch(InterruptedException e) {}
			System.out.println("waiting done");
			URL url;
			String path = Server.LINK + "/Project/rest/sql/autoupdate?time=" + UPDATE_WAIT_TIME;
			try {
				url = new URL(path);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				int req = con.getResponseCode();
				if (req == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					System.out.println(response.toString());
				} else {
					System.err.println("connection failed " + req);
				}
			} catch (MalformedURLException e) {
				System.err.println("MLFE");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("IOE");
				e.printStackTrace();
			}
		}
	}
}
