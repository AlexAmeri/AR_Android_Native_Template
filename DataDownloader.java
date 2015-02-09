package com.example.poloar;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class DataDownloader {
	public DataDownloader(){		
	}	
	public boolean download(int productID){		
		ProgressBack b = new ProgressBack();
		return b.execute(Integer.toString(productID)) != null;		
	}	
	class ProgressBack extends AsyncTask<String,String,String>
	{
		@Override
		protected String doInBackground(String... arg0) {
			try{
				File file = new File("pics/"+arg0+".jpg");
				URL u = new URL("http://alexameri.hobby-site.com/consumer/pictest.php");
				URLConnection ucon = u.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream ins = new BufferedInputStream(is,1024*5);
				FileOutputStream outStream = new FileOutputStream(file);
				byte[] buff = new byte[5*1024];				
				int len1=0;
				while((len1 = ins.read(buff))>0){
					outStream.write(buff,0,len1);						
				}
				//clean
				outStream.flush();
				outStream.close();
				ins.close();				
			} catch (Exception e){
				e.printStackTrace();
			}		
			return null;
		}		
	}
}