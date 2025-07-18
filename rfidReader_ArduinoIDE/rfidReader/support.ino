int analyseResponse(String strAnswer)
/****************************************************************************************************
 * void analyseResponse(String strAnswer) - the answer from the server will be converted to a json
 * document to extract the parameter and values
 * In dependency of the content further actions will be started.
****************************************************************************************************/
{
  JsonDocument jDoc;
  DeserializationError dsjsErr;
  int iAnswer = 0;
  Serial.println("Start of analyseResponse");
  dsjsErr = deserializeJson(jDoc, strAnswer);
  if(dsjsErr == DeserializationError::Ok)
  {
    if (jDoc.containsKey("DEVUSECASE") and (jDoc.containsKey("STATE")))
    {
      //if (jDoc.containsKey("DEVUSECASE"))
      //{
      Serial.println("UseCase - " + String(jDoc["DEVUSECASE"]));
      Serial.println("State   - " + String(jDoc["STATE"]));
      if((String(jDoc["DEVUSECASE"]) == "ERROR") and (String(jDoc["ERROR"]) != "") and (String(jDoc["STATE"]) == "ERROR"))
      {
        strMsg = String(jDoc["ERROR"]);
        dsplyErrorInfo("Error",strMsg,5,1,99);   // wenn zu der angegebenen MAC-Adresse 
        Serial.println("Error: MAC-Addr not found - "+WiFi.macAddress());            // keine Daten gefunden werden konnten
        playError();
        eState = error;
        return -1;
      }
      else if (String(jDoc["STATE"]) == "START")
      {
        Serial.println("setInitData(jDoc)");
        setInitData(jDoc);
      }
      setUseCase(String(jDoc["DEVUSECASE"]));    
      setState(String(jDoc["STATE"])) ;
      Serial.println("Inside analyseResponse");
      Serial.println("UseCase: "+String(jDoc["DEVUSECASE"])+ " - " + getUseCase());
      Serial.println("State  : "+String(jDoc["STATE"]));
      if((eUC == SwitchBox) and ((eState == end) or (eState == working))) //String(jDoc["STATE"]) != "Idle"))
      {
        Serial.println("evalSwitchBoxResp(jDoc)");
        evalSwitchBoxResp(jDoc);
      }
      else if ((eUC == GateKeeper) and (eState == end)) // String(jDoc["STATE"]) != "END"))
      {
        Serial.println("evalGateKeeperResp(jDoc)");
        evalGateKeeperResp(jDoc);
      }
      else if ((eUC == Counter) and (eState == end)) // (String(jDoc["STATE"]) == "END"))
      {
        Serial.println("evalCounterResp(jDoc)");
        evalCounterResp(jDoc);
      }
      else if ((eUC == AddTag) and (eState == end))
      {
        Serial.println("evalAddTagResp(jDoc)");
        evalAddTagResp(jDoc);
      }
      else
      {
        Serial.println("errorUnknownUseCase() or state or unknownAnything");
        errorUnknownUseCase();
      }
      //}
    }
	  else
	  {
	    dsplyErrorInfo("Error","Unknown Json received!",5,1,0);
      playError();
      stop();
	  }
	}
  else
  {
    dsplyErrorInfo("Error","No Json received!",5,1,0);
    playError();
    stop();
  }
  return 0;
}

void errorUnknownUseCase()
{
     bIntChange = true;
	   eState = error;
	   strHeader = "Error";
	   strMsg = "Unknown UseCase!";
	   iIconNo = 11; // error.bmp
}

