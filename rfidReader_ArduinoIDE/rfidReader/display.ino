
const uint cDspZero                 =   0;
const uint cDspXMax                 = 319;
const uint cDspYMax                 = 239;
const uint cDspBorderWidthSmall     =  10;
const uint cDspBorderSmallXMin      = cDspZero + cDspBorderWidthSmall;
const uint cDspBorderSmallYMin      = cDspZero + cDspBorderWidthSmall;
const uint cDspBorderSmallXMax      = cDspXMax - cDspBorderWidthSmall;
const uint cDspBorderSmallYMax      = cDspYMax - cDspBorderWidthSmall;
const uint cDspBorderSmallWidth     = cDspXMax - (2 * cDspBorderWidthSmall);
const uint cDspBorderSmallHeight    = cDspYMax - (2 * cDspBorderWidthSmall);

//const uint cDspGateSalutationX      =  25;
const uint cDspGateSalutationY      =  55;
//const uint cDspGateNameX            =  25;
const uint cDspGateNameY            =  80;
//const uint cDspGateStartStopX       =  25;
const uint cDspGateStartStopY       = 115;
//const uint cDspGateTimeX            =  25;
const uint cDspGateTimeY            = 145;
//const uint cDspGateUnitsX           =  25;
const uint cDspGateUnitsY           = 180;

const uint cDspErrInfoHeadX         =  85;
const uint cDspErrInfoHeadY         =  65;
const uint cDspErrInfoTxtX          =  15;
const uint cDspErrInfoTxtY          =  75;

const uint cDspSwitchHeadX          =  15;
const uint cDspSwitchHeadY          =  15;

const uint cDspSwitchRfidX          =  15;
const uint cDspSwitchRfidY          =  40;
const uint cDspSwitchRfidValX       =  80;
const uint cDspSwitchRfidValY       =  40;
const uint cDspSwitchRfidValLen     = 150;
const uint cDspSwitchRfidValHeight  =  20;

const uint cDspSwitchNameX          =  15;
const uint cDspSwitchNameY          =  60;
const uint cDspSwitchNameValX       = 120; 
const uint cDspSwitchNameValY       =  60;
const uint cDspSwitchNameValLen     = 180;
const uint cDspSwitchNameValHeight  =  20;

      const uint cDspSwitchTimeX          = 200;
      const uint cDspSwitchTimeY          =  40;
      const uint cDspSwitchTimeValX       = 280;
      const uint cDspSwitchTimeValY       =  40;
      const uint cDspSwitchTimeValLen     = 180;
      const uint cDspSwitchTimeValHeight  =  20;

      const uint cDspSwitchUnitX          = 200;
      const uint cDspSwitchUnitY          =  80;
      const uint cDspSwitchUnitValX       = 220;
      const uint cDspSwitchUnitValY       =  80;
      const uint cDspSwitchUnitValLen     =  90;
      const uint cDspSwitchUnitValHeight  =  20;

const uint cDspSwitchStartX         =  15;
const uint cDspSwitchStartY         = 180;
const uint cDspSwitchStartValX      = 100;
const uint cDspSwitchStartValY      =  80;
const uint cDspSwitchStartValLen    = 100;
const uint cDspSwitchStartValHeight =  20;

const uint cDspSwitchRunUnitX       = 150;
const uint cDspSwitchRunUnitY       = 180; 

const uint cDspSwitchStateX         =  15;
const uint cDspSwitchStateY         = 215;
const uint cDspSwitchStateValX      = 120;
const uint cDspSwitchStateValY      = 215;
const uint cDspSwitchStateValLen    = 100;
const uint cDspSwitchStateValHeight =  20;

int initDisplay()
/****************************************************************************************************
 * int initDisplay() - Initialization of the display
 * the color scheme will be inverted that the color settings fit
 * and no wrong colors will be displayed, also the the orientation will be selected
 ****************************************************************************************************/
{
  dsplyReset();
  // No one of the listed function delivers a return value
  // https://github.com/adafruit/Adafruit_ILI9341/blob/master/Adafruit_ILI9341.h
  tft.begin();
  tft.invertDisplay(true);
  tft.fillScreen(ILI9341_BLACK);
  // origin = left,top landscape (USB left upper)

  // the parameter RFIDREADERRIGHT is defined in the mainfile of the project
#ifdef RFIDREADERRIGHT
  tft.setRotation(1); 
#else
  tft.setRotation(3); 
#endif
  return 0;
}


