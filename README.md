A game where maths challenges fall down from the sky and you can shoot
them by typing the correct answer.

# TODO
* When player is killed, slow down the simulation over a few seconds
  then go to the Game Over screen
* Add a launch screen with a credits dialog
* Add a level-failed screen saying which maths killed us and what the
  correct answer was
* Tune the hit areas for the keys on our keyboard
* Add a level-success screen
* Pick maths depending on level
* Set a level-finished criteria
* Drop easier maths faster than harder maths
* Add feedback on key presses
* Adapt font sizes to screen
* Add sound effects
* Add a music score
* Add starry background
* Profile game for memory allocations
* Profile game for CPU usage while drawing
* Profile game for memory allocations on an actual 10" tablet
* Profile game for CPU usage on an actual 10" tablet
* Hide controls by default so that we get the full-screen experience,
  good for production. Verify that we can easily show the controls
  when they are hidden.
* Pause game while controls are visible
* Add a license
* Make an icon
* Make a "Feature Graphics"
* Publish on Google Play


## DROPPED
* In the Model, create a way to iterate only over our `FallingMaths`
  objects. Could have done something like
  [this](https://codereview.stackexchange.com/a/112111/159546), but it
  feels too complicated. Suggestions welcome.
* Make our keyboard react to actual digits presses from a real keyboard
  as well. Nice during development. Tried with `setOnKeyListener()` and
  [this](https://stackoverflow.com/a/26567134/473672) but only got
  events sporadically, never mind.

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