void evalSwitchBoxResp(JsonDocument jDoc)
{
  //RFID-Tag not found -> error message -> end -> idle
  //User with RFID-Tag has no tool access -> info message -> end -> idle
  //User with RFID-Tag has access to the tool -> working
  Serial.println("Missing: evalSwitchBoxResp(JsonDocument jDoc)");
  if(String(jDoc["ICON"]) == "NOREG")
  {
    // RFID-Tag not found -> error message -> end -> idle
    bIntChange = true;
    bIntRunning = false;
	  eState = end;
    bUnitRun  = false;
	  strHeader = String(jDoc["CUSTOMERNAME"]);
	  strMsg = String(jDoc["ERROR"]);
	  iIconNo = 7; // noreg.bmp
    strUnits = String(jDoc["UNITS"]);
    Serial.println("evalSwitchBoxResp - Customer uses unregistered RFID-TAG");
    playError();
  }
  else if(String(jDoc["ICON"]) == "STOP")
  {
    // User has no access to the tool, was working, like to switch off now  -> info message -> end -> idle
    bIntChange  = true;
    bIntRunning = false;
    bUnitRun    = false;
	  eState = end;
    strHeader = String(jDoc["CUSTOMERNAME"]);
	  strMsg = String(jDoc["ERROR"]); 
	  iIconNo = 6; // no access bmp	 
    strUnits = String(jDoc["UNITS"]);
    Serial.println("evalSwitchBoxResp - Customer has no access");
    Serial.println("??? Ist stete End gesetzte für temporäre Darstellung und Übergang in den Idle-State");
  }  
  else if((String(jDoc["ICON"]) == "") and (String(jDoc["STATE"])=="WORKING") and (String(jDoc["ERROR"]) == ""))
  {  // Usere has access to the tool -> enable power -> measure the time
    bIntChange  = true;
    bIntRunning = false;
    bUnitRun    = true;    
    eState      = working;
    bUnitRun    = true; 

    strCustName = String(jDoc["CUSTOMERNAME"]);
    strStartEnd = String(jDoc["CUSTOMERSTARTSTOP"]);
    strUnits = String(jDoc["UNITS"]);
    initUnitCounter();
    SolenoidON();
    bDsplySwitchBoxInit = true;
    Serial.println("evalSwitchBoxResp - Customer is starting");
  }
  else if((String(jDoc["ICON"]) == "BYE") and (String(jDoc["STATE"])=="END") and (String(jDoc["ERROR"]) == ""))
  { // Customer is leaving
    bIntChange  = true;
    bIntRunning = false;
    bUnitRun    = false;
    eState = end;
    strHeader = String(jDoc["CUSTOMERNAME"]);
    strMsg = String(jDoc["CUSTOMERSTARTSTOP"]);
    strUnits = String(jDoc["UNITS"]);
    iIconNo = 10; // bye.bmp     
    initUnitCounter();
    SolenoidOFF();
    // Ausgabe der auf dem ESP32 und der auf dem Server ermittelten Zeiten
    Serial.println();
    Serial.println();
    Serial.println("evalSwitchBoxResp - Customer leaving");
    Serial.println("ermittelte Zeiten");
    Serial.println("ESP    Start   :"+strWorkStartTime+" End :"+strWorkEndTime + " Dauer :"+strWorkUnitMin+":"+strWorkUnitSec);
    Serial.println("Server Start/End :"+strMsg+" Dauer :"+strUnits);
    Serial.println();
    Serial.println();
    Serial.println();
    Serial.println();
  }
}