void dsplyReset()
/****************************************************************************************************
 * void dsplyReset - During the prototype development it was seen, that for some Display Units
 * an extra reset in front of the initalization function a reset is necessary 
 ****************************************************************************************************/
{ // Reset of the touch unit
  pinMode(TFT_RST, OUTPUT);
  digitalWrite(TFT_RST, HIGH);
  delay(Touch_RST_Duration_ms);
  digitalWrite(TFT_RST, LOW);
  delay(Touch_RST_Duration_ms);
  digitalWrite(TFT_RST, HIGH);
  delay(Touch_RST_Duration_ms);
}

void dsplyUpdateHeadline()
/****************************************************************************************************
 * void dsplyUpdateHeadline() - During the the initialization phase of the rfid-reader
 * the device receives the settings of the database with the associated device name 
 ****************************************************************************************************/
{
  tft.fillRect(10,10,250,16,ILI9341_BLACK);
  tft.setCursor(10, 10);
  tft.setTextColor(ILI9341_WHITE);
  tft.setTextSize(2);
  tft.println(strDevName);
}

void dsplyTime()
{
    int iXPosStart = 0;
    sQdrupel result;
    String strMessage = String(rtc.getTime());
    tft.setTextSize(1);
    
    //iXPosStart = iCenterTxt(iXPosStart, 0, strMsg);
    result = getWidthHeight(0, 220, strMessage);
    tft.fillRect(cDspXMax-result.w,cDspYMax-result.h,result.w,result.h,ILI9341_BLACK); 
    tft.setCursor(cDspXMax-result.w,cDspYMax-result.h);
    tft.println(strMessage); 
    tft.setTextSize(2);
}

void dsplyWifiState()
{
  if (WiFi.status() != WL_CONNECTED)
  {
    dsplyWifiKO();
  }
  else
  {
    dsplyWifiOK();
  }
}

void dsplyWifiOK()
{
  ImageReturnCode stat;
  stat = reader.drawBMP("/wlgrnsm.bmp", tft, 279, 0);
  if(stat == IMAGE_ERR_FILE_NOT_FOUND)
  {
    int iXPosStart = 0;
    sQdrupel result;
    String strMessage = "Wifi OK";
    tft.setTextSize(1);
    Serial.println("dsplyWifiOK() - Start");
    result = getWidthHeight(iXPosStart, 200, strMessage);
    iXPosStart = iCenterTxt(iXPosStart, 200, strMessage);
    tft.fillRect(cDspXMax-result.w,0,result.w,result.h,ILI9341_BLACK); 
    tft.setCursor(cDspXMax-result.w,0);
    tft.println(strMessage); 
    tft.setTextSize(2);
    Serial.println("dsplyWifiOK() - End");
  }
}

void dsplyWifiKO()
{
  ImageReturnCode stat;
  stat = reader.drawBMP("/wlredsm.bmp", tft, 279, 0);
  if(stat == IMAGE_ERR_FILE_NOT_FOUND)
  {
    int iXPosStart = 0;
    sQdrupel result;
    String strMessage = "Wifi KO";
    tft.setTextSize(1);
    Serial.println("dsplyWifiKO() - Start");
    result = getWidthHeight(iXPosStart, 200, strMessage);
    iXPosStart = iCenterTxt(iXPosStart, 200, strMessage);
    tft.fillRect(cDspXMax-result.w,0,result.w,result.h,ILI9341_BLACK);
    tft.setCursor(cDspXMax-result.w,0);
    tft.println(strMessage); 
    tft.setTextSize(2);
    Serial.println("dsplyWifiKO() - End");
  }  
}

void dsplyWifiConnect(uint8_t uiStep, float fProgress)
{ 
  ImageReturnCode stat; 
  String strMessage = "Connect to Wifi ...";
  int iXPosStart = 0;
  if(uiStep == 0)
  {
    // tft.fillRect(10,10,250,16,ILI9341_BLACK); 
    stat = reader.drawBMP("/wlblugr.bmp", tft, 90, 20);
    
    tft.drawRect(50,160,220,30,ILI9341_BLUE);  //320-50 = 270  50->270  => 270-50 
    tft.setTextSize(2);
    iXPosStart = iCenterTxt(iXPosStart, 200, strMessage);

    tft.setCursor(iXPosStart,200);
    tft.println(strMessage); 
    tft.setTextColor(ILI9341_WHITE);    
  } 
  else
  {
    tft.fillRect(50,160,int(220*fProgress),30,ILI9341_BLUE);
  }
}

