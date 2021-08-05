const express = require('express');
const app = express();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const SerialPort = require("serialport");
const path = require('path');
const Readline = SerialPort.parsers.Readline;

cors = require("cors");

const url = require('url');
const proxy = require('express-http-proxy');

const { NODE_ENV } = process.env;


const apiProxy = proxy('localhost:3000/', {
  proxyReqPathResolver: req => url.parse(req.baseUrl).path
});
if (NODE_ENV === 'dev')
  app.use('/*', apiProxy);
else if (NODE_ENV === 'prod') {
  const filePath = process.argv[1];
  const dir = path.dirname(filePath);
  const indexFile = path.join(dir, '../../dist', 'index.html');
  const assetsFolder = path.join(dir, '../../dist/assets');
  app.use('/assets', express.static(assetsFolder));
  app.get('/*', (req, res) => {
    res.sendFile(indexFile);
  });
}

// app.use(cors());

const port = new SerialPort(process.argv[1], {
  baudRate: 9600,
});
const parser = port.pipe(new Readline());


io.on('connection', (socket) => {
  const logs = [];
  parser.on('data', (data) => {
    logs.push(data);
    socket.emit('all-logs', logs);
  });
  socket.on('upload', (data) => {
    socket.emit('all-logs', { data });
  })
});

server.listen(8081);