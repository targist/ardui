#include <Arduino.h>
#include <CommunicationProtocol.h>

void Callbacks::onComplete(char *, int len)
{
  Serial.println("onComplete is not implemented");
};
void Callbacks::onStartReceiving()
{
  Serial.println("onStartReceiving is not implemented");
}

void CommunicationProtocol::setCallbacks(Callbacks *callbacks)
{
  this->callbacks = callbacks;
}

void CommunicationProtocol::readIfAvailable()
{
  String command;
  String sSize;

  if (this->state == CommunicationProtocolState::messageReceived)
  {
    // Serial.println("CommunicationProtocolState::messageReceived");

    int iSep = this->message.indexOf(':');
    command = this->message.substring(0, iSep);
    sSize = this->message.substring(iSep + 1);

    // Serial.println("command received: " + command);
    // Serial.println("size: " + sSize);

    this->totalSize = (int)sSize.toInt();

    if (command == "START_UPDATE")
    {
      this->size = 0; //received data size
      this->state = CommunicationProtocolState::receiving;
      this->callbacks->onStartReceiving();
      // Serial.println("Start receiving");
    }
    else
    {
      this->state = CommunicationProtocolState::waiting;
    }
    this->lastMessageMillis = millis();
    this->message = "";
  }
  else if (this->state == CommunicationProtocolState::receiving)
  {
    unsigned long timeDifference = millis() - this->lastMessageMillis;
    Serial.println(timeDifference);

    if (timeDifference > 1000)
    {
      this->state = CommunicationProtocolState::waiting;
      return;
    }

    // Serial.println("CommunicationProtocolState::receiving");
    int available = Serial.available();
    // Serial.println("Available " + String(available));
    int toRead = min(available, this->totalSize - this->size);
    // Serial.println("to read " + String(toRead));
    while (toRead > 0)
    {
      char c = Serial.read();
      this->buffer[this->size++] = c;

      // Serial.println("Receiving :" + String((int)c) + String(" ") + String(this->size));
      if (this->size == this->totalSize)
      {
        this->callbacks->onComplete(this->buffer, this->totalSize);
        this->state = CommunicationProtocolState::waiting;
      }

      toRead--;
    }
  }
  else if (this->state == CommunicationProtocolState::waiting)
  {
    // Serial.println("CommunicationProtocolState::waiting");

    this->handleChar();
    if (this->state == CommunicationProtocolState::messageReceived)
    {
      this->readIfAvailable();
    }
  }
}

void CommunicationProtocol::handleChar()
{
  while (Serial.available())
  {
    char c = Serial.read();
    if (c == '\n')
    {
      this->state = CommunicationProtocolState::messageReceived;
      break;
    }
    else
    {
      this->message += String(c);
    }
  }
}

CommunicationProtocol::CommunicationProtocol() {}
CommunicationProtocol::~CommunicationProtocol() {}