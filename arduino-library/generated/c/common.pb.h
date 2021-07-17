/* Automatically generated nanopb header */
/* Generated by nanopb-0.4.6-dev */

#ifndef PB_COM_TARGIST_MAGICEMBED_PROTO_COMMON_PB_H_INCLUDED
#define PB_COM_TARGIST_MAGICEMBED_PROTO_COMMON_PB_H_INCLUDED
#include <pb.h>

#if PB_PROTO_HEADER_VERSION != 40
#error Regenerate this file with the current version of nanopb generator.
#endif

/* Enum definitions */
typedef enum _com_targist_magicembed_proto_Mode { 
    com_targist_magicembed_proto_Mode_INPUT = 0, 
    com_targist_magicembed_proto_Mode_OUTPUT = 1, 
    com_targist_magicembed_proto_Mode_INPUT_PULLUP = 2 
} com_targist_magicembed_proto_Mode;

typedef enum _com_targist_magicembed_proto_Level { 
    com_targist_magicembed_proto_Level_HIGH = 0, 
    com_targist_magicembed_proto_Level_LOW = 1 
} com_targist_magicembed_proto_Level;

/* Struct definitions */
typedef struct _com_targist_magicembed_proto_AnalogWrite { 
    uint32_t pin; 
    /* Duty cycle which should be between 0 and 255 */
    uint32_t value; 
} com_targist_magicembed_proto_AnalogWrite;

typedef struct _com_targist_magicembed_proto_DigitalWrite { 
    com_targist_magicembed_proto_Level level; 
    uint32_t pin; 
} com_targist_magicembed_proto_DigitalWrite;

typedef struct _com_targist_magicembed_proto_SetPinMode { 
    com_targist_magicembed_proto_Mode mode; 
    uint32_t pin; 
} com_targist_magicembed_proto_SetPinMode;

typedef struct _com_targist_magicembed_proto_Sleep { 
    uint32_t duration; 
} com_targist_magicembed_proto_Sleep;

typedef struct _com_targist_magicembed_proto_Instruction { 
    pb_size_t which_action;
    union {
        com_targist_magicembed_proto_SetPinMode setPinMode;
        com_targist_magicembed_proto_DigitalWrite digitalWrite;
        com_targist_magicembed_proto_AnalogWrite analogWrite;
        com_targist_magicembed_proto_Sleep sleep;
    } action; 
} com_targist_magicembed_proto_Instruction;

typedef struct _com_targist_magicembed_proto_LoopScript { 
    pb_size_t instructions_count;
    com_targist_magicembed_proto_Instruction instructions[16]; 
} com_targist_magicembed_proto_LoopScript;

typedef struct _com_targist_magicembed_proto_SetupScript { 
    pb_size_t instructions_count;
    com_targist_magicembed_proto_Instruction instructions[8]; 
} com_targist_magicembed_proto_SetupScript;

typedef struct _com_targist_magicembed_proto_GenericArduinoProgram { 
    bool has_setup;
    com_targist_magicembed_proto_SetupScript setup; 
    bool has_loop;
    com_targist_magicembed_proto_LoopScript loop; 
} com_targist_magicembed_proto_GenericArduinoProgram;


/* Helper constants for enums */
#define _com_targist_magicembed_proto_Mode_MIN com_targist_magicembed_proto_Mode_INPUT
#define _com_targist_magicembed_proto_Mode_MAX com_targist_magicembed_proto_Mode_INPUT_PULLUP
#define _com_targist_magicembed_proto_Mode_ARRAYSIZE ((com_targist_magicembed_proto_Mode)(com_targist_magicembed_proto_Mode_INPUT_PULLUP+1))

#define _com_targist_magicembed_proto_Level_MIN com_targist_magicembed_proto_Level_HIGH
#define _com_targist_magicembed_proto_Level_MAX com_targist_magicembed_proto_Level_LOW
#define _com_targist_magicembed_proto_Level_ARRAYSIZE ((com_targist_magicembed_proto_Level)(com_targist_magicembed_proto_Level_LOW+1))


