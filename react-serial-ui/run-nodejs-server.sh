#! /bin/bash
set -eux
npm install
cd server
npm install
cd ..
echo "Running NodeJS server for Communication with Arduino"
npm run app


