
int initTouch()
/****************************************************************************************************
 * int initTouch() - Initialization of the Touch Unit
 * if an error occures, it will be displayd on the tft display and the serial interface
 ****************************************************************************************************/
{
  // Init of Touch object
  tpReset();
  int iStartTime = millis();
  int iFlagInit = 1;
  while((ft6336u.begin() == false) and (iStartTime + cInitWaitTime > millis()))
  {
		Serial.println("wait for connection to ft6336u (touch controller) ...");
    iFlagInit = 0;
		delay(1000);
  }
  if(iFlagInit == 0)
  {
    pinMode(Touch_INT_PIN, INPUT_PULLUP);
    attachInterrupt(digitalPinToInterrupt(Touch_INT_PIN), tpIntHandle, FALLING);
    Serial.println("Touch-Chip information");
    Serial.print("FT6336U Firmware Version: "); 
    Serial.println(ft6336u.read_firmware_id());  
    Serial.print("FT6336U Device Mode: "); 
    Serial.println(ft6336u.read_device_mode()); 
  }
  else
  {
    dsplyErrorInfo("Error","Can't init touch controller",5,0,0);
    Serial.println("Error: Can't init ft6336u (touch controller)");
  }  
  return iFlagInit;
}

void tpReset(void)
/****************************************************************************************************
 * void tpReset(void) - During the prototype development it was seen, that for some Touch Units
 * an extra reset in front of the initalization function a reset is necessary 
 ****************************************************************************************************/
{ // Reset of the touch unit
  pinMode(Touch_RST_PIN, OUTPUT);
  digitalWrite(Touch_RST_PIN, HIGH);
  delay(Touch_RST_Duration_ms);
  digitalWrite(Touch_RST_PIN, LOW);
  delay(Touch_RST_Duration_ms);
  digitalWrite(Touch_RST_PIN, HIGH);
  delay(Touch_RST_Duration_ms);
}

void tpIntHandle(void)
/****************************************************************************************************
 * void tpIntHandle(void) - this function will be activated, when a user activity will be detected.
 * In the associated program part the value of the variable iTouchIntStatus will be checked
 ****************************************************************************************************/
{
  // Short Interrupt-Handling of the TouchPad
  // set flag - data will be picked up in the main loop
  iTouchIntStatus = true;
}

bool anyTouch()
/****************************************************************************************************
 * bool anyTouch() - check if a user activity was present
 ****************************************************************************************************/
{
  if(iTouchIntStatus)
  { 
    iTouchIntStatus = false;
    Serial.print("AnyTouchFT6336U TD Status: "); 
    Serial.print(ft6336u.read_td_status());  
    return true;
  }
  else
  {
    return false;
  }    
}

bool  waitForTouch()
/****************************************************************************************************
 * void waitForTouch() - The programm represents a message and will wait until
 * the touch unit receives a user activity or the timer expieres.
 * The time for waiting is defined in the constant cWaitForAnyTouch 
 ****************************************************************************************************/
{
  int iTouchWaitStart = 0;
  bool bTouchEvent = false;
  tft.setTextColor(ILI9341_WHITE);
  tft.setCursor(20,205);
  tft.setTextSize(2);
  tft.println("Weiter mit Touch"); 
  tft.setTextSize(2);
  iTouchWaitStart = millis();
  Serial.println("Waiting for Touch");
  while((!bTouchEvent) and (iTouchWaitStart + cWaitForAnyTouch >= millis()))
  {
    bTouchEvent = anyTouch();
    Serial.print("#");
  }
  Serial.println();
  return bTouchEvent;
}