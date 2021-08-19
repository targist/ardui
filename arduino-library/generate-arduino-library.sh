#! /bin/bash
set -eux
NANOPB_DIR=$1
NANOPB_CORE="${NANOPB_DIR}/pb_decode.h ${NANOPB_DIR}/pb_decode.c ${NANOPB_DIR}/pb_common.h ${NANOPB_DIR}/pb_common.c ${NANOPB_DIR}/pb.h"
GENERATED_FILES="generated/common.pb.h generated/common.pb.c"
ARDUI_FILES="ArdUI.h ArdUI.cpp"
${NANOPB_DIR}/generator/nanopb_generator.py \
  -D ./generated \
  -I ../proto \
  common.proto
zip -j ArdUI.zip $NANOPB_CORE $GENERATED_FILES $ARDUI_FILES
