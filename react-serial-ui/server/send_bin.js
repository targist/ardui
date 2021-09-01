


const fs = require('fs');
const serialport = require('serialport');

serialport.list().then(console.log);

const sp = new serialport('/dev/tty.ESP32test-ESP32SPP');
const filePath = '/Users/yousfisaad/Documents/PlatformIO/Projects/bluetooth/.pio/build/esp32cam/firmware.bin';

sp.on('open', () => {
  console.log('open');

  fs.promises.readFile(filePath).then((data) => {
    const byteLength = data.byteLength;
    sp.write(`file: ${byteLength}`);
    sp.write('\n');
    sp.write(Buffer.from(data));
    sp.write(Buffer.from(data));
    sp.write(Buffer.from(data));
    sp.write('\n');
    // sp.write(data);
  })
});

sp.pipe(new serialport.parsers.Readline()).on('data', (data) => {
  console.log('data', data);
});