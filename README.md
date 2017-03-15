# gestureinterpreter
A British Sign Language gesture interpreter using a [Leap Motion](https://www.leapmotion.com/) controller, written using Java.

Recognition is achieved using the [$P algorithm](https://depts.washington.edu/aimgroup/proj/dollar/pdollar.html).

Labelled gestures are performed by the user and saved to disk - these are then compared with new, unlabelled gestures performed by the user in order to find the closest match. 

The basis of the application is a simple matching game. The user is prompted to perform a number of random gestures within a specific time frame, amounting points for each successful gesture match. The goal of the application is to evaluate the feasibility of the Leap Motion when interpreting sign language gestures, as well as the performance of the $P when handling Leap Motion data. 

An example recognition use case is shown below:
![](http://orcworm.co.uk/image/2016-06-26_115753.png "Recognition")
Here, the application asks the user to perform the gesture for 'Z'. Instead, the user performs the 'C' gesture which is detected by the application, along with an accompanying match score of 87%.

Additionally, an example recording use case is shown below:
![](http://orcworm.co.uk/image/2016-06-26_120803.png "Recording")
Here, the application cycles through a list of gestures, prompting the user to perform the current gesture shown. Once performed, these gestures are labelled accordingly and saved to be used in recognition. 


The application exhibited an accuracy rate of 100% in user dependant testing, using 3 known and 1 unknown data sets within the scope of the British Sign Language alphabet. These 3 known sets are included in the repository. 

The project formed part of my [dissertation](http://orcworm.co.uk/file/FYP_Report.pdf), investigating the uses of the Leap Motion as an aid in sign language communication. I now hope that the application will be of some use to others!
