

timeproj_t jsExtractDate(JsonDocument jDoc)
/****************************************************************************************************
 * void jsExtractDate(JsonDocument jDoc) - the answer from the server, was alredy converted to a json
 * document, now the date will be extracted
****************************************************************************************************/
{
  uint8_t uiFlagTimeH   = 0;
  uint8_t uiFlagTimeM   = 0;
  uint8_t uiFlagTimeS   = 0;

  uint8_t uiFlagDateY   = 0;
  uint8_t uiFlagDateM   = 0;
  uint8_t uiFlagDateD   = 0;

  timeproj_t sTimeTmp;

  if (jDoc.containsKey("TIMEH")) 
  {
    sTimeTmp.uiDateHour = int(jDoc["TIMEH"]);
    Serial.println("jDoc[TIMEH] = "+String(sTimeTmp.uiDateHour));
    uiFlagTimeH = 1;
  }
  if (jDoc.containsKey("TIMEM")) 
  {
    sTimeTmp.uiDateMinute = int(jDoc["TIMEM"]);
    Serial.println("jDoc[TIMEM] = "+String(sTimeTmp.uiDateMinute));
    uiFlagTimeM = 1;
  }
  if (jDoc.containsKey("TIMES")) 
  {
    sTimeTmp.uiDateSecond = int(jDoc["TIMES"]);
    Serial.println("jDoc[TIMES] = "+String(sTimeTmp.uiDateSecond));
    uiFlagTimeS = 1;
  }
  if (jDoc.containsKey("DATEY")) 
  {
    sTimeTmp.uiDateYear = int(jDoc["DATEY"]);
    Serial.println("jDoc[DATEY] = "+String(sTimeTmp.uiDateYear));
    uiFlagDateY = 1;
  }
  if (jDoc.containsKey("DATEM")) 
  {
    sTimeTmp.uiDateMonth = int(jDoc["DATEM"]);
    Serial.println("jDoc[DATEM] = "+String(sTimeTmp.uiDateMonth));
    uiFlagDateM = 1;
  }
  if (jDoc.containsKey("DATED")) 
  {
    sTimeTmp.uiDateDay = int(jDoc["DATED"]);
    Serial.println("jDoc[DATED] = "+String(sTimeTmp.uiDateDay));
    uiFlagDateD = 1;
  }  
  if((uiFlagTimeH == 1) and (uiFlagTimeM == 1) and (uiFlagTimeS == 1) and (uiFlagDateY == 1) and (uiFlagDateM == 1) and (uiFlagDateD == 1))
  {
    setTime(sTimeTmp); // (( uiDateSecond,uiDateMinute,uiDateHour,uiDateDay,uiDateMonth,uiDateYear))
  }
  if(uiFlagTimeH == 0)
  {
    dsplyErrorInfo("Error","kein Json-TimeH!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-TimeH missing!");
  }
  if(uiFlagTimeM == 0)
  {
    dsplyErrorInfo("Error","kein Json-TimeM!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-TimeM missing!");
  }
  if(uiFlagTimeS == 0)
  {
    dsplyErrorInfo("Error","kein Json-TimeS!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-TimeS missing!");
  }
  if(uiFlagDateY == 0)
  {
    dsplyErrorInfo("Error","kein Json-DateY!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-DateY missing!");
  }
  if(uiFlagDateM == 0)
  {
    dsplyErrorInfo("Error","kein Json-DateM!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-DateM missing!");
  }  
  if(uiFlagDateD == 0)
  {
    dsplyErrorInfo("Error","kein Json-DateD!",5,1,0);
    Serial.println("Error: jsExtractDate(JsonDocument jDoc)-  Json-DateD missing!");
  }  
  if((uiFlagTimeH == 0) or (uiFlagTimeM == 0) or (uiFlagTimeS == 0) or (uiFlagDateY == 0) or (uiFlagDateM == 0) or (uiFlagDateD == 0))
  {
    Serial.println("Error: Extraktion Zeit und Datum");
    sTimeTmp.uiFlagValid = -1;
  }
  else
  {
    Serial.println("OK: Extraktion Zeit und Datum");
    sTimeTmp.uiFlagValid = 1;
  }
  return sTimeTmp;
}

