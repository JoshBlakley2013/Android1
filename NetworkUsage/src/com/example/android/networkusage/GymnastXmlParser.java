package com.example.android.networkusage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class GymnastXmlParser{
	private static final String ns = null;
	
	public List parse(InputStream in) throws XmlPullParserException, IOException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in,null);
			parser.nextTag();
			return readFeed(parser);
		}finally{
			in.close();
		}
	}
	private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
		List gymnasts = new ArrayList();
		
		parser.require(XmlPullParser.START_TAG, ns, "gymnast");
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			//look for the gymnast tag first
			if(name.equals("gymnast")){
				gymnasts.add(readGymnast(parser));
			}else{
				skip(parser);
			}
		}
		return gymnasts;
	}
	public static class Gymnast{
		public final String name;
		public final String id;
		
		private Gymnast(String name, String id){
			this.name = name;
			this.id = id;
		}
	}
	//Parses the content of the gymnast entry if it see's it parses if no skips
	
	private Gymnast readGymnast(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "gymnast");
		String name= null;
		String id =null;
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.next() !=XmlPullParser.START_TAG){
				continue;
			}
			String pName= parser.getName();
			if (pName.equals("name")){
				name= readName(parser);
			}else if(pName.equals("id")){
				id = readId(parser);
			}else{
				skip(parser);
			}
		}
		return new Gymnast(name, id);
	}
	//process the gymnast name
	private String readName(XmlPullParser parser) throws IOException, XmlPullParserException{
		parser.require(XmlPullParser.START_TAG, ns, "name");
		String name = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "name");
		return name;
	}
	//prcoess the gymnasts id
	private String readId(XmlPullParser parser) throws IOException, XmlPullParserException{
		parser.require(XmlPullParser.START_TAG, ns, "id");
		String name = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "id");
		return name;
	}
	
	//Extract the text from tha tags
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException{
		String result ="";
		if (parser.next() == XmlPullParser.TEXT){
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
		if (parser.getEventType() != XmlPullParser.START_TAG){
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0){
			switch (parser.next()){
			case XmlPullParser.END_TAG:
				depth --;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}