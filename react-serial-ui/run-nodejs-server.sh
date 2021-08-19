#! /bin/bash
set -eux
npm install
cd server
npm install
cd ..
echo "Running NodeJS server on port $1"
ARDUINO_PORT=$1 npm run app

