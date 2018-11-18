A game where maths challenges fall down from the sky and you can shoot
them by typing the correct answer.

# Releasing a new Version
* Update `VersionName` and `VersionCode` in `/app/build.gradle`
* Commit this change
* `git push`
* `git tag <new version name>`, do `git tag | cat` for inspiration
* In Android Studio, do *Build* / *Generate Signed APK...*
* Upload the new APK to [Google Play](https://play.google.com/apps/publish)
* `git push --tags`

# Credits
* Cannon fire sound: <https://www.freesfx.co.uk/download/?type=mp3&id=14604>
* Cannon explosion sound: <https://freesound.org/people/cydon/sounds/268557/>
* Maths exploding: <https://freesound.org/people/grunz/sounds/109662/>
* Maths arriving: <https://freesound.org/people/GameAudio/sounds/220202/>
* Wrong answer: <https://freesound.org/people/Autistic%20Lucario/sounds/142608/>
* Level cleared: <https://freesound.org/people/sagetyrtle/sounds/32260/>
* Key up and key down sounds: Made them myself using
  [Audacity](https://www.audacityteam.org/download/)
* Background image: <https://pixabay.com/sv/stj%C3%A4rnor-stj%C3%A4rnbilden-sky-1245902/>
* Music: <https://www.bensound.com/royalty-free-music/track/sci-fi>

NOTE: If you add to this list, also update
`app/src/main/res/raw/credits.txt`.

# TODO Misc
* Add tablet screenshots to Google Play
* Make sure Google recommends us for tablets
* Add a mode for addition
* Add a mode for division
* Add a mode for subtraction
* Make a more numbery icon
* Make the level-failed answers dialog look better
* Consider making the navigation bar white on black during the game to
  blend in better with the keyboard.
* Make sure that a `RuntimeException` from `GameActivity.levelCleared()`
  crashes the app
* When showing correct answer for "5*3=15", show also "3*5=15", but
  don't show "6*6=36" twice
* Fix volumes of all sounds so that they are good relative each other.
* Cheerier level-cleared image
* Draw trails after shots; make them more rocketlike
* Add to the launch screen a list of cleared levels and how many seconds
  each took down to one decimal. Enable the player to select an older
  level and re-play it.
* Hide controls by default so that we get the full-screen experience,
  good for production, search for `mHideRunnable`. Verify that we can
  easily show the controls when they are hidden.
* Pause game while controls are visible
* Remove click handler from GameView to force full screen, but verify
  that swiping down from the top still works
* Adapt font sizes to screen

## DROPPED
* In the Model, create a way to iterate only over our `FallingMaths`
  objects. Could have done something like
  [this](https://codereview.stackexchange.com/a/112111/159546), but it
  feels too complicated. Suggestions welcome.
* Stop all the invalidate() calls when the simulation is frozen. Can't
  see how this would help with much of anything.

## DONE
* Make text move down from top
* Add more falling text when the first text is 20% down
* View controls by default so that we can pause the game easily, good
  for development.
* Add a numeric keyboard
* Fire away numbers when keys are pressed
* Make maths assignments fall from the sky
* Shoot at maths assignments when the right answer is entered
* Fire a slow red shot when a wrong answer is entered
* Kill player when some maths lands
* When player is killed, freeze the simulation after a delay.
* Log frame rate on emulator (we're fine)
* Log frame rate on a 10" tablet (we're doing great)
* Show correct answer on screen when the player dies.
* Tune the hit areas for the keys on our keyboard
* Add a launch screen
* Dismissing the level-failed dialog should go to the launch screen, no
  matter how it is dismissed
* Add a bottom margin below the keyboard, the layout looked cramped
  without that on my phone.
* Show level-success dialog after clearing a level
* Add sound effects
* Add feedback on key presses
* Keep level state throughout the app
* Launch screen should say which level will be launched
* Make our keyboard react to actual digits presses from a real keyboard.
  Nice during development.
* Pick maths depending on level
* Tune game to 20s for a maths to fall, add one more every 50%
* Drop easier maths faster than harder maths
* Launch screen background image
* In-game background image
* Add a music score
* Validate performance now that we have the in-game background image
* Add a license
* If you clear one level and start the next, then press the back button
  to go back to the start screen, make sure to update the button
  contents with the correct level number.
* Pressing back in the you-failed dialog should go to the launch screen
* Make an icon
* Add a privacy policy
* Add a credits list of some form to the launch screen
* Make a "Feature Graphics"
* Actually publish on Google Play
