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

const httpServer = createServer(app);

const io = socketIo(server);

const Readline = SerialPort.parsers.Readline;

const { NODE_ENV } = process.env;

const apiProxy = proxy("localhost:3000/", {
  proxyReqPathResolver: (req) => url.parse(req.baseUrl).path,
});
if (NODE_ENV === "dev") app.use("/*", apiProxy);
else if (NODE_ENV === "prod") {
  const filePath = process.argv[1];
  const dir = path.dirname(filePath);
  const indexFile = path.join(dir, "../../dist", "index.html");
  const assetsFolder = path.join(dir, "../../dist/assets");
  app.use("/assets", express.static(assetsFolder));
  app.get("/*", (req, res) => {
    res.sendFile(indexFile);
  });
}

// app.use(cors());

const port = new SerialPort(process.argv[1], {
  baudRate: 9600,
});
const parser = port.pipe(new Readline());

const getHandleUpload = (logs) => (data) => {
  try {
    // const { setup, loop } = data;
    const program = programFromReq({ body: data });
    const buffer = program.serializeBinary();
    console.log("buffer=" + buffer);
    console.log("buffer-length=" + buffer.length);
    port.write([buffer.length]);
    port.write(buffer);
    logs.push(
      "Writing " + buffer.length + " bytes to Arduino Serial port " + port.path
    );
  } catch (error) {
    // TODO: use different log array for errors
    logs.push(`error: ${JSON.stringify(error)}`)
  }
};

io.on("connection", (socket) => {
  const logs = [];
  const handleUpload = getHandleUpload(logs);
  parser.on("data", (data) => {
    logs.push(data);
    socket.emit("logs", logs);
  });
  socket.on("upload", (data) => {
    handleUpload(data);
    socket.emit("logs", logs);
  });
});

server.listen(8081);
