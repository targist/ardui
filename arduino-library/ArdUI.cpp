#include "ArdUI.h"

#include <Arduino.h>
#include <common.pb.h>
#include <pb_decode.h>

#define BUFFER_SIZE 256

// #define DEBUG
#ifdef DEBUG
#define DPRINT(...) Serial.print(__VA_ARGS__)
#define DPRINTLN(...) Serial.println(__VA_ARGS__)
#else
#define DPRINT(...)
#define DPRINTLN(...)
#endif
#define UNINITIALIZED ((size_t)-1)

namespace {
uint8_t buffer[BUFFER_SIZE];
size_t curr_buffer_length = 0;
size_t next_buffer_length = UNINITIALIZED;
size_t curr = 0;
bool run_setup = false;
bool run_loop = false;
}  // namespace

bool run_instruction(const com_targist_ardui_proto_Instruction *instruction) {
  switch (instruction->which_action) {
    case com_targist_ardui_proto_Instruction_setPinMode_tag:
      switch (instruction->action.setPinMode.mode) {
        case com_targist_ardui_proto_Mode_INPUT:
          DPRINT(instruction->action.setPinMode.pin);
          DPRINTLN(" pinmode input");
          pinMode(instruction->action.setPinMode.pin, INPUT);
          return true;
        case com_targist_ardui_proto_Mode_OUTPUT:
          DPRINT(instruction->action.setPinMode.pin);
          DPRINTLN(" pinmode output");
          pinMode(instruction->action.setPinMode.pin, OUTPUT);
          return true;
        case com_targist_ardui_proto_Mode_INPUT_PULLUP:
          DPRINT(instruction->action.setPinMode.pin);
          DPRINTLN(" pinmode inputpullup");
          pinMode(instruction->action.setPinMode.pin, INPUT_PULLUP);
          return true;
        default:
          DPRINTLN("unknown pin mode");
          return false;
      }
    case com_targist_ardui_proto_Instruction_digitalWrite_tag:
      switch (instruction->action.digitalWrite.level) {
        case com_targist_ardui_proto_Level_LOW:
          DPRINT(instruction->action.digitalWrite.pin);
          DPRINTLN(" digitalWrite low");
          digitalWrite(instruction->action.digitalWrite.pin, LOW);
          return true;
        case com_targist_ardui_proto_Level_HIGH:
          DPRINT(instruction->action.digitalWrite.pin);
          DPRINTLN(" digitalWrite high");
          digitalWrite(instruction->action.digitalWrite.pin, HIGH);
          return true;
        default:
          DPRINTLN("unknown digital write level");
          return false;
      }
    case com_targist_ardui_proto_Instruction_analogWrite_tag:
      DPRINT("analogWrite ");
      DPRINT(instruction->action.analogWrite.pin);
      DPRINT(" ");
      DPRINT(instruction->action.analogWrite.value);
      analogWrite(instruction->action.analogWrite.pin,
                  instruction->action.analogWrite.value);
      return true;
    case com_targist_ardui_proto_Instruction_sleep_tag:
      DPRINT("delay ");
      DPRINTLN(instruction->action.sleep.duration);
      delay(instruction->action.sleep.duration);
      return true;
    default:
      return false;
  }
}

bool read_program_bytes() {
  while (true) {
    int r = Serial.read();
    if (r == -1) return false;
    DPRINT(curr);
    DPRINT(F(" "));
    DPRINT(r);
    DPRINT(F(" "));
    DPRINTLN(next_buffer_length);
    if (next_buffer_length == UNINITIALIZED) {
      next_buffer_length = (size_t)r;
      curr = 0;
    } else {
      buffer[curr++] = (uint8_t)r;
      if (curr == next_buffer_length) {
        curr_buffer_length = next_buffer_length;
        next_buffer_length = UNINITIALIZED;
        Serial.print(curr_buffer_length);
        Serial.println(F(" bytes have been read from Serial."));
        return true;
      }
    }
  }
}

bool instruction_callback(pb_istream_t *stream, const pb_field_t *field,
                          void **arg) {
  if (!stream) return true;

  char *c = (char *)(*arg);
  if (*c == 's' && !run_setup) return true;
  if (*c == 'l' && !run_loop) return true;

  com_targist_ardui_proto_Instruction instruction = {};
  if (!pb_decode(stream, com_targist_ardui_proto_Instruction_fields,
                 &instruction)) {
    return false;
  }

  return run_instruction(&instruction);
}

int run() {
  // if no bytes read or we are in the middle of reading a buffer do skip
  // program execution
  if (!curr_buffer_length || next_buffer_length != UNINITIALIZED) {
    return 0;
  }

  pb_istream_t input = pb_istream_from_buffer(buffer, curr_buffer_length);
  com_targist_ardui_proto_GenericArduinoProgram program = {};
  program.setup.instructions.funcs.decode = &instruction_callback;
  program.setup.instructions.arg = (void *)"s";
  program.loop.instructions.funcs.decode = &instruction_callback;
  program.loop.instructions.arg = (void *)"l";
  if (!pb_decode(&input, com_targist_ardui_proto_GenericArduinoProgram_fields,
                 &program)) {
    DPRINTLN(F("Running program failed!"));
    DPRINTLN(PB_GET_ERROR(&input));
    return -1;
  }
  return 0;
}

int execute_setup() {
  run_setup = true;
  run_loop = false;
  return run();
}

int execute_loop() {
  run_setup = false;
  run_loop = true;
  return run();
}
