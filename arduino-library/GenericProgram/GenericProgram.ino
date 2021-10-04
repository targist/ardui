#include <Arduino.h>
#include <ArdUI.h>
#include <CommunicationProtocol.h>

#define BAUD ((int)9600)

CommunicationProtocol comPro;
int *pNext_buffer_length;
int *pCurr_buffer_length;
uint8_t **pBuffer;

void MyCallbacks::onComplete(char *buffer_, int len)
{
  getPointers(&pNext_buffer_length, &pCurr_buffer_length, &pBuffer);

  // TODO: review
  memcpy((void *)pBuffer, (void *)buffer_, len);

  *pCurr_buffer_length = len;
  *pNext_buffer_length = ((int)-1);

  Serial.println(F("A new configuration has been loaded"));
  Serial.println(F("Running Setup.."));
  execute_setup();
  Serial.println(F("Done"));
}

void MyCallbacks::onStartReceiving()
{
  getPointers(&pNext_buffer_length, &pCurr_buffer_length, &pBuffer);

  // this prevents the execution of the current program durring the update
  *pCurr_buffer_length = 0;
  *pNext_buffer_length = ((int)-1);
}

void setup()
{
  Serial.begin(BAUD);
  Serial.println(F("Board reset"));

  comPro.setCallbacks(new MyCallbacks());
}

void loop()
{
  comPro.readIfAvailable();
  execute_loop();
}