void evalGateKeeperResp(JsonDocument jDoc)
{
   Serial.println("evalGateKeeperResp - Start");
   Serial.println("Error - "+ String(jDoc["ERROR"]));
   Serial.println("Icon  - "+String(jDoc["ICON"]));
   Serial.println("Name  - "+String(jDoc["CUSTOMERNAME"]));
   Serial.println("StartStop - "+String(jDoc["CUSTOMERSTARTSTOP"]));
   Serial.println("Units - "+ String(jDoc["UNITS"]));
   if(String(jDoc["ICON"]) == "NOREG")
   { // RFID-Tag not found -> error message -> end -> idle
     bIntChange = true;
     bIntRunning = false;
	   eState = end;
     bUnitRun  = false;
	   strHeader = "Error";
	   strMsg = String(jDoc["ERROR"]);
	   iIconNo = 7; // noreg.bmp
     strUnits = String(jDoc["UNITS"]);
     Serial.println("evalGateKeeperResp - Customer uses unregistered RFID-TAG");
     playError();
   }
   else if(String(jDoc["ICON"]) == "STOP") 
   { // User has no general access to the workshop area  -> info message -> end -> idle
     bIntChange  = true;
     bIntRunning = false;
     bUnitRun    = false;
	   eState = end;
     strHeader = String(jDoc["CUSTOMERNAME"]);
	   strMsg = String(jDoc["ERROR"]); 
	   iIconNo = 6; // no access bmp	 
     strUnits = String(jDoc["UNITS"]);
     Serial.println("evalGateKeeperResp - Customer has no access");
     playError();
   }
   // User has access to workshop area -> end -> idle
   //else if((String(jDoc["ERROR"]) == "") and (String(jDoc["CUSTOMERSTARTSTOP"]) != "") and (String(jDoc["ICON"]) == "HI"))  
   else if(String(jDoc["ICON"]) == "HI")  
   { // Customer is coming
     bIntChange  = true;
     bIntRunning = false;
     bUnitRun    = true;    
 	   eState = end;
     strHeader = String(jDoc["CUSTOMERNAME"]);
	   strMsg = String(jDoc["CUSTOMERSTARTSTOP"]);
	   iIconNo = 9; // hello.bmp	 
     strUnits = String(jDoc["UNITS"]);
     initUnitCounter();
     Serial.println("evalGateKeeperResp - Customer is starting");
     playOK();
   }
   //else if((String(jDoc["ERROR"]) == "") and (String(jDoc["CUSTOMERSTARTSTOP"]) != "") and (String(jDoc["ICON"]) == "BYE")) 
   else if(String(jDoc["ICON"]) == "BYE") 
   { // Customer is leaving
     bIntChange  = true;
     bIntRunning = false;
     bUnitRun    = false;
	   eState = end;
     strHeader = String(jDoc["CUSTOMERNAME"]);
	   strMsg = String(jDoc["CUSTOMERSTARTSTOP"]);
     strUnits = String(jDoc["UNITS"]);
	   iIconNo = 10; // bye.bmp	    
     initUnitCounter();
     Serial.println("evalGateKeeperResp - Customer leaving");
     playOK();
   }
   else
   {
     Serial.println("Error: Data don't fit"); 
     if ("" == NULL)
       Serial.println("null ok") ;
   }
}

void evalAddTagResp(JsonDocument jDoc)
{
  if((String(jDoc["ERROR"]) == "") and (String(jDoc["ICON"]) == "OK") and (String(jDoc["STATE"]) == "END"))
  { // display RFID-TAG-No+ " added"
    bIntChange = true;
    bIntRunning = false;
	  eState = end;
	  strHeader = "RFID-Tag";
	  strMsg = strWorkID+" added";
	  iIconNo = 8;
	  //dsplyErrorInfo(strHeader,strMsg,0,0,iIconNo);
	  Serial.println("evalAddTagResp(JsonDocument jDoc) Data added");
  }
  else
  { // unexpected answer received -> display error message
    bIntChange = true;
	  eState = end;
	  strHeader = String(jDoc["CUSTOMERNAME"]);
    strMsg    = String(jDoc["ERROR"]);
    iIconNo = 12;                                // rfiderr bitmap darstellen
    Serial.println("Error: "+strMsg);            // keine Daten gefunden werden konnten
    playError();   
  }
}

void evalCounterResp(JsonDocument jDoc)
{
  // Copy RFID-Tag to temporary, other action are to define on the front end side
  if((String(jDoc["ERROR"]) == "") and (String(jDoc["ICON"]) == "OK") and (String(jDoc["STATE"]) == "END"))
  { // display RFID-TAG-No+ " transferred"
    bIntChange = true;
    bIntRunning = false;
    eState = end;
    strHeader = "RFID-Tag";
    strMsg = strWorkID+" transferred";
	  iIconNo = 8;
	  //dsplyErrorInfo(strHeader,strMsg,0,0,iIconNo);
	  Serial.println("evalCounterResp(JsonDocument jDoc) Data transferred");
  }
  else
  { // unexpected answer received -> display error message
    bIntChange = true;
	  eState = end;
	  Serial.println("Is missing missing??? : evalCounterResp(JsonDocument jDoc) Data extraction");
	  strHeader = String(jDoc["CUSTOMERNAME"]);
    strMsg    = String(jDoc["ERROR"]);
    //dsplyErrorInfo("Error",strMsg,5,1,25);       // wenn zum angegebenen rfid-Tag
    Serial.println("Error: "+strMsg);            // keine Daten gefunden werden konnten
    playError();   
  }
}

