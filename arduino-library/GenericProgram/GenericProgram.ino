#include <ArdUI.h>

#define BAUD ((int)9600)

void setup() {
  Serial.begin(BAUD);
  Serial.println("Finished setup");
}

void loop() {
  if (readProgramBytes()) {
    runProgram();
  } else {
    runLoop();
  }
}