int jsExtractSwitchBoxData(JsonDocument jDoc)
/****************************************************************************************************
 * void jsExtractInitData(JsonDocument jDoc) - the RFID reader is starting and to request the 
 * configuration data from the server the json document will be prepared here.
****************************************************************************************************/
{
  uint8_t uiFlagDevName = 0;
  uint8_t uiFlagUseCase = 0;
  uint8_t uiFlagHTTP    = 0;
  uint8_t uiFlagDevIP   = 0;
  uint8_t uiFlagDevOn   = 0;
  uint8_t uiFlagDevOff  = 0;
  uint8_t uiFlagError   = 0;
  Serial.println("Start of jsExtractSwitchBoxData(JsonDocument jDoc)");
  if (jDoc.containsKey("DEVNAME")) 
  {
    strDevName = String(jDoc["DEVNAME"]);
    Serial.println("jDoc[DEVNAME] = "+strDevName);
    uiFlagDevName = 1;
  } 
  if (jDoc.containsKey("DEVUSECASE")) 
  {
    cDevUseCase = char(String(jDoc["DEVUSECASE"])[0]);
    if (cDevUseCase == 'G')
    {  // GateKeeper
      eUC = GateKeeper;
    }
    else if(cDevUseCase == 'S')
    { // SwitchBox
      eUC = SwitchBox;
    }
    else if (cDevUseCase == 'C')
    { // Counter 
      eUC = Counter;
    }
    else
    { // Unknown UseCase
      eUC = UnKnown;
    }
    Serial.println("jDoc[DEVUSECASE] = "+cDevUseCase);
    uiFlagUseCase = 1;
  }
  if (jDoc.containsKey("STARTHTTP")) 
  {
    strHTTPstart = String(jDoc["STARTHTTP"]);
    Serial.println("jDoc[STARTHTTP] = "+strHTTPstart);
    uiFlagHTTP = 1;
  }
  if (jDoc.containsKey("DEVIP")) 
  {
    strIpAddrSolenoid  = String(jDoc["DEVIP"]);
    Serial.println("jDoc[DEVIP] = "+strIpAddrSolenoid);
    uiFlagDevIP = 1;
  }
  if (jDoc.containsKey("SWITCHON")) 
  {
    strSolenoidOn = String(jDoc["SWITCHON"]);
    Serial.println("jDoc[SWITCHON] = "+strSolenoidOn);
    uiFlagDevOn = 1;
  }
  if (jDoc.containsKey("SWITCHOFF")) 
  {
    strSolenoidOff = String(jDoc["SWITCHOFF"]);
    Serial.println("jDoc[SWITCHOFF] = "+strSolenoidOff);
    uiFlagDevOff = 1;
  }
  if ((jDoc.containsKey("ERROR")) )
  {
    strError = String(jDoc["ERROR"]);
    Serial.println("jDoc[ERROR] = "+strError);
    uiFlagError = 1;
  }
  if(uiFlagDevName == 0)
  {
    dsplyErrorInfo("Error","kein Json-DevName!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)- Json-DevName missing!");
  }
  if(uiFlagUseCase == 0)
  {
    dsplyErrorInfo("Error","kein Json-UseCase!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)- Json-DevUseCase missing!");
  }
  if(uiFlagHTTP == 0)
  {
    dsplyErrorInfo("Error","kein Json-HTTP!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)-  Json-HTTPstart missing!");
  }
  if(uiFlagDevIP == 0)
  {
    dsplyErrorInfo("Error","kein Json-IP!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)-  Json-DevIP missing!");
  }
  if(uiFlagDevOn == 0)
  {
    dsplyErrorInfo("Error","kein Json-SwitchOn!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)-  Json-SwitchOn missing!");
  }
  if(uiFlagDevOff == 0)
  {
    dsplyErrorInfo("Error","kein Json-SwitchOff!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)-  Json-SwitchOff missing!");
  }
  if(uiFlagError == 0)
  {
    dsplyErrorInfo("Error","kein Json-Error!",5,1,0);
    Serial.println("Error: jsExtractInitData(JsonDocument jDoc)-  Json-Error missing!");
  }
  if((uiFlagDevName == 0) or (uiFlagUseCase==0) or (uiFlagHTTP==0) or (uiFlagDevIP==0) or (uiFlagDevOn==0) or (uiFlagDevOff==0) or (uiFlagError == 0))
  {
    return -1;
  }
  else
  {
    return 0;
  }
}

