package com.example.poloar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.poloar.ARViewActivity;
import com.example.poloar.R.id;
import com.example.poloar.R.layout;
import com.example.poloar.R.drawable;


import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.EPLAYBACK_STATUS;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.MovieTextureStatus;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class ARA extends ARViewActivity implements Callback {
	
	public IGeometry activeGeometry;
	public int activeIndex;
	ImageView logo;
	TextView t;
	boolean found=false;
	int count=0;
	ARLayout lay;
	LinearLayout prog;
	LinearLayout progress;
	ProgressBar p;
	Vector<Boolean> isPaused;
	boolean floating = false;
	TextView prodName;
	LinearLayout prodName2;
	Button toggle;
	Button stop;
	Button buy;
	LayoutParams buyB;
	LayoutParams stopbutton;
	LayoutParams gsight;
	ImageView gunsight;
	LayoutParams to;
	LayoutParams pb;
	
	Vector<IGeometry> movieStack;
	Vector<String> movieNames;
	IGeometry imagePlane;
	boolean paused=true;
	private int yVal = 200;
	
    ScaleGestureDetector SGD;
	private int distance = 0;
	private final long sleepTime = 10;
	private boolean fullsize = false;
	private String toggleString;
	private String toggleStringPortrait;
	int orientation = 1;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//LOADING SCREEN GUI
		t = new TextView(this);
		t.setGravity(Gravity.CENTER);
		lay = new ARLayout(this);
		lay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		p = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
	    prog = new LinearLayout(this);
	    prog.setGravity(Gravity.CENTER_HORIZONTAL);
	    LayoutParams li = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);	
	    li.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    prog.setLayoutParams(li);
	    li.setMargins(0, 0, 0, 20);		
		prog.setOrientation(LinearLayout.VERTICAL);		
		progress = new LinearLayout(this);
		progress.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		progress.setGravity(Gravity.CENTER_HORIZONTAL);		
		progress.addView(p);
		prog.addView(t);
		prog.addView(progress);
		lay.addView(prog);		
        lay.setBackgroundResource(drawable.logo_final);

		//NOW DO THE GUI FOR PLAYING MOVES
		prodName = new TextView(this);
		toggle = new Button(this);
		stop = new Button(this);
		buy = new Button(this);
		gunsight = new ImageView(this);
		prodName2 = new LinearLayout(this);
		LayoutParams pb = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams to = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams stopbutton = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams buyB = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams gsight = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LayoutParams prod = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		pb.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		pb.addRule(RelativeLayout.CENTER_HORIZONTAL);
		to.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		to.addRule(RelativeLayout.CENTER_HORIZONTAL);
		buyB.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		buyB.addRule(RelativeLayout.CENTER_HORIZONTAL);
		stopbutton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		stopbutton.addRule(RelativeLayout.CENTER_HORIZONTAL);
		gsight.addRule(RelativeLayout.CENTER_HORIZONTAL);
		gsight.addRule(RelativeLayout.CENTER_VERTICAL);
		to.setMargins(0,0,0,80);
		pb.setMargins(0, 10, 0, 0);
		stopbutton.setMargins(0,0,0,10);
		buyB.setMargins(0, 0, 0, 150);
		prod.setMargins(20, 20, 20, 20);
		prodName.setGravity(Gravity.CENTER_HORIZONTAL);
		prodName.setLayoutParams(prod);
		prodName2.setBackgroundResource(drawable.shape);
		toggle.setBackgroundResource(drawable.shape);
		stop.setBackgroundResource(drawable.shape);
		buy.setBackgroundResource(drawable.shape);

		//INITIALIZE MOVIE GUI 
		toggle.setVisibility(LinearLayout.GONE);
		stop.setVisibility(LinearLayout.GONE);
		gunsight.setVisibility(LinearLayout.GONE);
		buy.setVisibility(LinearLayout.GONE);
		prodName2.setVisibility(LinearLayout.GONE);
		prodName2.setLayoutParams(pb);
		prodName2.addView(prodName);
		toggle.setLayoutParams(to);
		stop.setLayoutParams(stopbutton);
		gunsight.setImageResource(R.drawable.gunsight);
		gunsight.setLayoutParams(gsight);
		buy.setLayoutParams(buyB);
		lay.addView(prodName2);
		lay.addView(gunsight);
		lay.addView(buy);
		lay.addView(toggle);
		lay.addView(stop);		
		gunsight.getLayoutParams().height = 0;
		gunsight.getLayoutParams().width = 0;		
		
		//Set the event listeners for all of the movie buttons
		toggle.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				doMovieAction();				
			}			
		});
		buy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			       Uri uriUrl = Uri.parse("http://www.ralphlauren.com/shop/index.jsp?categoryId=32956236");
			       Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
			       startActivity(launchBrowser);
			}
		});
		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				activeGeometry.setCoordinateSystemID(1);
				activeGeometry = null;
			}
		});
		// GUI UPDATE AS PER MICHAEL'S REQUEST - MAKE BUTTONS ALIGNED ON BOTTOM BY DEFAULT
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)toggle.getLayoutParams();
		params1.setMargins(0, 0, 0, 10);
		try {
			params1.removeRule(RelativeLayout.CENTER_HORIZONTAL);
		} catch (Exception e) { 							
		}
		params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		toggle.setLayoutParams(params1);
		toggle.requestLayout();			
		RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)buy.getLayoutParams();
		params2.setMargins(0, 0, 0, 10);
		try {
			params2.removeRule(RelativeLayout.CENTER_HORIZONTAL);
		} catch (Exception e) { 							
		}
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		buy.setLayoutParams(params2);
		// END GUI UPDATE REQUEST		
		// Initialize the movie stack (Really a vector
		// because of API requirements, but used as a stack)
		movieStack = new Vector();
		movieNames = new Vector();
		isPaused = new Vector();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		mGUIView=lay;
		SGD = new ScaleGestureDetector(this,new ScaleListener());
	}
	
	@Override
	protected int getGUILayout() {
		// TODO Auto-generated method stub		
		return 0;		
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected void loadContents() {
		// TODO Auto-generated method stub
		//load tracking info
		String trackingConfigFile = AssetsManager.getAssetPath("Assets3/TrackingData_MarkerlessFast.xml");
		boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
		final String imagePath = AssetsManager.getAssetPath("Assets3/loading.jpg");
		if (imagePath != null)
		{
			imagePlane = metaioSDK.createGeometryFromImage(imagePath, false);
			if (imagePlane != null)
			{
				imagePlane.setScale(4.0f);
				MetaioDebug.log("Loaded geometry "+imagePath);
			}
			else {
				MetaioDebug.log(Log.ERROR, "Error loading geometry: "+imagePath);
			}
		}	
	}
	@Override
	public void onDrawFrame() {
		// TODO Auto-generated method stub
		count+=1;
		//Initialization screen
		if(count<2)
			initGUI();
		else if(count==2)
			runningGUI();
		else if(count>2){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			//Get target values and display them
			final TrackingValuesVector track = metaioSDK.getTrackingValues();
			if(track.size()>0){
				 floating = false;
				 final TrackingValues v = track.get(0);
	             if (v.isTrackingState())
	             {   	            	     
	                    getMovie(v.getCosName(),v.getCoordinateSystemID());
	             }
				 if(activeGeometry != null) {
				 	 	activeGeometry.setCoordinateSystemID(1);
				 	 	activeGeometry.setTranslation(new Vector3d(0,0,0));
				  }
				  distance = 0;
			} else{
				if(activeGeometry != null) {
					floating = true;
					activeGeometry.setCoordinateSystemID(0);
					activeGeometry.setTranslation(new Vector3d(0,yVal,-1800 + distance));
					drawToggle();
				} else {
					targetGUI(false);
					distance = 0;
				}
			}			
		}
		super.onDrawFrame();
	}
	private void drawToggle() {
		final MovieTextureStatus status = activeGeometry.getMovieTextureStatus();
		runOnUiThread( new Runnable() {
			@Override
			public void run() {
				if (status.getPlaybackStatus() == EPLAYBACK_STATUS.EPLAYBACK_STATUS_PLAYING){
					if (orientation == 0){ toggle.setText("Tap Here to Pause");} else { toggle.setText("Pause");}
				}
				else{
					if (orientation == 0){ toggle.setText("Tap Here to Play");} else { toggle.setText("Play");}
				}
			}
		});
	}
	public void initGUI(){
		runOnUiThread(new Runnable()
		{
			@Override
			public void run(){				
					t.setText("Loading media content, please wait...\n(This takes about a minute on most smartphones)");			
			}
		});
	}
	
	public void runningGUI(){
		runOnUiThread(new Runnable()
		{
			@Override
			public void run(){				
					lay.setBackgroundResource(0);
					prog.setVisibility(LinearLayout.GONE);

			}
		});
	}
	
	public void getMovie(String name, int id){	
		//First check to see if the movie is already loaded
		boolean haveMovie=false;
		for(int i=0;i<movieNames.size();i++)
			if(movieNames.get(i).equals(name)){
				haveMovie=true;
				if(isPaused.get(i)==false){
					//its currently playing
					targetGUI(name,"Tap Here to Pause");
					this.toggleStringPortrait = "Pause";
					this.toggleString = "Tap Here to Pause";
				}else{
					//its currently paused
					targetGUI(name,"Tap Here to Play");
					this.toggleStringPortrait = "Play";
					this.toggleString = "Tap Here to Play";
				}
				activeGeometry=movieStack.get(i);
				activeIndex=i;
			}		
			//If it isn't,load the movie
			//Set the loading screen
			if(haveMovie==false){
			imagePlane.setCoordinateSystemID(id);
			imagePlane.setVisible(true);
			
			//Load Movie
					
			final String moviepath = AssetsManager.getAssetPath("Assets3/" + name + ".3g2");
			if (moviepath != null)
			{
				targetGUI(name,"Loading movie, please wait...");			
				final String cname = name;
				final int fid = id;			
				//new process on openGL Thread to load movie
				mSurfaceView.queueEvent(new Runnable(){
					@Override
					public void run(){
					final IGeometry newMovie = metaioSDK.createGeometryFromMovie(moviepath, false);			
					if (newMovie != null)
					{
						newMovie.setScale(10.0f);
						//mMoviePlane.setRotation(new Rotation(0f, 0f, (float)-Math.PI/2));
						MetaioDebug.log("Loaded geometry "+moviepath);				
						//If the movie stack is smaller than 3, add to end, else, remove the earliest and replace it FIFO style				
						if(movieStack.size()<=3){
						 movieStack.add(newMovie);
						 movieNames.add(cname);
						 isPaused.add(true);
						} else{
							//Geometry
							movieStack.add(0, newMovie);
							isPaused.add(0,true);
							newMovie.stopMovieTexture();
							//remove the oldest entry, which is now at index 1
							//but first nullify it
							movieStack.get(1).stopMovieTexture();
							metaioSDK.unloadGeometry(movieStack.get(1));
							movieStack.remove(1);
							isPaused.remove(1);					
							//Names
							movieNames.add(0, cname);
							//remove the oldest entry, which is now at index 1
							movieNames.remove(1);
						}
										
						//attach the movie to the target image
						imagePlane.setVisible(false);
						newMovie.setCoordinateSystemID(fid);
						activeGeometry=newMovie;
						if(newMovie.getCoordinateSystemID()==fid){
							targetGUI(cname,"Movie attached...");
						}
						paused=true;				
					}
					else {
						MetaioDebug.log(Log.ERROR, "Error loading geometry: "+moviepath);
					}			
				}
			});		
			}
		}
	}
	
	public void targetGUI(String name, String info){		
		final String cname = name;
		final String cinfo = info;			
		runOnUiThread(new Runnable(){			
			@Override
			public void run(){				
				//set the text of the gui
				prodName2.setVisibility(LinearLayout.VISIBLE);
				prodName.setText("   "+cname+"   ");
				gunsight.setVisibility(LinearLayout.INVISIBLE);
				gunsight.getLayoutParams().height = 0;
				gunsight.getLayoutParams().width = 0;
				fullsize = false;
				toggle.setVisibility(LinearLayout.VISIBLE);
				stop.setVisibility(LinearLayout.VISIBLE);
				buy.setVisibility(LinearLayout.VISIBLE);
				if (orientation == 0) {
					toggle.setText(toggleString);
					stop.setText("Tap Here to Stop Playing");
					buy.setText("Tap Here to Buy");
				} else {
					toggle.setText(toggleStringPortrait);
					stop.setText("Stop");
					buy.setText("Buy");	
				}
			}			
		});
	}
	
	public void targetGUI(boolean show){
		runOnUiThread(new Runnable(){			
			@Override
			public void run(){
				if(count>180) 
					prodName2.setVisibility(LinearLayout.VISIBLE);
				if(count < 180) {
					gunsight.setVisibility(LinearLayout.INVISIBLE);
				} else {
					gunsight.setVisibility(LinearLayout.VISIBLE);
				}
				prodName.setText(" Fit a Ralph Lauren Target Image inside the Square ");
				toggle.setVisibility(LinearLayout.INVISIBLE);
				stop.setVisibility(LinearLayout.INVISIBLE);
				buy.setVisibility(LinearLayout.INVISIBLE);
			}			
		});
		if(count > 181 && !fullsize) {
			gunsightAnimation();
			fullsize  = true;
		}
	}
    
	public void doMovieAction(){	   	   
	   if(activeGeometry!=null){
		   final MovieTextureStatus status = activeGeometry.getMovieTextureStatus();
		   if (status.getPlaybackStatus() == EPLAYBACK_STATUS.EPLAYBACK_STATUS_PLAYING){
				isPaused.set(activeIndex, !(isPaused.get(activeIndex)));
				activeGeometry.pauseMovieTexture();
		    }
		    else {
				isPaused.set(activeIndex, !(isPaused.get(activeIndex)));
				activeGeometry.startMovieTexture(true);			
			}
	   }
   }
	
   @Override
   public void onConfigurationChanged(Configuration newConfig) {
    	Log.i("com.example.poloar", "screen tilted");
    	distance = 0;
    			if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    				yVal = 0;
    				runOnUiThread(new Runnable(){
    					public void run() {
    						RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)toggle.getLayoutParams();
    						params1.setMargins(0, 0, 0, 10);
    						try {
    						params1.removeRule(RelativeLayout.CENTER_HORIZONTAL);
    						} catch (Exception e) { 							
    						}
    						params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    						toggle.setLayoutParams(params1);
    						toggle.requestLayout();
    						
    						RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)buy.getLayoutParams();
    						params2.setMargins(0, 0, 0, 10);
    						try {
    						params2.removeRule(RelativeLayout.CENTER_HORIZONTAL);
    						} catch (Exception e) { 							
    						}
    						params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    						buy.setLayoutParams(params2);
    						buy.requestLayout();
    						stop.setText("Tap Here to Stop");
    						buy.setText("Tap Here to Buy");
    						orientation = 0;
    					}
    				});
    			} else {
    				yVal = 200;
    				runOnUiThread(new Runnable(){
    					public void run() {
    						RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)toggle.getLayoutParams();
    						params1.setMargins(0, 0, 0, 10);
    						try {
    						params1.removeRule(RelativeLayout.CENTER_HORIZONTAL);
    						} catch (Exception e) { 							
    						}
    						params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    						toggle.setLayoutParams(params1);
    						toggle.requestLayout();
    						
    						RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)buy.getLayoutParams();
    						params2.setMargins(0, 0, 0, 10);
    						try {
    						params2.removeRule(RelativeLayout.CENTER_HORIZONTAL);
    						} catch (Exception e) { 							
    						}
    						params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    						buy.setLayoutParams(params2);
    						buy.requestLayout();
    						stop.setText("Stop");
    						buy.setText("Buy");
    						orientation = 1;
    					}
    				});
    			}
    	super.onConfigurationChanged(newConfig);
    }
    
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub


	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.i("PoloAR","touch detected");
		SGD.onTouchEvent(ev);
		return true;
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		   @Override
		   public boolean onScale(ScaleGestureDetector detector) {
			  Log.i("PoloAR","pinch detected");
		      if( detector.getScaleFactor() < 1f) {
		    	  distance -= 14;
		      } else {
		    	  distance += 14;
		      }
		      return true;
		   }
		   @Override
		   public boolean onScaleBegin(ScaleGestureDetector detector) {
			   return true;
		   }
		   @Override
		   public void onScaleEnd(ScaleGestureDetector detector) {
		   }
	}
	private class ARLayout extends RelativeLayout {
		public ARLayout(Context c) {
			super(c);
		}
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			Log.i("PoloAR","touch detected");
			SGD.onTouchEvent(ev);
			return true;
		}
	}
	
	private void gunsightAnimation() {
	 new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted() && gunsight.getLayoutParams().width < 375)
                try {
                    Thread.sleep(10);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            gunsight.getLayoutParams().width += 3;
                            gunsight.getLayoutParams().height += 3;
                            gunsight.requestLayout();
                        }
                    });
                }
                catch (InterruptedException e){
                    // ooops
                }
        	}
	 	}).start(); 
	}
}