bool  dsplyErrorInfo(String strHeader,String strErrMessage, uint8_t uiLevel, uint8_t uiTouch, uint8_t uiIconNo)
/****************************************************************************************************
 * void dsplyErrorInfo(String strErrMessage) - if an error occures it will be displayed on the tft
 * the text is mentioned in the parameter.
 ****************************************************************************************************/
{
  ImageReturnCode stat;
  bool bTouchEvent = false;
  int iXPosStart = 0;
  dspClear();
  if(uiLevel>=5)
  {
    tft.fillRect( cDspZero, cDspZero,cDspXMax,cDspYMax,ILI9341_RED);
    tft.setTextColor(ILI9341_RED);
  }
  else
  {
    tft.fillRect( cDspZero, cDspZero,cDspXMax,cDspYMax,green); // green from logo, bitwidth adapted
    tft.setTextColor(brightgreen);     // light green from logo, bitwidth adapted also check ILI9341_GREENYELLOW 
  } 
  tft.fillRect(cDspBorderSmallXMin,cDspBorderSmallYMin,cDspBorderSmallWidth,cDspBorderSmallHeight,ILI9341_BLACK);
  tft.setTextSize(2);
  iXPosStart = iCenterTxt(iXPosStart, 20, strHeader);
  tft.setCursor(iXPosStart, 20);
  tft.println(strHeader); 
  iXPosStart = 0;
  if(uiIconNo == 1)
  {
    stat = reader.drawBMP("/wlredgr.bmp", tft, 90, 40);
    Serial.println("Display red Wifi Bmp");
  }
  else if (uiIconNo == 2)
  {
    // Bildquelle:
    // <a href="https://www.flaticon.com/de/kostenlose-icons/defekt" title="defekt Icons">Defekt Icons erstellt von syafii5758 - Flaticon</a>
    stat = reader.drawBMP("/noserver.bmp", tft, 90, 40); 
    Serial.println("Display no server Bmp");
  }
  else if (uiIconNo == 3)
  {
    stat = reader.drawBMP("/wlgrngr.bmp", tft, 90, 40);
    Serial.println("Display green Wifi Bmp");
  }  
  else if (uiIconNo == 4)
  { // get configuration data from server
    // image source 
    // <a href="https://www.flaticon.com/free-icons/configuration" title="configuration icons">Configuration icons created by RaftelDesign - Flaticon</a>
    stat = reader.drawBMP("/getconf.bmp", tft, 90, 40);
    Serial.println("Display getconf Bmp");
    dsplyWifiState();
    Serial.println("Display getconf nach Wifi Status");
  }
  else if (uiIconNo == 5)
  { // get userdata
    // image source
    // <a href="https://www.flaticon.com/free-icons/administration" title="administration icons">Administration icons created by pojok d - Flaticon</a>
    stat = reader.drawBMP("/getusrdt.bmp", tft, 90, 40);
    Serial.println("Display getusrdt (get user data) Bmp");
    dsplyWifiState();
  }
  else if (uiIconNo == 6)
  { // user has no access to the tool handled by this switchbox 
    // image source 
    // <a href="https://www.flaticon.com/free-icons/no-entry" title="no entry icons">No entry icons created by Andyarteicon - Flaticon</a>
    stat = reader.drawBMP("/noaccess.bmp", tft, 90, 40);
    Serial.println("Display notreg (registered, but no access) Bmp");
    dsplyWifiState();
  }
  else if (uiIconNo == 7)
  { //  used id is not registered, possible in use case "Switchbox" and usecase "GateKeeper"
    // image source 
    // <a href="https://www.flaticon.com/free-icons/no-entry" title="no entry icons">No entry icons created by amonrat rungreangfangsai - Flaticon</a>
    stat = reader.drawBMP("/noreg.bmp", tft, 90, 40);
    Serial.println("Display noaccess (not registered) Bmp");
    dsplyWifiState();
  }
  else if(uiIconNo == 8)
  {  // <a href="https://www.flaticon.com/free-icons/foursquare-check-in" title="foursquare check in icons">Foursquare check in icons created by hqrloveq - Flaticon</a>
    stat = reader.drawBMP("/check.bmp", tft, 90, 40);
    if(stat == IMAGE_ERR_FILE_NOT_FOUND)
    {
      int iXPosStart = 0;
      sQdrupel result;
      String strMessage = "OK";
      tft.setTextSize(3);
      result = getWidthHeight(iXPosStart, 120, strMessage);
      iXPosStart = iCenterTxt(iXPosStart, 120, strMessage);
      tft.fillRect(cDspXMax-result.w,120,result.w,result.h,ILI9341_BLACK);
      tft.setCursor(cDspXMax-result.w,120);
      tft.println(strMessage); 
      tft.setTextSize(2);
    }      
    Serial.println("Display check Bmp");
  }
  else if (uiIconNo == 9)
  { // <a href=httpswww.flaticon.comfree-iconshello title=hello iconsHello icons created by Freepik - Flaticon</a>
    //stat = reader.drawBMP("/hello.bmp", tft, 90, 40);
    stat = reader.drawBMP("/hellosm.bmp", tft, 120, 40);
    if(stat == IMAGE_ERR_FILE_NOT_FOUND)
    {
      int iXPosStart = 0;
      sQdrupel result;
      String strMessage = "Hello";
      tft.setTextSize(3);
      result = getWidthHeight(iXPosStart, 80, strMessage);
      iXPosStart = iCenterTxt(iXPosStart, 80, strMessage);
      tft.fillRect(iXPosStart,80,result.w,result.h,ILI9341_BLACK);
      tft.setCursor(iXPosStart,80); // cDspXMax-result.w
      tft.println(strMessage); 
      tft.setTextSize(2);
    }      
    Serial.println("Display hello Bmp");
  }
  else if (uiIconNo == 10)
  { // <a href="https://www.flaticon.com/free-icons/goodbye" title="Goodbye icons">Goodbye icons created by Freepik - Flaticon</a>
    //stat = reader.drawBMP("/bye.bmp", tft, 90, 40);
    stat = reader.drawBMP("/byesm.bmp", tft, 120, 40);
    if(stat == IMAGE_ERR_FILE_NOT_FOUND)
    {
      int iXPosStart = 0;
      sQdrupel result;
      String strMessage = "Bye";
      tft.setTextSize(3);
      result = getWidthHeight(iXPosStart, 80, strMessage);
      iXPosStart = iCenterTxt(iXPosStart, 80, strMessage);
      tft.fillRect(cDspXMax-result.w,80,result.w,result.h,ILI9341_BLACK);
      tft.setCursor(cDspXMax-result.w,80);
      tft.println(strMessage); 
      tft.setTextSize(2);
    }      
    Serial.println("Display bye Bmp");
  }
  else if (uiIconNo == 11)
  { //  <a href="https://www.flaticon.com/free-icons/error" title="error icons">Error icons created by Flat Icons - Flaticon</a>
    stat = reader.drawBMP("/error.bmp", tft, 90, 40);
    if(stat == IMAGE_ERR_FILE_NOT_FOUND)
    {
      int iXPosStart = 0;
      sQdrupel result;
      String strMessage = "ERROR";
      tft.setTextSize(3);
      result = getWidthHeight(iXPosStart, 120, strMessage);
      iXPosStart = iCenterTxt(iXPosStart, 120, strMessage);
      tft.fillRect(cDspXMax-result.w,120,result.w,result.h,ILI9341_BLACK);
      tft.setCursor(cDspXMax-result.w,120);
      tft.println(strMessage); 
      tft.setTextSize(2);
    }      
    Serial.println("Display error Bmp");
  }
  else if (uiIconNo == 99)
  { 
    int iXPosStart = 0;
    String StringTxt = WiFi.macAddress();
    //tft.fillRect(0,100,320,60,ILI9341_BLACK);
    iXPosStart = iCenterTxt(iXPosStart, 120, StringTxt);
    tft.setCursor(iXPosStart,120);
    tft.println(StringTxt);     
    Serial.println("display MAC ADR not found: "+WiFi.macAddress());
    dsplyWifiState();
  }
  else
  {
    Serial.println("Display uiIconNo == 0");
  }
  
  //tft.fillRect(90,90,200,15,ILI9341_BLACK);
  //tft.setTextColor(ILI9341_RED);
//  if((uiIconNo == 9 /* Hi */) or (uiIconNo == 10 /* Bye */))
//  {  // messgae line shall not displayed in the middle, when "Bye" or "Hi"message shall be displayed
//    tft.setCursor(50,185);
//    tft.println(strErrMessage); 
//    //tft.setCursor(220,185);
//    //tft.println(strUnits); 
//    tft.setTextColor(ILI9341_WHITE);
//    tft.setTextSize(2);
//  }
//  else
  if((uiIconNo == 9) or (uiIconNo == 10))
  {  // 
    tft.setTextSize(2);
    iXPosStart = iCenterTxt(iXPosStart, 160, strErrMessage);

    tft.setCursor(iXPosStart,160);
    tft.println(strErrMessage); 
    tft.setTextColor(ILI9341_WHITE);
    tft.setTextSize(2);
  }
  else
  {
    iXPosStart = iCenterTxt(iXPosStart, 185, strErrMessage);

    tft.setCursor(iXPosStart,185);
    tft.println(strErrMessage); 
    tft.setTextColor(ILI9341_WHITE);
    tft.setTextSize(2);
    if(uiTouch == 1)
    {
      bTouchEvent = waitForTouch();
      dspClear();
    }
  }
  return bTouchEvent;
}

