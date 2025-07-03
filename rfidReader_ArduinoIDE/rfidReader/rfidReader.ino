/*
 * -----------------------------------------------------------------------------------------
 * This program shall be used to handle the following three use cases
 * 1.) Counter                                                                           Implemented
 *     The assistant at the counter read the rfid number of the tag and place 
 *     it to a database table in combination with the customer number field
 *     which is also used for union members of the open workshop
 * 2.) SwitchBox
 *     The customer can switch the power of the requested machine on,
 *     when he has joined the introduction course of this machine.
 *     in a database this is mentioned, and part of the answer of the server.
 * 3.) GateKeeper                                                                        Implemented
 *     Set the start and end signal in the database table, when the customer or
 *     union member of the open workshop enter or leave the workshop area.
 * 
 * Hint:
 * -) The code is splitted to several files to have shorter files which provide 
 *    a better overview
 *
 * https://github.com/OSSLibraries/Arduino_MFRC522v2#pin-layout           -- RDIF-Modul
 *
 * https://github.com/espressif/arduino-esp32/tree/master                 -- HTTPClient Library
 * https://arduinojson.org/?utm_source=meta&utm_medium=library.properties -- JSON Library  
 */

//-----------------------------
// 
//  pin def of the used setup  
//  BREADBOARD - when you use the breadboard
//  PCB        - when you use the 1st version of the PCB
//
//  with the current pcb version 2024 the rfid reader can be placed on the left or on the right side
//  so the orientation of the TFT display is to set,
//  with the parameter RFIDREADERRIGHT the pins of the TFT display are on the right side 
//  and the rfid reader is on the right side of the TFT display

//#define BREADBOARD          // ESP32 Dev Module
#define PCB                 // ESP32 C3 on PCB
#define RFIDREADERRIGHT     // processor settings are to modify

// The used pins of this project are defined in the pinoutdef.h file
// the PCB version use a ESP32 C3 for te Arduino IDE the processor def is to set to ESP32C3 Dev Module
// breadboard is using the setting ESP32 Dev Module
// also the serial interface is to set 

#include <Arduino.h>

#include <WiFi.h>
#include <esp_wifi.h> // get the mac address
#include <HTTPClient.h>
#define wifiNoTries 50      // number of tries to get WLan connection

//-----------------------------
#include <MFRC522v2.h>
#include <MFRC522DriverSPI.h>
#include <MFRC522DriverPinSimple.h>
#include <MFRC522Debug.h>
//-----------------------------
#include <ArduinoJson.h>        // the json format shall be used to to cut out the
                                // value of a property from the set of transferred data
//-----------------------------
#include <ESP32Time.h>          // https://github.com/fbiego/ESP32Time
//----------------------------- 
#include <Adafruit_GFX.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_ILI9341.h>

#include <SdFat.h>                // SD card &amp; FAT filesystem library
#include <Adafruit_SPIFlash.h>    // SPI / QSPI flash library
#include <Adafruit_ImageReader.h> // Image-reading functions

#include "RAK14014_FT6336U.h"     // Click here to get the library: http://librarymanager/All#RAK14014_FT6336U

// #include "pitches.h"  def of tones
#include "pinoutdef.h"
#include "ver.h"

const char* WiFiSsid =  "WLAN-Name";
const char* WiFiPassWd =  "WLAN-PassWd";

const uint port = 5000;
const char* ip = "odoo"; 

bool  bWifiInitFlag = false;
bool  bWifiLostFlag = false;

HTTPClient httpClient;  // to send the control command to the shelly or similar device
WiFiClient wifiClient;  // for discussion with the server and database

uint8_t uiUpdatedUC = 0;
uint8_t uiUpdatedRFID = 1;