#ifdef __cplusplus
extern "C" {
#endif

/* Initializer values for message structs */
#define com_targist_magicembed_proto_GenericArduinoProgram_init_default {false, com_targist_magicembed_proto_SetupScript_init_default, false, com_targist_magicembed_proto_LoopScript_init_default}
#define com_targist_magicembed_proto_SetupScript_init_default {0, {com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default}}
#define com_targist_magicembed_proto_LoopScript_init_default {0, {com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default, com_targist_magicembed_proto_Instruction_init_default}}
#define com_targist_magicembed_proto_Instruction_init_default {0, {com_targist_magicembed_proto_SetPinMode_init_default}}
#define com_targist_magicembed_proto_SetPinMode_init_default {_com_targist_magicembed_proto_Mode_MIN, 0}
#define com_targist_magicembed_proto_DigitalWrite_init_default {_com_targist_magicembed_proto_Level_MIN, 0}
#define com_targist_magicembed_proto_AnalogWrite_init_default {0, 0}
#define com_targist_magicembed_proto_Sleep_init_default {0}
#define com_targist_magicembed_proto_GenericArduinoProgram_init_zero {false, com_targist_magicembed_proto_SetupScript_init_zero, false, com_targist_magicembed_proto_LoopScript_init_zero}
#define com_targist_magicembed_proto_SetupScript_init_zero {0, {com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero}}
#define com_targist_magicembed_proto_LoopScript_init_zero {0, {com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero, com_targist_magicembed_proto_Instruction_init_zero}}
#define com_targist_magicembed_proto_Instruction_init_zero {0, {com_targist_magicembed_proto_SetPinMode_init_zero}}
#define com_targist_magicembed_proto_SetPinMode_init_zero {_com_targist_magicembed_proto_Mode_MIN, 0}
#define com_targist_magicembed_proto_DigitalWrite_init_zero {_com_targist_magicembed_proto_Level_MIN, 0}
#define com_targist_magicembed_proto_AnalogWrite_init_zero {0, 0}
#define com_targist_magicembed_proto_Sleep_init_zero {0}

/* Field tags (for use in manual encoding/decoding) */
#define com_targist_magicembed_proto_AnalogWrite_pin_tag 1
#define com_targist_magicembed_proto_AnalogWrite_value_tag 2
#define com_targist_magicembed_proto_DigitalWrite_level_tag 1
#define com_targist_magicembed_proto_DigitalWrite_pin_tag 2
#define com_targist_magicembed_proto_SetPinMode_mode_tag 1
#define com_targist_magicembed_proto_SetPinMode_pin_tag 2
#define com_targist_magicembed_proto_Sleep_duration_tag 1
#define com_targist_magicembed_proto_Instruction_setPinMode_tag 1
#define com_targist_magicembed_proto_Instruction_digitalWrite_tag 2
#define com_targist_magicembed_proto_Instruction_analogWrite_tag 3
#define com_targist_magicembed_proto_Instruction_sleep_tag 4
#define com_targist_magicembed_proto_LoopScript_instructions_tag 1
#define com_targist_magicembed_proto_SetupScript_instructions_tag 1
#define com_targist_magicembed_proto_GenericArduinoProgram_setup_tag 1
#define com_targist_magicembed_proto_GenericArduinoProgram_loop_tag 2

/* Struct field encoding specification for nanopb */
#define com_targist_magicembed_proto_GenericArduinoProgram_FIELDLIST(X, a) \
X(a, STATIC,   OPTIONAL, MESSAGE,  setup,             1) \
X(a, STATIC,   OPTIONAL, MESSAGE,  loop,              2)
#define com_targist_magicembed_proto_GenericArduinoProgram_CALLBACK NULL
#define com_targist_magicembed_proto_GenericArduinoProgram_DEFAULT NULL
#define com_targist_magicembed_proto_GenericArduinoProgram_setup_MSGTYPE com_targist_magicembed_proto_SetupScript
#define com_targist_magicembed_proto_GenericArduinoProgram_loop_MSGTYPE com_targist_magicembed_proto_LoopScript

#define com_targist_magicembed_proto_SetupScript_FIELDLIST(X, a) \
X(a, STATIC,   REPEATED, MESSAGE,  instructions,      1)
#define com_targist_magicembed_proto_SetupScript_CALLBACK NULL
#define com_targist_magicembed_proto_SetupScript_DEFAULT NULL
#define com_targist_magicembed_proto_SetupScript_instructions_MSGTYPE com_targist_magicembed_proto_Instruction

#define com_targist_magicembed_proto_LoopScript_FIELDLIST(X, a) \
X(a, STATIC,   REPEATED, MESSAGE,  instructions,      1)
#define com_targist_magicembed_proto_LoopScript_CALLBACK NULL
#define com_targist_magicembed_proto_LoopScript_DEFAULT NULL
#define com_targist_magicembed_proto_LoopScript_instructions_MSGTYPE com_targist_magicembed_proto_Instruction

#define com_targist_magicembed_proto_Instruction_FIELDLIST(X, a) \
X(a, STATIC,   ONEOF,    MESSAGE,  (action,setPinMode,action.setPinMode),   1) \
X(a, STATIC,   ONEOF,    MESSAGE,  (action,digitalWrite,action.digitalWrite),   2) \
X(a, STATIC,   ONEOF,    MESSAGE,  (action,analogWrite,action.analogWrite),   3) \
X(a, STATIC,   ONEOF,    MESSAGE,  (action,sleep,action.sleep),   4)
#define com_targist_magicembed_proto_Instruction_CALLBACK NULL
#define com_targist_magicembed_proto_Instruction_DEFAULT NULL
#define com_targist_magicembed_proto_Instruction_action_setPinMode_MSGTYPE com_targist_magicembed_proto_SetPinMode
#define com_targist_magicembed_proto_Instruction_action_digitalWrite_MSGTYPE com_targist_magicembed_proto_DigitalWrite
#define com_targist_magicembed_proto_Instruction_action_analogWrite_MSGTYPE com_targist_magicembed_proto_AnalogWrite
#define com_targist_magicembed_proto_Instruction_action_sleep_MSGTYPE com_targist_magicembed_proto_Sleep

#define com_targist_magicembed_proto_SetPinMode_FIELDLIST(X, a) \
X(a, STATIC,   SINGULAR, UENUM,    mode,              1) \
X(a, STATIC,   SINGULAR, UINT32,   pin,               2)
#define com_targist_magicembed_proto_SetPinMode_CALLBACK NULL
#define com_targist_magicembed_proto_SetPinMode_DEFAULT NULL

#define com_targist_magicembed_proto_DigitalWrite_FIELDLIST(X, a) \
X(a, STATIC,   SINGULAR, UENUM,    level,             1) \
X(a, STATIC,   SINGULAR, UINT32,   pin,               2)
#define com_targist_magicembed_proto_DigitalWrite_CALLBACK NULL
#define com_targist_magicembed_proto_DigitalWrite_DEFAULT NULL

#define com_targist_magicembed_proto_AnalogWrite_FIELDLIST(X, a) \
X(a, STATIC,   SINGULAR, UINT32,   pin,               1) \
X(a, STATIC,   SINGULAR, UINT32,   value,             2)
#define com_targist_magicembed_proto_AnalogWrite_CALLBACK NULL
#define com_targist_magicembed_proto_AnalogWrite_DEFAULT NULL

#define com_targist_magicembed_proto_Sleep_FIELDLIST(X, a) \
X(a, STATIC,   SINGULAR, UINT32,   duration,          1)
#define com_targist_magicembed_proto_Sleep_CALLBACK NULL
#define com_targist_magicembed_proto_Sleep_DEFAULT NULL

extern const pb_msgdesc_t com_targist_magicembed_proto_GenericArduinoProgram_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_SetupScript_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_LoopScript_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_Instruction_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_SetPinMode_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_DigitalWrite_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_AnalogWrite_msg;
extern const pb_msgdesc_t com_targist_magicembed_proto_Sleep_msg;

/* Defines for backwards compatibility with code written before nanopb-0.4.0 */
#define com_targist_magicembed_proto_GenericArduinoProgram_fields &com_targist_magicembed_proto_GenericArduinoProgram_msg
#define com_targist_magicembed_proto_SetupScript_fields &com_targist_magicembed_proto_SetupScript_msg
#define com_targist_magicembed_proto_LoopScript_fields &com_targist_magicembed_proto_LoopScript_msg
#define com_targist_magicembed_proto_Instruction_fields &com_targist_magicembed_proto_Instruction_msg
#define com_targist_magicembed_proto_SetPinMode_fields &com_targist_magicembed_proto_SetPinMode_msg
#define com_targist_magicembed_proto_DigitalWrite_fields &com_targist_magicembed_proto_DigitalWrite_msg
#define com_targist_magicembed_proto_AnalogWrite_fields &com_targist_magicembed_proto_AnalogWrite_msg
#define com_targist_magicembed_proto_Sleep_fields &com_targist_magicembed_proto_Sleep_msg

/* Maximum encoded size of messages (where known) */
#define com_targist_magicembed_proto_AnalogWrite_size 12
#define com_targist_magicembed_proto_DigitalWrite_size 8
#define com_targist_magicembed_proto_GenericArduinoProgram_size 390
#define com_targist_magicembed_proto_Instruction_size 14
#define com_targist_magicembed_proto_LoopScript_size 256
#define com_targist_magicembed_proto_SetPinMode_size 8
#define com_targist_magicembed_proto_SetupScript_size 128
#define com_targist_magicembed_proto_Sleep_size  6

#ifdef __cplusplus
} /* extern "C" */
#endif

#endif