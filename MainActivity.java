package com.example.poloar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;


import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.example.poloar.R;
import com.example.poloar.R.layout;
import com.example.poloar.R.string;

public class MainActivity extends Activity {
	ArrayList<video> currentServerData;
	LinkedHashMap<Integer,Integer> clientDataMap = new LinkedHashMap<Integer,Integer>();
	LinkedHashMap<Integer,Integer> serverDataMap= new LinkedHashMap<Integer,Integer>();
	public String z="";
	ArrayList<Integer> finals = new ArrayList<Integer>();
	boolean done=false;
	TextView t;
	View mProgress;
	Context c;
	GetServerManifest s;
	ArrayList<video> currentClientData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	    AssetsChecker asc = new AssetsChecker();
	    asc.execute();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			//getMenuInflater().inflate(layout.menu.main, menu);
			return true;		
	}
	
	private class AssetsChecker extends AsyncTask<Integer, Integer, Integer>{
			@Override
			protected void onPreExecute(){
				setContentView(R.layout.landing);		
			}		
			@Override
			protected Integer doInBackground(Integer... params) 
			{
				synchronized(this){			
				t=(TextView)findViewById(R.id.loading);
				try 
				{
					try{
						t.setText("Loading Ralph Lauren AR Experience");
						c = getApplicationContext();							
						GetVideoData g = new GetVideoData(c);
						try {
							//Get the current client data from the assets folder
							currentClientData = g.parseXML();
		
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();					
						}
						try{
							s = new GetServerManifest();
						} catch (Exception e){				
						}
						GetVideoData f = new GetVideoData(s.response);
						try{
							currentServerData = f.parseXML();			
						} catch(Exception e){
							e.printStackTrace();
						}					
					for(int i=0;i<currentClientData.size();i++){
						clientDataMap.put(currentClientData.get(i).identifierID, currentClientData.get(i).version);
					}
					for(int i=0;i<currentServerData.size();i++){
						serverDataMap.put(currentServerData.get(i).identifierID,currentServerData.get(i).version);
					}
					//Find out what's missing in the client's manifest
					for(int s:serverDataMap.keySet()){
						if(clientDataMap.containsKey(s)){
							///
							if(clientDataMap.get(s).equals(serverDataMap.get(s))){
								//
							}else{
								finals.add(s);
							}
						}
						else{
							finals.add(s);
						}
					}
					done=true;
					}	catch(Exception e){
						
					}
				} catch (Exception e) {
					MetaioDebug.printStackTrace(Log.ERROR, e);
					return 0;
				}
			}			
			if(finals.size()==0){
				return 1;
			}
			else{
				return 2;
			}
		}		
	
		@Override
		protected void onPostExecute(Integer result) {
			switch(result){			
				case 1:{
				    //App is up-to-date, start the AR stuff
				    AssetsEx mtask = new AssetsEx();
				    mtask.execute(0);
				} break;
				case 2:{
					//app not up to date, send user to update download screen
					//setContentView(R.layout.needupdate);
					AssetsEx mtask = new AssetsEx();
				    mtask.execute(0);
				} break;
			}
		}
	}
	
	private class AssetsEx extends AsyncTask<Integer, Integer, Boolean>{
			@Override
			protected Boolean doInBackground(Integer... arg0) {
				try 
				{
					AssetsManager.extractAllAssets(getApplicationContext(), true);
				} 
				catch (Exception e) 
				{
					MetaioDebug.printStackTrace(Log.ERROR, e);
					return false;
				}
				return true;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result){				
					new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try{
								Thread.sleep(4000);
							}catch(Exception e){							
							} finally{
							}
						}					
					});				
					setContentView(R.layout.instructions);
				}
			}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}	
	public void startARA(View v){
		Intent start = new Intent("com.example.poloar.ARA");
		startActivity(start);
	}	
}