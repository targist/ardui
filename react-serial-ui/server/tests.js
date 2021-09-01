



const serialport = require('serialport');

serialport.list().then(console.log);

const sp = new serialport('/dev/tty.ESP32test-ESP32SPP');

sp.on('open', () => {
  console.log('open');
  sp.write('hello');
});

sp.on('data', (data) => {
  console.log('data', data);
});