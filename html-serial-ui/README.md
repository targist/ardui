## Install the needed dependencies:

```
brew install node
npm install google-protobuf
npm install express
npm install serialport
```

## Run the server
```
node server/index.js <serial-port-path>
```

for example:
```
node server/index.js /dev/tty.XXX
```
