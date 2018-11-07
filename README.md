A game where maths challenges fall down from the sky and you can shoot
them by typing the correct answer.

# TODO
* Make our keyboard react to actual digits presses from a real keyboard
  as well. Nice during development.
* Kill player when some maths lands
* Add feedback on key presses
* Add a launch screen
* Add a level-failed screen
* Add a level-success screen
* Add starry background
* Adapt challenges font size to screen
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


## DONE
* Make text move down from top
* Add more falling text when the first text is 20% down
* View controls by default so that we can pause the game easily, good
  for development.
* Add a numeric keyboard
* Fire away numbers when keys are pressed
* Make maths assignments fall from the sky
* Shoot at maths assignments when the right answer is entered

## DROPPED
* In the Model, create a way to iterate only over our `FallingMaths`
  objects. Could have done something like
  [this](https://codereview.stackexchange.com/a/112111/159546), but it
  feels too complicated. Suggestions welcome.
* Fire a slow red shot when a wrong answer is entered