/*
int jsExtractSBUpdateData(JsonDocument jDoc)
{
  sTimeStart = jsExtractDate(jDoc);
  sTimeRunning = sTimeStart;
  ulTimeStart = rtc.getLocalEpoch();

 if((String(jDoc["DEVACCESS"])=="Yes") and (eState == working) and (String(jDoc["ERROR"])=="")) // !!!
  { // already checked DEVACCESS = YES, ERROR = "", STATE = WORKING
    SolenoidON();
    if(eSolState == SolenoidOn)
    {
      uiFlagRunTimer = 1;
    }
    strCustName = String(jDoc["CUSTOMERNAME"]);
    return 1;
  }
  else
  {
    return -1;
  }
  return 0;
}
*/

int jsExtractGateKeeperData(JsonDocument jDoc)
/****************************************************************************************************
 * void jsExtractGateKeeperData(JsonDocument jDoc) - when the RFID reader is working as GateKeeper
 * The data from the server will be extracted and displayed on the tft display
 * When unexpected data occur an error message on the tft display will represented.
****************************************************************************************************/
{
 
  uint8_t uiFlagCustName   = 0;
  uint8_t uiFlagStartStop  = 0;
  uint8_t uiFlagIcon       = 0;
  if (jDoc.containsKey("CUSTOMERNAME"))
  {
    strCustName    = String(jDoc["CUSTOMERNAME"]);
    Serial.println("jDoc[CUSTOMERNAME] = "+strCustName);
    uiFlagCustName = 1;
  }
  if(jDoc.containsKey("CUSTOMERSTARTSTOP"))
  {
    strStartEnd = String(jDoc["CUSTOMERSTARTSTOP"]);
    Serial.println("jDoc[CUSTOMERSTARTSTOP] = "+strStartEnd);
    uiFlagStartStop = 1;
  }
  if(jDoc.containsKey("ICON"))
  {
    strIcon = String(jDoc["ICON"]);
    Serial.println("jDoc[ICON] = "+strIcon);
    uiFlagIcon = 1;
  }
  if(uiFlagCustName == 0)
  {
    dsplyErrorInfo("Error","keine Json-CustName!",5,1,0);
    Serial.println("jsExtractGateKeeperData(JsonDocument jDoc)- Json-CustName missing!");
  }
  if(uiFlagStartStop == 0)
  {
    dsplyErrorInfo("Error","keine Json-StartStop!",5,1,0);
    Serial.println("jsExtractGateKeeperData(JsonDocument jDoc)- Json-StartStop missing!");
  }
   if(uiFlagIcon == 0)
  {
    dsplyErrorInfo("Error","keine Json-FlagIcon!",5,1,0);
    Serial.println("jsExtractGateKeeperData(JsonDocument jDoc)- Json-FlagIcon missing!");
  } 
  if ((uiFlagCustName == 0) or (uiFlagStartStop == 0) or (uiFlagIcon == 0)) 
  {
    return -1;
  }
  else
  {
    Serial.println("Error: Check what is here to do?");
    strHeader = strCustName;
    strMsg    = strStartEnd;
    iIconNo   = getIconNo(strIcon);
    return 0;
  }
}

JsonDocument getJSONInitData()
/****************************************************************************************************
 * JsonDocument getJSONInitData()- to request the necessary initialization data from the server
 * the data will be summarized and represented in a json document, for the identification 
 * the MAC address of the ESP32 processor will be used.
****************************************************************************************************/
{
  JsonDocument jDoc;
  jDoc["MACADDR"] = WiFi.macAddress(); 
  jDoc["STATE"]   = "INIT";
  jDoc["DEVUSECASE"] = "START";
  //jDoc["RFID"]    = "";
  // answer = "[\n {\n  \"macaddr\" : \""+WiFi.macAddress()+"\", \n  \"CMD\" : \"INIT\"\n  } \n]";
  serializeJson(jDoc, Serial);
  Serial.println(" <- Init String");
  return jDoc;
}

