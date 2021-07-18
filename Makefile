.PHONY: prepare-release-zip
prepare-release-zip:
	rm -rf dist
	mkdir dist
	cp "arduino-library/sketches/GenericProgram/GenericProgram.ino" "dist/GenericProgram.ino"
	cp "arduino-library/generated/c/Ardui.zip" "dist/Ardui.zip"
	cp "arduiapp/app/release/app-release.apk" "dist/ArdUI.apk"
	zip -r dist/release.zip dist
