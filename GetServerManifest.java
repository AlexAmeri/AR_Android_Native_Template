package com.example.poloar;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class GetServerManifest {	
	public String response;
	public ArrayList<video> videos;
	XmlPullParser parser;
	
	public GetServerManifest(){
		try{				 	        
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse responses = httpclient.execute(new HttpGet("http://mcveg-food.com/MC_Veg_Food/getmanifest.php"));
	        StatusLine statusLine = responses.getStatusLine();
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            responses.getEntity().writeTo(out);
	            out.close();
	            response = out.toString().replaceAll("\\s", "");
	            //..more logic
	        } else{
	            //Closes the connection.
	            responses.getEntity().getContent().close();
	            throw new IOException(statusLine.getReasonPhrase());
	        }
		} catch (Exception e6){
			e6.printStackTrace();
		}
		XmlPullParserFactory parserFactory;	
		try{
			parserFactory = XmlPullParserFactory.newInstance();
			parser = parserFactory.newPullParser();
			InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));		
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(is,null);	
		} catch (Exception e9){
			e9.printStackTrace();
		}
	}
}