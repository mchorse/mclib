# Change Log

McLib's change log.

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