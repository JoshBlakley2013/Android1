package com.example.android.networkusage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.example.android.networkusage.GymnastXmlParser.Gymnast;


public class NetworkActivity extends Activity{
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";
	public static final String URL = "http://tmmdisco.co.uk/gymnastsData.xml";
	
	//Check for connections
	private static boolean wifiConnected = false;
	private static boolean mobileConnected = false;
	public static boolean refreshDisplay = true;
	public static String sPref = null;

	
	//Use ASyncTask to download the XML data
	public void loadPage(){
		if ((sPref.equals(ANY)) && (wifiConnected || mobileConnected)){
			new DownloadXmlTask().execute(URL);
		}else if((sPref.equals(WIFI)) && (wifiConnected)){
			new DownloadXmlTask().execute(URL);
		}else{
			//show error
		}
	}
	
	//implement the ASyncTask
	private class DownloadXmlTask extends AsyncTask<String,Void,String>{
		protected String doInBackground(String... urls){
			try{
				return loadXmlFromNetwork(urls[0]);
			} catch(IOException e){
				return getResources().getString(R.string.connection_error);
			} catch (XmlPullParserException e){
				return getResources().getString(R.string.xml_error);
			}
		}
		protected void onPostExecute(String result){
			setContentView(R.layout.main);
			//Displays the HTML String in the UI via WebView
			WebView myWebView = (WebView) findViewById(R.id.webview);
			myWebView.loadData(result, "text/html", null);
		}
	}
	
	//XML from website, parsing it and combining with HTML return HTML
	private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
		InputStream stream = null;
		//Instantiate the parser
		GymnastXmlParser gymnastXmlParser = new GymnastXmlParser();
		List<Gymnast> gymnasts = null;
		String name = null;
		String id = null;
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean pref = sharedPrefs.getBoolean("summaryPref", false);
		
		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<h3>"+ getResources().getString(R.string.page_title)+ "</h3>");
		htmlString.append("<em>"+ getResources().getString(R.string.updated)+ "</em>");
		
		try{
			stream = downloadUrl(urlString);
			gymnasts = gymnastXmlParser.parse(stream);
			//make sure the InputStream is closed
		}finally{
			if(stream != null){
				stream.close();
			}
		}
		for(Gymnast gymnast: gymnasts){
			htmlString.append("<p>");
			htmlString.append(gymnast.name);
			htmlString.append("</p>");
			if(pref){
				htmlString.append(gymnast.id);
			}
		}
		return htmlString.toString();
	}
	//get the stream from the string url
	private InputStream downloadUrl(String urlString) throws IOException{
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		//start the query
		conn.connect();
		return conn.getInputStream();
	}
	
	
}