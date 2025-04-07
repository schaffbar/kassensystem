void initWifi(void)
/****************************************************************************************************
 * void initWifi(void) - Initalization of the Wifi functionality 
 ****************************************************************************************************/
{
  int i=0;
  WiFi.mode(WIFI_STA);
  WiFi.begin(WiFiSsid, WiFiPassWd);
  Serial.print("Connecting to WiFi ..");
  
  dsplyWifiConnect(0, 0);   // Init of the progressbar
  
  while ((WiFi.status() != WL_CONNECTED)&&(i < wifiNoTries))
  {
    Serial.print('.');
    dsplyWifiConnect(1, (float(i)/float(wifiNoTries)));  // update the progressbar
    delay(500);
    i++;
  }
  if(WiFi.status()!= WL_CONNECTED)
  { // no wifi connection -> represent error message
    dsplyErrorInfo("Error","No Wifi !",5,0,1);
    Serial.println("No Wifi Connection! -> Reset");   
    playError();
    delay(cWaitforSoftReset);
    softReset();
  }
  else
  { // wifi ok -> represent status message
    dsplyErrorInfo("Info","Wifi Connected!",0,1,3); // 3 - display green wifi bmp
    //dsplyWifiOK();
    Serial.println("Wifi Connected!");   
    Serial.println(WiFi.localIP());
    Serial.print("ESP32 MAC Address: ");
    Serial.println(WiFi.macAddress());
    //String strHost = "RFID_"+String(WiFi.macAddress());
    //char hostname[30];
    //strHost.replace(":","_");
    //strHost.toCharArray(hostname, strHost.length()) ;
    //WiFi.setHostname(hostname);
    //Serial.println("Hostname = "+strHost);
    bWifiInitFlag = true;
  }
}

void sendRequest(JsonDocument jDoc) 
/****************************************************************************************************
 * void sendRequest(JsonDocument jDoc) - When an exchange with the server or the database is necessary 
 * the data will be collected and represented in a json document 
 ****************************************************************************************************/
{
  int iLTime = 0;

  uint uiCnCnt = 0;
  uint uiCnnctCnt = 0;
  bool bCnFlag    = false;
  bool bCnnctFlag = false;
  bool bTimeOutServer = false;
  Serial.println("Start of sendRequest()");
    
  char cArJsonWifi[256];
  //Serial.println("Json-Len = "+String(sizeof(cArJsonWifi)));
  String strJsonWifi = String(cArJsonWifi);
  serializeJson(jDoc, Serial);
  
  while((!bCnFlag) and (uiCnCnt < wifiNoTries ))
  {
    
    if(wifiClient.connect(ip, port)) 
    {
      Serial.println("nach connect");
      bCnFlag = true;
      
      //while((!bCnnctFlag) and (uiCnnctCnt < wifiNoTries) and (!bTimeOutServer))
      //{
      Serial.println("nach connect");
      if ((wifiClient.connected()) and (!bTimeOutServer))
      {
        Serial.println("localClient connected");
        bCnnctFlag = true;
        serializeJson(jDoc, strJsonWifi); // cArJsonWifi);  // strJsonWifi - wifiClient
        //Serial.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        wifiClient.print(strJsonWifi); //Send Json String to Server
        
        Serial.println("msg sent - waiting for answer");
        iLTime = millis();
        while((!wifiClient.available()) and (iLTime + 100000 >= millis()))
        //while(!wifiClient.available()) 
        {
            Serial.print("*");
            
        }
        Serial.println("");
        if (wifiClient.available() > 0) 
        {
          String strResponse = wifiClient.readStringUntil('\n');
          Serial.println("answer received!");
          Serial.println(strResponse);
          analyseResponse(strResponse);
          // dsplyUpdateHeadline();
        }
        else        
        {
            bTimeOutServer = true;
        }
      }
    }
    else
    {

      delay(100);
      uiCnCnt = uiCnCnt + 1;
      dsplyWifiTryCnt(uiCnCnt);
    }
  }
  if(bTimeOutServer)
  {
    dsplyErrorInfo("Error","TimeOut from Server",5,0,0);
    eState = error;
    Serial.println("Error: TimeOut no response from Server !");
    playError();
  }
  if(!bCnFlag)
  {
    dsplyErrorInfo("Error","Server not available",5,0,2);
    Serial.println("Message NOT sent");
    playError();
    stop();
  }
 


}