String getState()
{
  String strRetState="";
  if(eState ==  error)
  {
    strRetState = "Error";
  }
  else if(eState ==  start)
  {
    strRetState = "Start";
  }
  else if(eState ==  idle)
  {
    strRetState = "Idle";
  }
  else if(eState ==  working)
  {
    strRetState = "Working";
  }
  else if(eState ==  end) 
  {
    strRetState = "End";
  }
  else
  {
    Serial.println(" Error: Unknown State!!!");
  }
  return strRetState;
}

void setState(String strState)
{
  Serial.println("setState = "+strState);
  strState.toUpperCase();
  if(strState.equals("INIT"))
  {
    eState = start;
  }
  else if (strState == "IDLE") 
  {
    eState = idle;
    chngState2Idle(); 
  }
  else if (strState == "WORKING")
  {
    eState = working;
  }
  else if (strState == "END")
  {
    eState = end;
  }
  else
  {
    eState = error;
  } 
}

void setUseCase(String strUseCase)
{
  char cUseCase = char(strUseCase[0]);
  if(strUseCase.length() == 1)
  {
    if(cUseCase == 'S')
    {
      eUC = SwitchBox;
    }
    else if (cUseCase == 'G')
    {
      eUC = GateKeeper;
    }
    else if (cUseCase == 'C')
    {
      eUC = Counter;
    }
    else if (cUseCase == 'A')
    {
      eUC = AddTag;
    }
    else
    {
      eUC = UnKnown;
      Serial.println("!!!Unknown Use Case = "+strUseCase);
    }
  }
  else
  {
      eUC = UnKnown;
      Serial.println("!!!Unknown Use Case = "+strUseCase);
  }
  Serial.println("UseCase = "+String(cUseCase) + " " + strUseCase);
}

void setInitData(JsonDocument jDoc)
{
  int iAnswer = 0;
  eState = idle;
  chngState2Idle();
  cDevUseCase = char(String(jDoc["DEVUSECASE"])[0]);
  Serial.println("Start of setInitData(JsonDocument jDoc) - UC" +String(jDoc["DEVUSECASE"])+" "+String(cDevUseCase)+" State "+getState());
  setUseCase(String(jDoc["DEVUSECASE"]));
  if (jDoc.containsKey("DEVNAME")) 
  {
    strDevName = String(jDoc["DEVNAME"]);
    Serial.println("jDoc[DEVNAME] = "+strDevName);
  }   
  if(cDevUseCase == 'S') // SwitchBox
  {
    Serial.println("SwitchBox");
    iAnswer = jsExtractSwitchBoxData(jDoc);
    if(iAnswer == 0)
    {
      sTimeRunning = jsExtractDate(jDoc);
      if(sTimeRunning.uiFlagValid == 1)
      {
        //dsplyMask();
        eState = idle;
        Serial.println("End of analyseResponse -  SwitchBox");
        //return 0;
      }
    }
  }
  else if(cDevUseCase == 'C') // Counter
  {
      Serial.println("Counter: Check Code what is missing!!");
      strTerminal = String(jDoc["TERMINAL"]);
      Serial.print("Received Data for UseCase Counter : ");
      eState = idle;
      serializeJson(jDoc,Serial);
      Serial.println();
  }
  else if(cDevUseCase == 'G') // GateKeeper
  {
    Serial.println("GateKeeper");
    eState = idle;
    Serial.print("Received Data for UseCase GateKeeper : ");
    serializeJson(jDoc,Serial);
    Serial.println();
  }
  else if (cDevUseCase == 'A')
  {
    Serial.println("AddTag");
    eState = idle;
    Serial.print("Received Data for UseCase AddTag : ");
    serializeJson(jDoc,Serial);
    Serial.println();    
  }
  else
  {
    dsplyErrorInfo("Error","Unknown UseCase!",5,1,99);
    Serial.println("Error: unbekannter UseCase "+cDevUseCase);
    stop();
  }
  sTimeRunning = jsExtractDate(jDoc);
}