// http://192.168.8.151/relay/0?turn=off
String strDevName         = "SchaffBar-RFID-Controller";
char cDevUseCase          = ' '; // SwitchBox Ein/Ausschalten von Geräten - TimeCtrl Betreten/Verlassen des Werkstatt-Bereichs - Counter
String strHTTPstart       = "http://";
String strIpAddrSolenoid  = "192.168.8.151";
String strSolenoidOff     = "/relay/0?turn=off";
String strSolenoidOn      = "/relay/0?turn=on";
String strSolenoidToggle  = "/relay/0?turn=toggle";

String strSalut           = "Hi/Bye";
String strCustName        = "XXXXXXXXXXX";
String strStartEnd        = "StartStop";
String strCustomerTime    = "XX:XX";       // answer from server XX:XX for start and XX:XX - YY:YY for stop
String strIcon            = "";
String strUnits           = "ZZZZZZ";      // usecase SwitchBox
String strError           = "";
String strFlagOK          = "";            // OK - access to tool granted / KO - access of tool not allowed

// the following variables are used to display the good-bye message for the usecase "SwitchBox"
String strWorkID          = "";            // store the card id that nobody else can switch off the machine
String strWorkStartTime   = "";            // Start-Zeitstempel vom ESP32 bestimmt
String strWorkEndTime     = "";            // Stop-Zeitstemper vom ESP32 bestimmt 
String strWorkUnitMin     = "";
String strWorkUnitSec     = "";
bool bDsplySwitchBoxInit  = false;

String strHeader          = "";
String strMsg             = "";
int iIconNo               = 0;

String strTerminal        = "";             //  Terminal ID, where the RFID-Reader is connected 

//-------------------------------------------------------------
ESP32Time rtc(0);  // possible offset in seconds

int     iLastTime       = 0;
int     iCountSec       = 0;
int     iCountMin       = 0;
int     iCountHour      = 0;
int     iCountDay       = 0;
int     iUnitLengthMin  = 0;
int     iUnitLengthHour = 0;
int     iUnitLengthDay  = 0;
int     iUnitCountMin   = 0;
int     iUnitCountHour  = 0;
int     iUnitCountDay   = 0;


uint8_t uiFlagRunTimer  = 0;
typedef struct 
{
  uint    uiDateYear      = 0;
  uint8_t uiDateMonth     = 0;
  uint8_t uiDateDay       = 0;
  uint8_t uiDateHour      = 0;
  uint8_t uiDateMinute    = 0;
  uint8_t uiDateSecond    = 0;
  uint8_t uiFlagValid     = 0;
} timeproj_t;

timeproj_t sTimeRunning;
timeproj_t sTimeStart;

//----------------------------------------------------

float fSolUnitsMillis = 0.0;
float fSolUnitsMin    = 0.0;

uint8_t  uiUnitSeconds       = 0;     // seconds of the interval used for use case 
long int liUnitMinutes       = 0;     // minutes of the interval
long int liUnitCounterMil    = 0;     // millis counter to check the intervall
//uint8_t  uiGKLastSecond   = 0;     // update of seconds from the rtc clock library
//bool     bGKMinUpdateFlag = false; // update the minute value on the display
bool     bUnitRun            = false;
bool     bUnitMinInitDisplay = false;
int iOldXPos = 0;
int iOldYPos = 0;
String strOldTxt = "";

//----------------------------------------------------

String StringUseCaseName[5] = {"GetInit","Switchbox","GateKeeper","Counter","Unknown"};
typedef enum eDevUseCase_t 
{
  GetInit,       // request for init data
  SwitchBox,     // is switching the different tools on and off
  GateKeeper,    // start and stops the timer 
  Counter,       // support the hero at the counter
  UnKnown        // unkown use case
} eDevUseCase_t;

eDevUseCase_t eUC= GetInit;

typedef enum eDevState_t
{
  error,
  start,
  idle,
  working,
  end                   // temporary state, when a "good bye" message shall be displayed, after an intervall the rfid reader shall fall back to idle mode
} eDevState_t;

eDevState_t eState = start;

typedef enum eSolenoidState_t
{
  SolenoidOn,
  SolenoidOff
} eSolenoidState_t;

