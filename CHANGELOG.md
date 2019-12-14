# Change Log

McLib's change log.

## Version 1.0.4

This is a patch update which reworks file tree system, improves texture picker GUI and a couple of other neat features.

* Added key repeating (`Keyboard.enableRepeatEvents(true);`) (suggested by Tschipp)
* Added "Open folder" to texture picker to open current folder 
* Added `Interpolations.envelope()` methods which allow to easily make fade in/out factor calculations based on given time value, and durations of fade in, sustain and fade out
* Added background option for list elemenet
* Added refreshing of current folder in texture picker
* Added `IResourceTransformer` to allow fixing any resource locations created via `RLUtils`
* Added keyboard control to texture picker. You can now use arrows up/down to go up and down in the list, shift + arrow up/down to jump in the beginning/end of the list, type in the name to select the closest matching and enter to select the file/enter the folder
* Changed the way file tree system works to allow lazy loading (instead of full tree generation) and check for changes

## Version 1.0.3

A small patch update which is accompanied by Aperture 1.3.2 release.

* Added max values for width and height of `Resizer` class
* Added scrollable base GUI element
* Added support for relative width and height for `Resizer` class
* Added `double` variants of interpolation functions
* Added `Interpolation` enum
* Fixed issue with JSON multi resource location

## Version 1.0.2

A small patch update that features a couple of fixes, and one small feature.

* Add `random([range/min], [max], [seed])` function to math expression builder
* Change clicking on `+` in multi-skin menu to select the added resource location
* Fix multiple `../` in the texture picker (reported by mr wolf)
* Fix crash with registering texture when a multi-skin child returns a `null`

## Version 1.0.1

Second update. Mostly provides new code for Blockbuster, Aperture and Metamorph.

* Added a core mod which adds a handler in resource manager to support texture multi resource locations (i.e. multi-skin feature)
* Added texture picker GUI from Blockbuster and file tree API
* Added panel base GUI class (which allows to create multi-paneled GUIs)
* Extracted dummy entity and model renderer GUI from Blockbuster
* Extracted interpolation code from Aperture
* Extracted core modding utils from Blockbuster
* Extracted texture map reflection code from Blockbuster
* Fixed alpha in modals GUI which caused font not to render properly (reported by ycwei982)

## Version 1.0

Initial release. This bad boy contains so much useful code.

* Extracted math expression code from Aperture
* Extracted GUI framework code from Blockbuster
* Extracted Ernio's network base code from Blockbuster