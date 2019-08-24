# Die Game
 
   Rock n Roll is an android application (Minimum API Level: 23) to play die game. The application has a scoreboard at top, set of die and controls at the bottom.
 
## Functionalities
1.	Rolling the die – The dice can be rolled by using the play button or by shaking the device. Additionally, the dice will start when the user touches it.
2.	Specifying number of die – By default, the application will have two dice. The user will able to add dice up to 6. The add and remove button is placed at right corner of the controls section.
3.	Score Board – A score board will have the overall score, points of the current roll and its total. Each time a user rolls the die, a message will be displayed in toast based on the percentage of the current roll.
4.	Replay – The user can reset the score board and start a new game whenever they wish by using the reset button near play.

## Polish
1.	Animation effect – To simulate the die roll effect, the bounce_interpolator is used with rotate and translate.
2.	Sound effect – A die roll sound will be played in background to give a real experience of the game.
3.	Background – A board background is selected since the die roll is a board game.
4.	 Full screen – In order to avoid the distractions while playing, the application will launch in full screen mode.

## Device Feature
Accelerometer – It is used to to start the play when the user shakes the device. The acceleration threshold is set to be 600ms in this application. I’ve chosen to facilitate shake feature by using Accelerometer, since the gyroscope tends the drain the battery.
Framework, Tools and Libraries
•	Android Studio – 3.4
•	Java – JRE (1.8.0)
•	com.google.android:flexbox – 1.0.0 
•	support-emoji-bundled – 28.0.0

## References
[1] Emoji Compatibility  |  Android Developers. (n.d.). Retrieved June 16, 2019, from https://developer.android.com/guide/topics/ui/look-and-feel/emoji-compat

[2] File:Dice 1-6.svg. (n.d.). Retrieved June 16, 2019, from https://commons.wikimedia.org/wiki/File:Dice_1-6.svg

[3] Saurel, S., & Saurel, S. (2017, June 08). Create a Roll Dice Game on Android with Shake Effect. Retrieved from https://android.jlelse.eu/create-a-roll-dice-game-on-android-with-shake-effect-527b14f0c492

[4] Using the Accelerometer on Android. (n.d.). Retrieved June 16, 2019, from https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125



