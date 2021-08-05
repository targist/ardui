const common = require("./common_pb");

function parseInstructions(text) {
  return text
    .split("\n")
    .filter((x) => x != "")
    .map(function (s) {
      console.log("s=" + s);
      const tokens = s.split(" ");
      var pin, level, value, ms;
      switch (tokens[0]) {
        case "sleep":
          ms = parseInt(tokens[1]);
          console.log(">>>sleep " + ms);
          return new common.Instruction().setSleep(
            new common.Sleep().setDuration(ms)
          );

        case "digitalwrite":
          pin = parseInt(tokens[1]);
          level = tokens[2] == "high" ? common.Level.HIGH : common.Level.LOW;
          console.log(">>>digitalwrite " + pin + " " + level);
          return new common.Instruction().setDigitalwrite(
            new common.DigitalWrite().setPin(pin).setLevel(level)
          );

        case "analogwrite":
          pin = parseInt(tokens[1]);
          value = parseInt(tokens[2]);
          console.log(">>>analogwrite " + pin + " " + value);
          return new common.Instruction().setDigitalwrite(
            new common.AnalogWrite().setPin(pin).setValue(value)
          );

        case "pinmode":
          pin = parseInt(tokens[1]);
          var mode;
          switch (tokens[2]) {
            case "input":
              mode = common.Mode.INPUT;
              break;
            case "output":
              mode = common.Mode.OUTPUT;
              break;
            case "input_pullup":
              mode = common.Mode.INPUT_PULLUP;
              break;
          }
          console.log(">>>pinmode " + pin + " " + mode);
          return new common.Instruction().setSetpinmode(
            new common.SetPinMode().setPin(pin).setMode(mode)
          );

        default:
          return null;
      }
    });
}

function programFromReq(req) {
  const setupInstructions = parseInstructions(req.body.setup);
  const loopInstructions = parseInstructions(req.body.loop);
  console.log(">>>setup-instructions-count=" + setupInstructions.length);
  console.log(">>>loop-instructions-count=" + loopInstructions.length);
  const setup = new common.SetupScript().setInstructionsList(setupInstructions);
  const loop = new common.LoopScript().setInstructionsList(loopInstructions);
  return new common.GenericArduinoProgram().setSetup(setup).setLoop(loop);
}

module.exports = { programFromReq, parseInstructions };