void dsplyIdleText(String strParam)
{
  int iXPosStart = 0;
  uint8_t uiTxtFlag=0;
  String StringTxt = StringUseCaseName[uint8_t(eUC)];
  tft.fillRect(0,100,320,60,ILI9341_BLACK);
  if(strParam == "")
  {
    strParam = StringTxt;
    uiTxtFlag = 1;
  }
  iXPosStart = iCenterTxt(iXPosStart, 100, strParam);
  tft.setCursor(iXPosStart,120);
  tft.println(strParam); 
  if(uiTxtFlag == 1)
  {
    iXPosStart = iCenterTxt(iXPosStart, 140, strDevName);
    tft.setCursor(iXPosStart,140);
    tft.println(strDevName);
  }
}

void dsplyIdle()
{
  ImageReturnCode stat;
  dsplyTime();
  dsplyWifiState();
  if((rtc.getSecond() < 15) or ((rtc.getSecond()>30) and rtc.getSecond()<45))
  {
    if(uiUpdatedRFID != 0)
    {
      uiUpdatedUC = 1;
      uiUpdatedRFID = 0;
      // <a href="https://www.flaticon.com/de/kostenlose-icons/rfid" title="rfid Icons">Rfid Icons erstellt von Freepik - Flaticon</a>
      // dspClear();
      //if(uiSDCardFlag == 1)
      //{
      stat = reader.drawBMP("/rfid.bmp", tft, 90, 40);
      if(stat == IMAGE_ERR_FILE_NOT_FOUND)
      {  
        dsplyIdleText("Bereit fuer RFID");
      }
      else
      {
        Serial.println("Display RFID BMP");  
      }
    }
  }
  else
  {
    if(uiUpdatedUC != 0)
    {
      uiUpdatedUC = 0;
      uiUpdatedRFID = 1;
      //dspClear();
      if(eUC == SwitchBox)
      {
        // <a href="https://www.flaticon.com/free-icons/electric-motor" title="electric motor icons">Electric motor icons created by Freepik - Flaticon</a>
        if(uiSDCardFlag == 1)
        {
          stat = reader.drawBMP("/motor.bmp", tft, 90, 40);
          Serial.println("Display Motor BMP");  
        }
        //if(stat == IMAGE_ERR_FILE_NOT_FOUND)
        else
        { 
          dsplyIdleText(strDevName); 
        }
      }
      else if( eUC == GateKeeper)
      {
        // <a href="https://www.flaticon.com/free-icons/gate" title="gate icons">Gate icons created by Freepik - Flaticon</a>
        if(uiSDCardFlag == 1)
        {
          stat = reader.drawBMP("/gate.bmp", tft, 90, 40);
          Serial.println("Display Gate BMP");  
        }
        else
        {
          dsplyIdleText("Tor-Kontrolle");
          Serial.println("GateKeeper");  
        }
      }
      else if( eUC == Counter)
      {
        // <a href="https://www.flaticon.com/free-icons/cash-register" title="cash register icons">Cash register icons created by Earthz Stocker - Flaticon</a>
        if(uiSDCardFlag == 1)
        { 
          stat = reader.drawBMP("/cash.bmp", tft, 90, 40);
          Serial.println("Display Cash BMP");  
        }
        else
        {
          dsplyIdleText("");
        }
      }
    }
  }
}


