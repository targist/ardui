#include <ArdUI.h>

#define BAUD ((int)9600)

void setup() {
  Serial.begin(BAUD);
  Serial.println("Board reset");
}

void loop() {
  if (read_program_bytes()) {
    Serial.println("A new configuration has been loaded");
    Serial.println("Running Setup..");
    execute_setup();
    Serial.println("Done");
  }
  execute_loop();
}