String Uid2StrHex(MFRC522::Uid uid)
/****************************************************************************************************
 * String Uid2StrHex(MFRC522::Uid uid) - The library of the RFID reader is handlig the ID
 * of a RFID tag as a byte array for the output on display or the exchange with the server
 * the RFID will be converted from a byte array to a String object.
 ****************************************************************************************************/
{  // convert byte array uid to String
  String strReturn = "";
  // index of char array represent the associated character
  const char hexDigits[] = "0123456789ABCDEF";
  for (int i = 0; i < uid.size; ++i) 
  {
    strReturn += hexDigits[(uid.uidByte[i] >> 4) & 0x0F];
    strReturn += hexDigits[uid.uidByte[i] & 0x0F];
    //strReturn += " ";
  }  
  //strReturn.trim();
  return strReturn;
}

void stop()
{
  Serial.println("STOP");
  for(;;)
  {
     delay(1000);
  }
}

void softReset()
{
  SolenoidOFF();
  Serial.println("Relais ausgeschaltet- Restart ...");
  ESP.restart();
}

sQdrupel getWidthHeight(int iXPos, int iYPos, String strTxt)
{
  int16_t  x1, y1;
  uint16_t w, h;
  sQdrupel retQDrupel;
  tft.getTextBounds(strTxt, iXPos, iYPos, &x1, &y1, &w, &h);
  retQDrupel.x = x1;
  retQDrupel.y = y1;
  retQDrupel.w = w;
  retQDrupel.h = h;
  return retQDrupel;
}

int iCenterTxt(int iXPos, int iYPos, String strTxt)
{
  int16_t  x1, y1;
  uint16_t w, h;
  float fDspHalf = 0;
  float fWidthHalf = 0;
  tft.getTextBounds(strTxt, iXPos, iYPos, &x1, &y1, &w, &h);
  fDspHalf = float(cDspXMax+1)/float(2);
  fWidthHalf = float(w)/float(2);
  x1 = int(fDspHalf-fWidthHalf);
  return x1;
}

void playOK()
{
  tone(BUZZZER_PIN,4978, 125);
  delay(250);
  tone(BUZZZER_PIN,4000, 125);
}

void playError()
{
  tone(BUZZZER_PIN, 200, 500);
}


String getUseCase()
{
  String strReturn = "";
  if (eUC == SwitchBox)
  {
    strReturn = "S";
  }
  else if (eUC == GateKeeper)
  {
    strReturn = "G";
  }
  else if (eUC == Counter)
  {
    strReturn = "C";
  }
  else if (eUC == GetInit)
  {
    strReturn = "GetInit";
  }
  else if (eUC == AddTag)
  {
    strReturn = "A";
  }
  else if (eUC == UnKnown)
  {
    strReturn = "Unknown";
  }
  Serial.println("getUseCase = "+strReturn);
  return strReturn;
}

void rfidReset(void)
/****************************************************************************************************
 * void tpReset(void) - During the prototype development it was seen, that for some Touch Units
 * an extra reset in front of the initalization function a reset is necessary 
 ****************************************************************************************************/
{ // Reset of the touch unit
  pinMode(Pin_MFRC522_Reset, OUTPUT);
  digitalWrite(Pin_MFRC522_Reset, HIGH);
  delay(Touch_RST_Duration_ms);
  digitalWrite(Pin_MFRC522_Reset, LOW);
  delay(Touch_RST_Duration_ms);
  digitalWrite(Pin_MFRC522_Reset, HIGH);
  delay(Touch_RST_Duration_ms);
}

void evalSwitchBoxAction(String strRfidTag)
{
  if(eState == idle) // and  uiFlagRunTimer == 0)
  {
    jsonSendDoc = getJSONUserData(strRfidTag);
    strWorkID = strRfidTag;   // sichere die ID, damit Dich niemand anders abmelden kann :-)
    dsplyErrorInfo("Info","Lade Daten", 1, 0, 5);
    sendRequest(jsonSendDoc);
    if( uiFlagRunTimer == 1)
    {
      fSolUnitsMin  = 0;
      Serial.println("Toggle Solenoid state");
    }
  }
  else if(eState == working) // and (uiFlagRunTimer == 1))
  {
    if(strWorkID == strRfidTag)
    {
      // finish your working task
      SolenoidOFF();
      uiFlagRunTimer = 0;
      dsplyErrorInfo("Info","Schreibe Daten", 1, 0, 5);
      jsonSendDoc = getJSONUserData(strRfidTag);
      sendRequest(jsonSendDoc);
      fSolUnitsMin  = 0;
    }
    else
    {
      Serial.println("Fremdes rfid-Tag erkannt!");
      dsplyErrorInfo("Fehler","falscher rfid-Tag!", 5, 1, 12);   // roter Rahmen, Touch erlauben, "flasches rfid-Tag gefunden"-Icon
      dspClear(); 
    }
  }
}