eSolenoidState_t eSolState = SolenoidOff;

typedef struct 
{ // shall be used for point or dimensions of a square for width and hight
  int x;  // x-position
  int y;  // y-position
  int w;  // width of the text box
  int h;  // height of the text box
} sQdrupel;

#define brightgreen 0xEFBA
#define green 0x8574

//----------------------------------------
MFRC522DriverPinSimple ss_pin(Pin_MFRC522_CS); // Create pin driver. See typical pin layout above.

SPIClass &spiClass = SPI; // Alternative SPI e.g. SPI2 or from library e.g. softwarespi.

const SPISettings spiSettings = SPISettings(SPI_CLOCK_DIV4, MSBFIRST, SPI_MODE0); // May have to be set if hardware is not fully compatible to Arduino specifications.

MFRC522DriverSPI driver{ss_pin, spiClass, spiSettings}; // Create SPI driver.

MFRC522 mfrc522{driver}; // Create MFRC522 instance.

//--------------------------
Adafruit_ILI9341 tft = Adafruit_ILI9341(TFT_CS, TFT_DC,TFT_RST);
//Adafruit_ILI9341 tft = Adafruit_ILI9341(spiClass, TFT_DC, TFT_CS, TFT_RST);
//--------------------------
FT6336U ft6336u;              // Touch object
static uint8_t iTouchIntStatus = false; // TP interrupt generation flag.
//--------------------------------------------------------------------------
// For SD card use, these two globals are declared:
SdFat SD; // SD card filesystem
Adafruit_ImageReader reader(SD); // Image-reader object, pass in SD filesys
uint8_t uiSDCardFlag = 0;

//---------------------------------------------------------------------------
String strUIdHex = "";
uint8_t uidLength = 0;
char chArCardID[30];
char chArCarIdLen[3];

const int cInitWaitTime          =  4000;  // How long the prog shall wait until the init of the touch unit is ready
const int cWaitForAnyTouch       =  5000;  // Waiting for Touch-Action or Timer expires
const int cGateKeeperDisplayTime =  3000;  // How long the salutaion and name shall be displayed on the tft
const int cWaitforSoftReset      = 15000;  // represent an error message then start again

//---------------------------
JsonDocument jsonSendDoc;   // String jsonSendData = "";
JsonDocument jsonRecDoc;    // String jsonReceiveData = "";

unsigned long ulTimeStart;  // Time stamp when the machine was enabled
unsigned long ulLocalEpochStart = 0;


bool bIntChange  = false;  // flag to start display 
bool bIntRunning = false;  // intervall is running
long int lIntStartAt = 0;  // millis(), when the intervall was started
const long clIntDuration = 1000 * 15;  // duration how long the intervall shall run