JsonDocument getJSONUserData(String strRfidTag)
/****************************************************************************************************
 * JsonDocument getJSONUserData(String strRfidTag) - When the RFID reader is working as switchbox 
 * after the initialization, a visitor arrives and places the RFID tag on the RFID reader
 * it is to check if the user is allowed to use the machine, the decisin will be delivered 
 * in the answer from the server.
 * Here the json document will be prepared
****************************************************************************************************/
{
  JsonDocument jDoc;
  String strTime = "";
  jDoc["MACADDR"]      = WiFi.macAddress();
  
  jDoc["RFID"]         =  strRfidTag;
  jDoc["DEVUSECASE"]   = getUseCase(); // +'\"';
  jDoc["ICON"]         = "";
  jDoc["ERROR"]        = "";
  jDoc["CUSTOMERNAME"] = "";
  strTime = rtc.getTime();
  jDoc["CUSTOMERSTARTSTOP"] = strTime;  // Startzeit, wenn State = IDLE und Endzeit, wenn State = Working beim Senden der Botschaft an den Server
                                        // Beim Empfang der zugeh�rigen Botschaft vom Server steht in diesem Eintrag die Summe der bisher aufgelaufenen Einheiten
  jDoc["STATE"]   = getState();
  if (eState == working)
  {
    char cATmpSec[6];
    itoa(liUnitMinutes,cATmpSec,10);
    strWorkUnitMin   = String(cATmpSec);
    itoa(uiUnitSeconds,cATmpSec,10);
    strWorkUnitSec   = String(cATmpSec);;

    strWorkEndTime = strTime;
  }
  else if (eState == idle)
  {
    strWorkStartTime = strTime;
  }
  /*
  if(eSolState == SolenoidOff)
  { // state is off -> request state on

    // jDoc["REQUEST"] = "ON";                // wurde für eine einheitliche Struktur des JSON-Doks entfernt => die Entscheidung erfolgt jetzt über den State-Eintrag
    // jDoc["STATE"]   = "IDLE";
    jDoc["UNITS"]   = 0;                        
  }
  else 
  { // state is on -> request state off
    // jDoc["REQUEST"] = "OFF";    
    // jDoc["STATE"]   = "WORKING";   
    jDoc["UNITS"]   = fSolUnitsMin;           // aufgelaufene Einheiten in Minuten und Sekunden, beim Abmelden bzw. Ausschalten, des aktuellen Intervalls
  }
  */
  Serial.println("MISSING CODE:  Request und Units anpassen!");
  Serial.println("State: "+getState()+"  jDoc[CUSTOMERSTARTSTOP]: "+ strTime);
  // answer = "[\n {\n  \"macaddr\" : \""+WiFi.macAddress()+"\", \n  \"ID\" : \""+strRfidTag+"\"\n  } \n]";
  //serializeJson(jDoc, Serial);
  return jDoc;
}

JsonDocument getJDocGKData(String strRfidTag)
/***************************************
*
* Create the JsonDocument for the GateKeeper functionality
*
***************************************/
{
  JsonDocument jDoc;
  jDoc["MACADDR"] = WiFi.macAddress();
  jDoc["STATE"]   = "IDLE";
  jDoc["DEVUSECASE"] = getUseCase();
  jDoc["RFID"]    = strRfidTag;
  jDoc["ICON"]    = "";  // NotFound/NoAccess
  jDoc["ERROR"]   = "";  
  jDoc["CUSTOMERNAME"]      = "";
  jDoc["CUSTOMERSTARTSTOP"] = "";
  return jDoc;
}

JsonDocument getJDocCounter(String strRfidTag)
/***************************************
*
* Create the JsonDocument for the Counter functionality
*
***************************************/
{
  JsonDocument jDoc;
  jDoc["MACADDR"]  = WiFi.macAddress();
  jDoc["STATE"]    = "IDLE";
  jDoc["DEVUSECASE"]  = getUseCase();
  jDoc["RFID"]     =  strRfidTag;  
  jDoc["TERMINAL"] =  strTerminal;
  jDoc["ICON"]     = "";
  jDoc["ERROR"]    = "";
  return jDoc;
}