void evalGateKeeperAction(String strRfidTag)
{
  jsonSendDoc = getJDocGKData(strRfidTag);
  playOK();
  sendRequest(jsonSendDoc);
}

void evalCounterAction(String strRfidTag)
{ // evaluation of the activity at the counter or
  // to add the rfidTag to the Card table on the server 
  jsonSendDoc = getJDocCounter(strRfidTag);
  playOK();
  sendRequest(jsonSendDoc);
}

void evalTouchAction()
{
  // auskommentiert da auch für den UseCse SwitchBox bei unbekannter Karte diese Funktion erfordert
  //if((eUC == GateKeeper) or (eUC == Counter))
  //{ 
    if(eState == end)
    {  // use case GateKeeper or GateKeeper -> cancel display message on touch event => switch back to idle state
      chngState2Idle();
    }
  //}
}

void chngState2Idle()
{ // reset intervall settings 
  bIntChange  = false;
  bIntRunning = false;
  lIntStartAt = 0;
  // Switching back to idle mode
  eState      = idle;
  Serial.println("Switching state to idle (UC="+String(eUC)+")");
  dspClear();
  strHeader = "";
  strMsg    = "";
  bUnitRun = false;  // for use case GateKeeper

   // display any icon when we go back to idle state
  uiUpdatedUC = 1;   
  uiUpdatedRFID = 1;
  //bGKMinUpdateFlag = false;
}

int getIconNo(String strIcon)
{
  int iRetVal = 0;
  strIcon.toUpperCase();
  if (strIcon == "HI")
  {
    iRetVal = 9;
  }
  else if (strIcon == "BYE")
  {
    iRetVal = 10;
  }
  else if (strIcon == "STOP")
  {
    iRetVal = 6;
  }
  else if (strIcon == "ERROR")
  {
     iRetVal = 11;
  }
  else
  {
    iRetVal = 11;
    Serial.println("Error in getIconNo(strIcon()");
  }
  return iRetVal; 
}

void initUnitCounter()
{
  // Display the units for the use case GateKeeper (and SwitchBox)
  // split in minutes units and seconds
  // expected format XX:YY
  // here XX represent the minutes
  // and YY are the seconds
  int iPos = strUnits.indexOf(":");
  int iLength = strUnits.length();
  sQdrupel sQStrSize;
  String strMinutes = "";
  String strSeconds = "";
  if (iPos>0)
  {
    strMinutes = strUnits.substring(0,iPos);
    strSeconds = strUnits.substring(iPos+1,iLength);
    Serial.println("dsplyUnits -----------------");
    Serial.println("Minutes "+strMinutes);
    Serial.println("Seconds "+strSeconds);
    Serial.println("----------------------------");
    uiUnitSeconds = strSeconds.toInt();
    liUnitMinutes = strMinutes.toInt();
    if(iIconNo == 9) // only start the counter when the customer enters the workshop, here the IconNo = 9 = "HI"
    {
      bUnitRun  = true;              // unit timer is running
      liUnitCounterMil = millis();   
    }
    bUnitMinInitDisplay = true;    // flag for initial display of minutes for IconNo=9 and IconNo = 10
    //sQStrSize = getWidthHeight(20,20,strUnits);
    //bGKMinUpdateFlag = true;  // that the unit minutes will be displayed at display init
  }
  else
  {
    Serial.println("ERROR - initUnitCounter: Units delimiter not found!");
  }
}

bool incrUnitCounter()
{
  bool bUpdateMinFlag = false;
  if((liUnitCounterMil + 1000) <= millis())
  {
    liUnitCounterMil = millis();
    uiUnitSeconds = uiUnitSeconds + 1;
    if(uiUnitSeconds > 59)
    {
      uiUnitSeconds = 0;
      liUnitMinutes = liUnitMinutes + 1;
      bUpdateMinFlag = true;
    }
  }
  return bUpdateMinFlag;
}