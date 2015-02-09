package com.example.poloar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class compareData {
	private LinkedHashMap <Integer, Integer> server;
	private LinkedHashMap <Integer, Integer> client;
	private ArrayList<video> clientData;
	private ArrayList<video> serverData;
	private ArrayList <Integer> updateIndexes;	
	public compareData(LinkedHashMap <Integer, Integer> server,LinkedHashMap <Integer, Integer> client, ArrayList<video> clientStuff, ArrayList<video> serverStuff){
		this.server=server;
		this.client=client;
		this.clientData=clientStuff;
		this.serverData=serverStuff;	
	}
	
	public ArrayList<Integer> compare(){	
		//Loop through the server hashmap
		for(int serverKey:server.keySet()){
			//Check to see if the client has a product for this index
			if(client.containsKey(serverKey)){
			//case:true
				//Check if version is the same
				if(client.get(serverKey).equals(server.get(serverKey))){
					//versions are the same, so do nothing
				}
				else {
					//Client does not have up to date data for this product, download it!
					updateIndexes.add(serverKey);					
				}				
			}
			else {
				//Client does not have data for this product, download it!
				updateIndexes.add(serverKey);
			}
		}
		return updateIndexes;
	}	
}
