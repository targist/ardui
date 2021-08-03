const common = require("../js/common_pb");
const SerialPort = require("serialport");
const Readline = SerialPort.parsers.Readline;
const express = require("express");

const app = express();
var path = require("path");
app.use(express.static(path.join(__dirname, "views")));
app.use(express.urlencoded({
  extended: true,
}));
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
const port = new SerialPort(process.argv[2], {
  baudRate: 9600,
});

app.get("/", function (req, res) {
  res.render("index", { port: port.path });
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

const parser = port.pipe(new Readline());
parser.on("data", function (line) {
  console.log("From Arduino >> " + line);
});

function programFromReq(req) {
  const setupInstructions = parseInstructions(req.body.setup);
  const loopInstructions = parseInstructions(req.body.loop);
  console.log(">>>setup-instructions-count=" + setupInstructions.length);
  console.log(">>>loop-instructions-count=" + loopInstructions.length);
  const setup = new common.SetupScript().setInstructionsList(setupInstructions);
  const loop = new common.LoopScript().setInstructionsList(loopInstructions);
  return new common.GenericArduinoProgram()
    .setSetup(setup)
    .setLoop(loop);
}

app.post("/upload", function (req, res) {
  const program = programFromReq(req);
  const buffer = program.serializeBinary();
  console.log(">>>buffer=" + buffer);
  console.log(">>>buffer-length=" + buffer.length);
  port.write([buffer.length]);
  port.write(buffer);
  res.send(
    "Writing " + buffer.length + " bytes to Arduino Serial port " + port.path
  );
});

app.listen(8081, function () {
  console.log("ArdUI listening on port http://0.0.0.0:8081");
});
