package com.example.gymnasticsprogressmonitor;

import java.io.IOException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class activity_main extends Activity {
	public static final String TAG = "project test";
	public static final String QUERY_URL = "http://tmmdisco.co.uk/gymnastsData.xml";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncDownloader downloader = new AsyncDownloader();
        downloader.execute();
         }
	
	private void handleNewRecord(String name){
		TextView textView = (TextView) findViewById(R.id.gymnastId);
		String message = name;
		
		textView.setText(message);
	}
	
	
	
	
	
	
	
	//Inner class for doing the background download
	private class AsyncDownloader extends AsyncTask<Object, String, Integer>{

		@Override
		protected Integer doInBackground(Object... arg0) {
			XmlPullParser recievedData = tryDownloadingXmlData();
			Integer recordsFound = tryParsingXmlData(recievedData);
			return recordsFound;
		}
		private XmlPullParser tryDownloadingXmlData() {
			// TODO Auto-generated method stub
			try{
				URL xmlURL= new URL(QUERY_URL);
				XmlPullParser recievedData = XmlPullParserFactory.newInstance().newPullParser();
				recievedData.setInput(xmlURL.openStream(), null);
				return recievedData;
			}catch(XmlPullParserException e){
				Log.e(TAG, "XmlPullParserException", e);
			}catch(IOException e){
				Log.e(TAG, "XmlPullParserException", e);
			}
			return null;
			
		}
		private int tryParsingXmlData(XmlPullParser recievedData) {
			if(recievedData !=null){
				try{
					return processRecievedData(recievedData);
				}catch(XmlPullParserException e){
					Log.e(TAG, "Pull parsing XML", e);
				}catch(IOException e){
					Log.e(TAG, "IO Exception parsing XML", e);
				}
			}
			return 0;
		}

		

		private int processRecievedData(XmlPullParser xmlData) throws XmlPullParserException, IOException {
			int eventType= -1;
			int recordsFound=0;
			
			//Find Value in the XML records
			String name ="";
			String data = " ";
			
			while(eventType != XmlResourceParser.END_DOCUMENT){
				String tagName = xmlData.getName();
				
				switch(eventType){
				case XmlResourceParser.START_TAG:
					//Start of a record so full the values encoded
					if(tagName.equals("gymnast")){
						name = xmlData.getAttributeValue(null, "name");
						data = " ";
					}
					break;
					
					//grab data text(very simple processing)
				case XmlResourceParser.TEXT:
					data += xmlData.getText();
					break;
				case XmlPullParser.END_TAG:
					if(tagName.equals("gymnast")){
						recordsFound++;
						publishProgress(name);
					}
					break;
				}
				eventType= xmlData.next();
			}
			//Handle no data available : Publish an empty event.
			if(recordsFound ==0){
				publishProgress();
			}
			return recordsFound;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			if(values.length==0){
				Log.i(TAG, "No Datra Downloaded");
			}
			if(values.length==4){
				String name= values[0];
				
				//Log it!
				
				Log.i(TAG, "name=" + name);
				//pass it to the application
				handleNewRecord(name);
			}
			super.onProgressUpdate(values);
		}
		
		
		
	}
	
     
}
