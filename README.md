# Red-Light-Warning-System
This is a project made by a small group of 4 people.
It is a project made for OSU capstone.
It is best running on Andriod Studio with latest version of JAVA API and gradle.
In Java folder their are four files.


# Algorithm
This class take the prediction data and the speed and utilizes math to determine whether the user has time to stop before running the light at any point. This is of course applied liberaly so that the warning is sounded so they have time to slow down and stop.

# GeoReferenced_web_request
This is the core of our app. It handles most of the information, and utilizes other classes to determine if a warning sound needs to be emited.

It utilizes built in android libraries to get the speed and dirtection, then send this information to the TTS API. The tts API then responds with json listing upcoming lights, as well as what color they are, where there stoplines are, and when they are going to turn. This information is parsed and loaded into the prediction class, where the information is easily accesible.

The speed and prediction is entered into the algorithm class, this class return a simple respone, whetther to emit a sound or not.

This whole process is repeated once per second.


# Login
This is the first class and instance which is interacted with. In this class, the user loggs in, and their login information is stored in a prefernce. The login information is sent to TTS API, and the API responds with a session code. Once succesfuly logged in, it will a start a new instance of the GeoReferenced_web_request class.

# Prediction
This is not much of a class, just stores json information. Utilizes Gsontojson parsing.




