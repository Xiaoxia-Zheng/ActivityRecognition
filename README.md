# ActivityRecognition

 ## Walkthrough
 This is an activity recognition application for Android. 
 The app can distinguish three activities: laying down (sleeping), sitting and walking or running. 
 It's using the following sensors: accelerometer, gyroscope and location. 
 
 1. The UI will display the last 1-10 activities that with timestamps. Also the App will write to a file on external storage 
 that will keep track of the activities that the system infers.

2) Main activity will spawn a service (bound service) that will be collecting the sensor data continuously. 
The service will chunk the data collected into time intervals that you set and run an algorithm that determines 
the activity for that time intervals. The activities and 
the time period can be stored locally in the service in a data structure. The main activity at regular intervals 
would poll for the set of activities from the service and write it to external storage.
 
  ## App Screenshot
  
 <img src="https://github.com/Xiaoxia-Zheng/ActivityRecognition/blob/master/AppScreenshot/Screenshot_2016-03-25-11-17-25.jpeg" 
 alt="Smiley face" height="400">
 
 
 ## Demo video
https://www.youtube.com/watch?v=lmC6gxj16Hk&t=4s
