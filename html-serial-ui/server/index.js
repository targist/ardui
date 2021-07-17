const common = require("../js/common_pb");
const SerialPort = require("serialport");
const Readline = SerialPort.parsers.Readline;
const express = require("express");
const app = express();
var path = require("path");
app.use(express.static(path.join(__dirname, "public")));
app.use(express.urlencoded({
  extended: true,
}));

app.get("/", function (req, res) {
  res.render("public/index.html");
});

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
            new common.Sleep().setDuration(ms),
          );

        case "digitalwrite":
          pin = parseInt(tokens[1]);
          level = tokens[2] == "high" ? common.Level.HIGH : common.Level.LOW;
          console.log(">>>digitalwrite " + pin + " " + level);
          return new common.Instruction().setDigitalwrite(
            new common.DigitalWrite().setPin(pin).setLevel(level),
          );

        case "analogwrite":
          pin = parseInt(tokens[1]);
          value = parseInt(tokens[2]);
          console.log(">>>analogwrite " + pin + " " + value);
          return new common.Instruction().setDigitalwrite(
            new common.AnalogWrite().setPin(pin).setValue(value),
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
            new common.SetPinMode()
              .setPin(pin)
              .setMode(mode),
          );

        default:
          return null;
      }
    });
}

const port = new SerialPort("<Your Serial port i.e: /dev/cu.usbmodem14200>", {
  baudRate: 9600,
});

const parser = port.pipe(new Readline());
parser.on("data", console.log);

function switchOfLed13Program() {
  const setup = new common.Script().setInstructionsList([
    new common.Instruction()
      .setSetpinmode(
        new common.SetPinMode()
          .setPin(13)
          .setMode(common.Mode.OUTPUT),
      ),
    new common.Instruction().setDigitalwrite(
      new common.DigitalWrite()
        .setPin(13)
        .setLevel(common.Level.LOW),
    ),
  ]);
  return new common.GenericArduinoProgram()
    .setSetup(setup);
}

function programFromReq(req) {
  const setupInstructions = parseInstructions(req.body.setup);
  const loopInstructions = parseInstructions(req.body.loop);
  console.log(">>>loop=" + loopInstructions.length);
  console.log(">>>setup=" + setupInstructions.length);
  const setup = new common.Script().setInstructionsList(setupInstructions);
  const loop = new common.Script().setInstructionsList(loopInstructions);
  return new common.GenericArduinoProgram()
    .setSetup(setup)
    .setLoop(loop);
}

app.post("/upload", function (req, res) {
  const program = programFromReq(req);
  // const program = switchOfLed13Program();
  const buffer = program.serializeBinary();
  console.log("buffer=" + buffer);
  console.log("buffer-length=" + buffer.length);
  port.write([buffer.length]);
  port.write(buffer);
  res.send(
    "Writing " + buffer.length + " bytes to Arduino Serial port " +
      req.body.port,
  );
});

app.listen(8081, function () {
  console.log("ArdUI listening on port http://0.0.0.0:8081");
});