void dsplyMask() 
/****************************************************************************************************
 * void dsplyMask() - in dependency of the use case the basic mask will be displayed
 ****************************************************************************************************/
  {
  dspClear();
  if (cDevUseCase == 'S')
  {
    dsplySwitchBox();
  }
  else if(cDevUseCase == 'C')
  {
    dsplyCounter();
  }
  else if(cDevUseCase == 'G')
  {  
    dsplyGateKeeper();
    updateGateKeeper();
  }
  else
  {
    dsplyErrorInfo("Error","Unkown UseCase",5,0,0);
  }
  if(bWifiLostFlag)
  { // Wifi Connnection lost ->
    // display red small wifi icon in the upper right corner
    dsplyWifiKO();
  }
  else
  { // Wifi Connnection ok ->
    // display green small wifi icon in the upper right corner
    dsplyWifiOK();
  }  
 }

void dsplyCounter()
{
  int iTmpXpos = 0;
  tft.fillScreen(ILI9341_BLACK);
  iTmpXpos = iCenterTxt(0,120,strUIdHex);
  
  tft.setCursor(iTmpXpos,120); 
  tft.println(strUIdHex);
  waitForTouch();
}

void dsplySwitchBox()
{
  tft.fillScreen(ILI9341_BLACK);
  tft.setCursor(cDspSwitchHeadX,cDspSwitchHeadY); 
  tft.println("Geraetname");

  tft.setCursor(cDspSwitchRfidX,cDspSwitchRfidY); 
  tft.println("Tag-ID :");

  tft.setCursor(cDspSwitchNameX,cDspSwitchNameY); 
  tft.println("Name :");

  tft.setCursor(cDspSwitchTimeX,cDspSwitchTimeY); 
  tft.println("Zeit :");

  tft.setCursor(cDspSwitchStartX,cDspSwitchStartY); 
  tft.println("Startzeit :");

  tft.setCursor(cDspSwitchUnitX,cDspSwitchUnitY); 
  tft.println("Einheit :");

  tft.setCursor(cDspSwitchRunUnitX,cDspSwitchRunUnitY); 
  tft.println("lfd. Einheit :");

  tft.setCursor(cDspSwitchStateX,cDspSwitchStateY); 
  tft.println("Gerätestatus :");

  waitForTouch();
}

