#include <Callbacks.h>

#define BUFFER_SIZE 256

enum CommunicationProtocolState
{
  waiting,
  receiving,
  messageReceived
};

class CommunicationProtocol
{
public:
  CommunicationProtocol(void);
  ~CommunicationProtocol(void);

  // bool begin();

  void setCallbacks(Callbacks *callbacks);
  void readIfAvailable(void);

private:
  void handleChar(void);

  Callbacks *callbacks = NULL;
  String message;
  char buffer[BUFFER_SIZE];
  int size = 0;
  int totalSize = 0;
  CommunicationProtocolState state = CommunicationProtocolState::waiting;
  unsigned long lastMessageMillis = 0;
};
