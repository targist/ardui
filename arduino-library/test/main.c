#include <common.pb.h>
#include <pb_decode.h>
#include <stdio.h>

void run_instruction(const com_targist_ardui_proto_Instruction *instruction) {
  switch (instruction->which_action) {
    case com_targist_ardui_proto_Instruction_setPinMode_tag:
      switch (instruction->action.setPinMode.mode) {
        case com_targist_ardui_proto_Mode_INPUT:
          printf("pinmode %d input\n", instruction->action.setPinMode.pin);
          break;
        case com_targist_ardui_proto_Mode_OUTPUT:
          printf("pinmode %d output\n", instruction->action.setPinMode.pin);
          break;
        case com_targist_ardui_proto_Mode_INPUT_PULLUP:
          printf("pinmode %d inputpullup\n",
                 instruction->action.setPinMode.pin);
          break;
      }
      break;
    case com_targist_ardui_proto_Instruction_digitalWrite_tag:
      switch (instruction->action.digitalWrite.level) {
        case com_targist_ardui_proto_Level_LOW:
          printf("digitalWrite %d low\n", instruction->action.digitalWrite.pin);
          break;
        case com_targist_ardui_proto_Level_HIGH:
          printf("digitalWrite %d high\n",
                 instruction->action.digitalWrite.pin);
          break;
      }
      break;
    case com_targist_ardui_proto_Instruction_analogWrite_tag:
      printf("analogWrite %d %d\n", instruction->action.analogWrite.pin,
             instruction->action.analogWrite.value);
      break;
    case com_targist_ardui_proto_Instruction_sleep_tag:
      printf("sleep %d\n", instruction->action.sleep.duration);
      break;
  }
}

bool instruction_callback(pb_istream_t *stream, const pb_field_t *field,
                          void **arg) {
  PB_UNUSED(arg);

  if (!stream) return true;

  com_targist_ardui_proto_Instruction instruction = {};

  bool status = pb_decode(stream, com_targist_ardui_proto_Instruction_fields,
                          &instruction);
  if (!status) {
    return false;
  }

  run_instruction(&instruction);

  return true;
}

bool runSetup = true;
int setup_index = 0;
int loop_index = 0;
bool setup_instruction_callback(pb_istream_t *stream, const pb_field_t *field,
                                void **arg) {
  if (!runSetup) return true;
  ++setup_index;
  if (!instruction_callback(stream, field, arg)) {
    printf("Error in running setup instruction %d\n", setup_index);
    return false;
  }
  return true;
}

bool loop_instruction_callback(pb_istream_t *stream, const pb_field_t *field,
                               void **arg) {
  ++loop_index;
  if (!instruction_callback(stream, field, arg)) {
    printf("Error in running loop instruction %d\n", loop_index);
    return false;
  }
  return true;
}

/* Check this with Blink example */
pb_byte_t buf[256] = {10,16,10,6,10,4,8,1,16,13,10,6,18,4,8,1,16,13,18,0};
size_t msglen = 20;

int run() {
  pb_istream_t input = pb_istream_from_buffer(buf, msglen);
  com_targist_ardui_proto_GenericArduinoProgram program = {};
  program.setup.instructions.funcs.decode = &setup_instruction_callback;
  program.loop.instructions.funcs.decode = &loop_instruction_callback;
  if (!pb_decode(&input, com_targist_ardui_proto_GenericArduinoProgram_fields,
                 &program)) {
    fprintf(stderr, "Decode failed: %s\n", PB_GET_ERROR(&input));
    return -1;
  }
  return 0;
}

int runProgram() {
  runSetup = true;
  setup_index = 0;
  loop_index = 0;
  return run();
}

int runLoop() {
  runSetup = false;
  setup_index = 0;
  loop_index = 0;
  return run();
}

int main() {
  int i;
  int err = runProgram();
  for (i = 0; i < 5 && !err; ++i) {
    err = runLoop();
  }
  return 0;
}