void localupdtSwitchBox()
{
  Serial.println("lokal Update");
  // Wenn die Maschine eingeschaltet ist
  // Updeate der Zeit
  // Update der Einheiten
}

void dsplyGateKeeper()
{
  // draw border
    tft.fillRect( cDspZero, cDspZero,cDspXMax,cDspYMax,green); // green from logo, bitwidth adapted
    tft.setTextColor(brightgreen);     // light green from logo, bitwidth adapted
    //tft.println(strHeader);
    tft.fillRect(cDspBorderSmallXMin,cDspBorderSmallYMin,cDspBorderSmallWidth,cDspBorderSmallHeight,ILI9341_BLACK);
}

void updateGateKeeper()
{ // old parameter list String strSalutaion,String strName, String Time, String UnitNo
  int iTmpXPos = 0;
  tft.fillRect(cDspBorderSmallXMin,cDspBorderSmallYMin,cDspBorderSmallWidth,cDspBorderSmallHeight,ILI9341_BLACK);
  tft.setTextColor(brightgreen);  
  
  iTmpXPos = iCenterTxt(0,cDspGateNameY,strSalut);
  tft.setCursor(iTmpXPos,cDspGateSalutationY); 
  tft.println(strSalut);
  
  iTmpXPos = iCenterTxt(0,cDspGateNameY,strCustName);
  tft.setCursor(iTmpXPos,cDspGateNameY); 
  tft.println(strCustName);

  iTmpXPos = iCenterTxt(0,cDspGateStartStopY,strStartEnd);
  tft.setCursor(iTmpXPos,cDspGateStartStopY); 
  tft.println(strStartEnd);  

  iTmpXPos = iCenterTxt(0,cDspGateTimeY,strCustomerTime);
  tft.setCursor(iTmpXPos,cDspGateTimeY); 
  tft.println(strCustomerTime);  

  iTmpXPos = iCenterTxt(0,cDspGateUnitsY,strUnits);
  tft.setCursor(iTmpXPos,cDspGateUnitsY); 
  tft.println(strUnits);    

  tft.setTextColor(ILI9341_WHITE);
  waitForTouch();
}