void setup() 
/****************************************************************************************************
 * void setup() -Standard Init code of the sketch, consist of several init function of the different HW components
 *  TFT display
 *  Touch Unit
 *  Timer Functionality
 *  Wifi Functions
 ****************************************************************************************************/
{
  Serial.begin(115200); // Initialize serial communications with the PC for debugging.
  //while (!Serial);      // Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4).
  Serial.println("Setup start ...");

  initDisplay();
  initTouch();  // ToDo: add return value

  Serial.println(F("Scan PICC to see UID, SAK, type, and data blocks..."));
  mfrc522.PCD_Init();   // Init MFRC522 board.
  delay(10); // for is requested for slow ICs 
  MFRC522Debug::PCD_DumpVersionToSerial(mfrc522, Serial);	// Show details of PCD - MFRC522 Card Reader details.
	
  eState = start;

  //////////////////////////////////////////////  SD-Card
  if(!SD.begin(SD_CS, SD_SCK_MHZ(25))) 
  { // ESP32 requires 25 MHz limit
    Serial.println(F("SD begin() failed"));
    uiSDCardFlag = 0;
    //for(;;); // Fatal error, do not continue
  }
  else
  {
    Serial.println(F("SD begin() ok :-)"));
    uiSDCardFlag = 1;
  }
  ImageReturnCode stat;
  stat = reader.drawBMP("/logo.bmp", tft, 0, 0);
  dsplySWVersion();
  delay(4000);
  tft.fillScreen(ILI9341_BLACK);
  //stat = reader.drawBMP("/schwein.bmp", tft, 0, 0); //  
  //delay(4000);
  //tft.fillScreen(ILI9341_BLACK);
  //////////////////////////////////////////////
  eUC = GetInit;
  initTime();
  initWifi();
  jsonSendDoc = getJSONInitData();
  //ulLocalEpochStart = rtc.getLocalEpoch();
  //unsigned long xxx = millis();
  //playOK();
  //Serial.println("timediff = "+String(rtc.getLocalEpoch()-ulLocalEpochStart));
  //Serial.println("timediff = "+String(millis()-xxx));
  //Serial.println("+++++++++++++++++++++++++++++++");
  dsplyErrorInfo("Info","Lade Config Data", 1, 0, 4);
  sendRequest(jsonSendDoc);
  if(eUC == SwitchBox)
  {
    // UseCase = SwitchBox -> Schalte das Relais aus
    SolenoidOFF();
  }
  bIntChange  = false;  // flag to start display 
  bIntRunning = false;  // time how long the message shall be displayed
  Serial.println("UseCase "+String(eUC));
  Serial.println("State   "+String(eState));
  dspClear();
  if((eUC != GetInit) and (eUC != UnKnown))
  {
    eState = idle;
  }
  
  Serial.println("End of SetUp()");
  Serial.flush();
}

