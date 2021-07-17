#include <common.pb.h>
#include <pb_decode.h>

// #define DEBUG
#ifdef DEBUG
#define DPRINT(...) Serial.print(__VA_ARGS__)
#define DPRINTLN(...) Serial.println(__VA_ARGS__)
#else
#define DPRINT(...)
#define DPRINTLN(...)
#endif
#define UNINITIALIZED ((int)-1)
#define BAUD ((int)9600)
int bytes_length = UNINITIALIZED;
int curr = 0;
uint8_t bytes[256];
com_targist_magicembed_proto_GenericArduinoProgram program =
    com_targist_magicembed_proto_GenericArduinoProgram_init_zero;
void run(const com_targist_magicembed_proto_Instruction *instructions,
         pb_size_t instructions_count) {
  for (pb_size_t i = 0; i < instructions_count; ++i) {
    const com_targist_magicembed_proto_Instruction *instruction =
        &instructions[i];
    switch (instruction->which_action) {
      case com_targist_magicembed_proto_Instruction_setPinMode_tag:
        switch (instruction->action.setPinMode.mode) {
          case com_targist_magicembed_proto_Mode_INPUT:
            DPRINTLN("pinmode input");
            pinMode(instruction->action.setPinMode.pin, INPUT);
            break;
          case com_targist_magicembed_proto_Mode_OUTPUT:
            DPRINTLN("pinmode output");
            pinMode(instruction->action.setPinMode.pin, OUTPUT);
            break;
          case com_targist_magicembed_proto_Mode_INPUT_PULLUP:
            DPRINTLN("pinmode inputpullup");
            pinMode(instruction->action.setPinMode.pin, INPUT_PULLUP);
            break;
        }
        break;
      case com_targist_magicembed_proto_Instruction_digitalWrite_tag:
        switch (instruction->action.digitalWrite.level) {
          case com_targist_magicembed_proto_Level_LOW:
            DPRINTLN("digitalWrite low");
            digitalWrite(instruction->action.digitalWrite.pin, LOW);
            break;
          case com_targist_magicembed_proto_Level_HIGH:
            DPRINTLN("digitalWrite high");
            digitalWrite(instruction->action.digitalWrite.pin, HIGH);
            break;
        }
        break;
      case com_targist_magicembed_proto_Instruction_analogWrite_tag:
        analogWrite(instruction->action.analogWrite.pin,
                    instruction->action.analogWrite.value);
        break;
      case com_targist_magicembed_proto_Instruction_sleep_tag:
        DPRINT("delay ");
        DPRINTLN(instruction->action.sleep.duration);
        delay(instruction->action.sleep.duration);
        break;
    }
  }
}

void deserializeProgram() {
  DPRINTLN(F("deserializing Program.."));
  int len = bytes_length;
  bytes_length = UNINITIALIZED;
  pb_istream_t stream = pb_istream_from_buffer(bytes, len);
  bool status = pb_decode(
      &stream, com_targist_magicembed_proto_GenericArduinoProgram_fields,
      &program);
  DPRINTLN(status);

  if (!status) {
    DPRINTLN(F("Command decoding failed! Fall back to default"));
    program = com_targist_magicembed_proto_GenericArduinoProgram_init_zero;
    return;
  }

  DPRINT(F("Program decoding was Successful bytes length = "));
  DPRINTLN(len);

  DPRINTLN(F("A new configuration has been loaded"));
  DPRINTLN(F("Running setup instructions.."));
  run(program.setup.instructions, program.setup.instructions_count);
  Serial.print(F("s="));
  Serial.println(program.setup.instructions_count);
  DPRINTLN(F("Done."));
}

void setup() {
  Serial.begin(BAUD);
  DPRINTLN(F("Finished setup"));
  Serial.println(sizeof(com_targist_magicembed_proto_GenericArduinoProgram));
}

bool readBuffer() {
  while (true) {
    int r = Serial.read();
    if (r == -1) return false;
    DPRINT(curr);
    DPRINT(F(" "));
    DPRINT(r);
    DPRINT(F(" "));
    DPRINTLN(bytes_length);
    if (bytes_length == UNINITIALIZED) {
      bytes_length = r;
      curr = 0;
    } else {
      bytes[curr++] = (uint8_t)r;
      if (curr == bytes_length) {
        DPRINT("r=");
        DPRINTLN(bytes_length);
        return true;
      }
    }
  }
}

void loop() {
  if (readBuffer()) {
    deserializeProgram();
  }

  run(program.loop.instructions, program.loop.instructions_count);
}
