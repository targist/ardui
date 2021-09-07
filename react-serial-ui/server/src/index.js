const express = require("express");
const SerialPort = require("serialport");
const { programFromReq } = require("./parseInstructions.js");
const { createServer } = require("http");
const proxy = require("express-http-proxy");
const url = require("url");

const path = require("path");
const socketIo = require("socket.io");

const app = express();
const server = createServer(app);

const io = socketIo(server);

const Readline = SerialPort.parsers.Readline;

const { NODE_ENV, ARDUINO_PORT } = process.env;

const apiProxy = proxy("localhost:3000/", {
  proxyReqPathResolver: (req) => url.parse(req.baseUrl).path,
});

if (NODE_ENV === "dev") {
  app.use("/*", apiProxy);
} else if (NODE_ENV === "prod") {
  const filePath = process.argv[1];
  const dir = path.dirname(filePath);
  const indexFile = path.join(dir, "../../dist", "index.html");
  const assetsFolder = path.join(dir, "../../dist/assets");
  app.use("/assets", express.static(assetsFolder));
  app.get("/*", (req, res) => {
    res.sendFile(indexFile);
  });
}

const selectPort = (onData, portArg) => {
  const port = new SerialPort(portArg || ARDUINO_PORT, {
    baudRate: 9600,
  });
  const parser = port.pipe(new Readline());
  parser.on("data", onData);
  const MAX_BUFFER_SIZE = 255;
  const getHandleUpload = (logs) => (data) => {
    try {
      const program = programFromReq({ body: data });
      const buffer = program.serializeBinary();
      console.log("buffer=" + buffer);
      console.log("buffer-length=" + buffer.length);
      logs.push(
        "Writing " + buffer.length + " bytes to Arduino Serial port " + port.path
      );
      if (buffer.length > MAX_BUFFER_SIZE) {
        logs.push("Error on write: bytes length = " + buffer.length + " max length possible = " + MAX_BUFFER_SIZE);
      } else {
        port.write([buffer.length]);
        port.write(buffer, function (err) {
          if (err) {
            logs.push('Error on write: ' + err.message);
          }
        });
      }
    } catch (error) {
      logs.push(`error: Please check your instructions.`);
    }
  };

  return { getHandleUpload, close: () => { port.close() } };
}

io.on("connection", (socket) => {
  const logs = [];
  const handleUpload = {};
  const closePort = {};
  handleUpload.current = (data) => {
    logs.push("Device not yet connected");
  }

  socket.on("upload", (data) => {
    handleUpload.current(data);
    socket.emit("logs", logs);
  });

  socket.on("get-available-ports", () => {
    SerialPort.list().then(ports => {
      socket.emit('available-ports', ports);
    })
  });

  socket.on("select-port", (port) => {
    console.log("select-port", { port })
    logs.length = 0;
    closePort.current && closePort.current();
    const current = selectPort((data) => {
      logs.push(data);
      socket.emit("logs", logs);
    }, port);
    handleUpload.current = current.getHandleUpload(logs);
    closePort.current = current.close;
    socket.emit("port-selected", port);
  })
});

server.listen(8081);
