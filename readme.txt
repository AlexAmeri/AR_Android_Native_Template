Augemented Reality App Template Code (Native Android Development)
=================================================================

@Author Alex Ameri

 The following code is a "template" which can be used for building AR apps using the Metaio computer vision API natively with Java. On startup, the
following happens:

(1) MainActivity's OnCreate() method is called, during which the central server is contacted and queried for product updates. Latest available product information is stored onboard the client device in the "Manifest.xml" file, which is compared to data downloaded from the server. If discrepancies exist (meaning there is more up to date data) the new product data is downloaded via the methods in dataDownloader class.

(2) The Metaio View is loaded. The Metaio API has a configuration file oboard the client device called "TrackingData/MarkerlessFast.xml" which is used to define target images for the API engine to recognize, as well as general settings. 

(3) When a target image is detected, it's corrosponding product movie is loaded, and the pointer to the OpenGL geometry for the movie is placed in a Vector (which is being used as a stack) which is only 3 units large, and the movie is displayed on the screen. When more than 3 movies have been loaded, the oldest movie's geometry is deleted, and everything else in the Vector is shifted down FIFO style. The purpose is to prevent RAM overuse.

(4) The file location of the movies to play is stored in the "Assets3/" directory of the assets folder.