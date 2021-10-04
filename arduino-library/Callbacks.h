

// TODO: Maybe interface
class Callbacks
{
public:
  Callbacks(){};
  ~Callbacks(){};

  virtual void onComplete(char *, int len);
  virtual void onStartReceiving(void);
};

class MyCallbacks : public Callbacks
{
  // MyCallbacks() : Callbacks() {}
  void onComplete(char *, int len);
  void onStartReceiving(void);
  // public:
};