void updateSwitchBox()
{
  Serial.println("Error: Code fehlt!");
}

void dspClear()
{
  tft.fillScreen(ILI9341_BLACK);
}

void dsplyUnitMinutes(int iWidthOfSeconds)
{
  int iXPosStart = 0;
  sQdrupel result;
  String strTmp = "";
  char cATmpSec[6];

  tft.setTextSize(2);
  itoa(liUnitMinutes,cATmpSec,10);
  strTmp = "bisher:"+String(cATmpSec);
  
  result = getWidthHeight(0, 220, strTmp); 
  tft.fillRect(cDspXMax-iWidthOfSeconds-result.w-10,180/*cDspYMax-10-result.h*/,result.w,result.h,ILI9341_BLACK); // 205 -> 180
  tft.setCursor(cDspXMax-iWidthOfSeconds-result.w-10,180/*cDspYMax-result.h*/); // 205 -> 180
  tft.println(strTmp); 
}

void dsplyUnitSecond()
{
  int iXPosStart = 0;
  sQdrupel result;
  String strTmp = "";
  char cATmpSec[2];
  bool bUpdateFlag = false;
  
  if(bUnitRun)
  {
    Serial.println("UnitCounter is running");
    bUpdateFlag = incrUnitCounter();
  }
  else
  {
    Serial.println("GateKeeper UnitCounter NOT running");
  }
  //iXPosStart = iCenterTxt(iXPosStart, 0, strMsg);
  
  itoa(uiUnitSeconds,cATmpSec,10);
  strTmp = String(cATmpSec);
  
  if(uiUnitSeconds <= 9)
  {
    strTmp = ":0"+strTmp;
  }
  else
  {
    strTmp = ":"+strTmp;
  }
  Serial.println("dsplyUnitSecond - SecUpdate "+strTmp);
  tft.setTextSize(2);
  result = getWidthHeight(0, 220, strTmp);
  tft.fillRect(cDspXMax-10-result.w,180/*cDspYMax-10-result.h*/,result.w,result.h,ILI9341_BLACK);  // 205 -> 180
  tft.setCursor(cDspXMax-10-result.w,180/*cDspYMax-10-result.h*/);  // 205 -> 180
  tft.println(strTmp); 
  if(bUpdateFlag or bUnitMinInitDisplay)
  { 
    bUpdateFlag         = false;
    bUnitMinInitDisplay = false;
    Serial.println("dsplyUnitSecond - in front of update from Minutes ");
    dsplyUnitMinutes(result.w);
  }
  tft.setTextSize(2);
}

void dsplyWifiTryCnt(uint8_t uiTry)
{ // Counter how many tries are neccessry to get the server connection
  int iXPosStart = 0;
  sQdrupel result;
  String strTry = String(uiTry);
  tft.setTextSize(1);
   
  //iXPosStart = iCenterTxt(iXPosStart, 0, strMsg);
  result = getWidthHeight(0, 220, strTry);
  tft.fillRect(0,cDspYMax-result.h,result.w,result.h,ILI9341_BLACK); 
  tft.setCursor(0,cDspYMax-result.h);
  tft.println(strTry); 
  tft.setTextSize(2);
}

void dsplySWVersion()
{
  tft.setTextColor(ILI9341_WHITE);
  tft.setCursor(20,225);
  tft.setTextSize(1);
  tft.print("SW Version: "); 
  tft.print(sw_version); 
  tft.setTextSize(2);
}

void dsplyPrepareTouchEvent()
{
  tft.setTextColor(ILI9341_WHITE);
  tft.setCursor(20,205);
  tft.setTextSize(2);
  tft.println("Weiter mit Touch"); 
  tft.setTextSize(2);
}