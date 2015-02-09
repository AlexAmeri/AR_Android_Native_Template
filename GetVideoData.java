package com.example.poloar;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Xml;

public class GetVideoData {
	private XmlPullParser parser;
	ArrayList <video> videos;
	FileInputStream inf;
	InputStream in_s;
	String readString;
	public GetVideoData(Context c){
		// TODO Auto-generated method stub		
		//First step is to build an arraylist containing the device's current videos and their versions, the 
		//name is the key and the version is the value		
		XmlPullParserFactory parserFactory;		
		try{		
			parserFactory = XmlPullParserFactory.newInstance();
			parser = parserFactory.newPullParser();		
			//See if the manifest exists on the device's hard drive
				try{
					inf = c.openFileInput("info/clientmanifest.xml");
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					parser.setInput(inf, null);				
					InputStreamReader isr = new InputStreamReader(inf);
			        char[] inputBuffer = new char[60000];
			        isr.read(inputBuffer);
			        readString = new String(inputBuffer);				
				} catch(FileNotFoundException e){
					//File doesn't exist, write it but set input to what's in assets
					e.printStackTrace();
					File f = new File("info/clientmanifest.xml");
					in_s = c.getAssets().open("video_names/client_video_manifest.xml");
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					parser.setInput(in_s, null);
				} catch (Exception e){
					//Didn't work, just set input to what's in assets
					in_s = c.getAssets().open("video_names/client_video_manifest.xml");
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					parser.setInput(in_s, null);
					
				}
				//if the hard drive manifest file is empty, set input to what's in assets
				if(readString.equals("")||readString==null){
					in_s = c.getAssets().open("video_names/client_video_manifest.xml");
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
					parser.setInput(in_s, null);
				}	
		} catch (Exception e){			
		}
	}	
	
	public GetVideoData(String s){
		// TODO Auto-generated method stub		
		//First step is to build an arraylist containing the device's current videos and their versions, the 
		//name is the key and the version is the value
		XmlPullParserFactory parserFactory;		
		try{			
			parserFactory = XmlPullParserFactory.newInstance();
			parser = parserFactory.newPullParser();
			in_s = new ByteArrayInputStream(s.getBytes("UTF-8"));	
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);
		} catch (Exception e){			
		}
	}
	
	public ArrayList<video> parseXML() throws Exception{
		videos = null;
		parser = this.parser;
		int eventType = parser.getEventType();	
		video currentVideo=null;		
		while(eventType!=XmlPullParser.END_DOCUMENT){
			String n = null;
			switch(eventType){			
			case XmlPullParser.START_DOCUMENT:
					videos = new ArrayList();
					break;			
		    //Build the values of a product in the arraylist
			case XmlPullParser.START_TAG:
				n = parser.getName();
				if(n.equalsIgnoreCase("video")){
					currentVideo = new video();
				} else if (currentVideo!=null){
					if(n.equalsIgnoreCase("name")){
						currentVideo.name=parser.nextText();
					} 
					else if(n.equalsIgnoreCase("version")){
						currentVideo.version=Integer.parseInt(parser.nextText());
					} 
					else if(n.equalsIgnoreCase("identifierID")){
						currentVideo.identifierID=Integer.parseInt(parser.nextText());
					} 
					else if(n.equalsIgnoreCase("targetimage")){
						currentVideo.targetimage=parser.nextText();
					} 
					else if(n.equalsIgnoreCase("filepath")){
						currentVideo.filepath=parser.nextText();
					} 
				}
				break;
			case XmlPullParser.END_TAG:
				n=parser.getName();
				if (n.equalsIgnoreCase("video") && currentVideo != null){
                	videos.add(currentVideo);
                }	
			}
			eventType = parser.next();
		}		
		return videos;		
	}
}