void loop() 
/****************************************************************************************************
 * void loop() - Standard Loop Function of the Sketch
 * To get the ID of the RFID-Tag
 * or user activations of the Touch Unit
 * The user actions will be evaluated in the associated sub-function
 ****************************************************************************************************/
{
	//if ( !mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {
	//	return;
	//}
  Serial.println("Start of Loop() - "+rtc.getTime()+" - State "+int(eState)+ " - UC - "+int(eUC));
  //if((eState == idle) or ((eState == working)))
  //{
    if((WiFi.status() == WL_CONNECTED)&&(bWifiInitFlag))
    {
    if(mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial())
    {
      MFRC522Debug::PICC_DumpToSerial(mfrc522, Serial, &(mfrc522.uid));
      Serial.println("RFID-ByteSize "+String(mfrc522.uid.size));
      Serial.println("RFID-ID "+Uid2StrHex(mfrc522.uid));
      uidLength = mfrc522.uid.size;
      strUIdHex = Uid2StrHex(mfrc522.uid); 
      strUIdHex.toCharArray(chArCardID, 26); 
      itoa(uidLength, chArCarIdLen, 10);
      Serial.println("UID-Len = "+ String(chArCarIdLen) + " / RFID-Tag " + String(chArCardID));
      // clear previous output
      //tft.fillRect(100,50,200,15,ILI9341_BLACK);
      // present card data on display
      //tft.setCursor(100,50);
      //tft.println(chArCarIdLen); 
      //tft.setCursor(130,50);
      //tft.println(chArCardID); 
      Serial.println("in loop() - "+getUseCase());
      if (eUC == SwitchBox)
      {
          Serial.println("evalSwitchBoxAction("+String(chArCardID)+"))");
          evalSwitchBoxAction(String(chArCardID));
      }
      else if (eUC == GateKeeper)
      {
        Serial.println("evalGateKeeperAction("+String(chArCardID)+"))");
        evalGateKeeperAction(String(chArCardID));
      }
      else if (eUC == Counter)
      {
        Serial.println("evalCounterAction("+String(chArCardID)+"))");
        evalCounterAction(String(chArCardID));
          
      }
      else
      {
        // Init error message, this will be displayed in the next cycle of the loop
        eState = error;
        strMsg = "unknown Use Case detected!";
      }
    }
    if(iTouchIntStatus)
    { 
      iTouchIntStatus = false;
      Serial.print("FT6336U TD Status: "); 
      Serial.print(ft6336u.read_td_status());  
      //Serial.print("FT6336U Touch Event/ID 1: ("); 
      //Serial.print(ft6336u.read_touch1_event()); 
      //Serial.print(" / "); 
      //Serial.print(ft6336u.read_touch1_id()); 
      Serial.println(")"); 
      Serial.print("FT6336U Touch Position 1: ("); 
      Serial.print(ft6336u.read_touch1_x()); 
      Serial.print(" , "); 
      Serial.print(ft6336u.read_touch1_y()); 
      Serial.println(")"); 
      //Serial.print("FT6336U Touch Weight/MISC 1: ("); 
      //Serial.print(ft6336u.read_touch1_weight()); 
      //Serial.print(" / "); 
      //Serial.print(ft6336u.read_touch1_misc()); 
      //Serial.println(")"); 
      //Serial.print("FT6336U Touch Event/ID 2: ("); 
      //Serial.print(ft6336u.read_touch2_event()); 
      //Serial.print(" / "); 
      //Serial.print(ft6336u.read_touch2_id()); 
      //Serial.println(")"); 
      //Serial.print("FT6336U Touch Position 2: ("); 
      //Serial.print(ft6336u.read_touch2_x()); 
      //Serial.print(" , "); 
      //Serial.print(ft6336u.read_touch2_y()); 
      //Serial.println(")"); 
      //Serial.print("FT6336U Touch Weight/MISC 2: ("); 
      //Serial.print(ft6336u.read_touch2_weight()); 
      //Serial.print(" / "); 
      //Serial.print(ft6336u.read_touch2_misc()); 
      //Serial.println(")"); 
      evalTouchAction();
    }  // end of touch action

    //}    // end of WLAN ok
  }
  if(eState == idle)
  {
    Serial.println("dsplyIdle()");
    dsplyIdle();
  }
  if(eState == working)
  {
    dsplyMask();
  }
  if(eState == error)
  {
    Serial.println("- error - "+strMsg);
    if(dsplyErrorInfo("Error",strMsg,5,1,99))
    { // touch event occured => soft reset
      softReset();
    }    
    delay(500);
    Serial.println("- error - "+strMsg);
  }
  if(eState == end)
  {
    Serial.println("intermediate display - eState = end");
    if((bIntChange == true) and (bIntRunning == false))
    {
      
      bIntChange  = false;  // flag to start display 
      bIntRunning = true;  // time how long the message shall be displayed
      lIntStartAt = millis();  // get start point f the intervall
      dsplyErrorInfo(strHeader,strMsg,0,0,iIconNo); 
      Serial.println("- End-Flag received - "+strMsg);
      Serial.println("- Header - "+strHeader); 
      Serial.println("- Msg    - "+strMsg);
      Serial.println("- Icon   - "+String(iIconNo));
      dsplyPrepareTouchEvent();
    }
    dsplyTime();
    dsplyWifiState();
    if((eUC == GateKeeper) and ((iIconNo == 9)  or (iIconNo == 10)) or ((eUC == SwitchBox) and  (iIconNo == 10))) // and bGKMinUpdateFlag
    { // the value of the units shall be displayed when the customer is entering or leving the workshop are every time
      // or switch of the tool 
      Serial.println("Info: before dsplyUnitSecond() ");
      dsplyUnitSecond();  // the update of minutes will called, when necessary
    }
    if(bIntRunning)
    {
      Serial.println("Start: "+String(lIntStartAt)+" Len "+String(clIntDuration)+" Time "+String(millis()));
      if(lIntStartAt + clIntDuration <= millis())
      { // message representation ends here
        Serial.println("Info: before chngState2Idle() ");
        chngState2Idle();
      }
    }
  }
}
