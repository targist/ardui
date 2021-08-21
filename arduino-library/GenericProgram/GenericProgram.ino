#include <ArdUI.h>

#define BAUD ((int)9600)

void setup() {
  Serial.begin(BAUD);
  Serial.println(F("Board reset"));
}

void loop() {
  if (read_program_bytes()) {
    Serial.println(F("A new configuration has been loaded"));
    Serial.println(F("Running Setup.."));
    execute_setup();
    Serial.println(F("Done"));
  }
  execute_loop();
}
