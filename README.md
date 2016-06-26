# gestureinterpreter
A British Sign Language gesture interpreter using a [Leap Motion](https://www.leapmotion.com/) controller, written using Java.

Recognition is achieved using the [$P algorithm](https://depts.washington.edu/aimgroup/proj/dollar/pdollar.html).

Labelled gestures are performed by the user and saved to disk - these are then compared with new, unlabelled gestures in order to find the closest match.  

An example recognition use case is shown below:
![](http://orcworm.co.uk/image/2016-06-26_115753.png "Recognition")
Here, the application asks the user to perform the gesture for 'Z'. Instead, the user performs the 'C' gesture which is detected by the application, along with an accompanying similarity score. The objective is to correctly match as many gestures as possible within the time limit (60s). 

Additionally, an example recording use case is shown below:
![](http://orcworm.co.uk/image/2016-06-26_120803.png "Recording")
Here, the application cycles through a list of gestures, prompting the user to perform the current gesture shown. Once performed, these gestures are labelled accordingly and saved to be used in recognition. 

The application exhibited an accuracy rate of 100% in user dependant testing, using 3 known and 1 unknown data sets within the scope of the British Sign Language alphabet. These 3 known sets are included in the repository. 

The project formed part of my [dissertation](http://orcworm.co.uk/file/FYP_Report.pdf), investigating the uses of the Leap to aid in sign language communication. I now hope that the application will be of some use to others!
