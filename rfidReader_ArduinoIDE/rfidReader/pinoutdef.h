/*
 * Hardware Description:
 * Typical pin layout used:
 * -----------------------------------------------------------------------------------------
 *             MFRC522      Arduino       Arduino   Arduino    Arduino          Arduino
 *             Reader/PCD   Uno/101       Mega      Nano v3    Leonardo/Micro   Pro Micro
 * Signal      Pin          Pin           Pin       Pin        Pin              Pin
 * -----------------------------------------------------------------------------------------
 * SPI SS      SDA(SS)      10            53        D10        10               10
 * SPI MOSI    MOSI         11 / ICSP-4   51        D11        ICSP-4           16
 * SPI MISO    MISO         12 / ICSP-1   50        D12        ICSP-1           14
 * SPI SCK     SCK          13 / ICSP-3   52        D13        ICSP-3           15
 *
 * The parameter BREADBOARD and PCB are defined in the main file of the project
 */
#ifdef BREADBOARD
  #define BUZZZER_PIN  12// ESP32 pin GPIO14 connected to piezo buzzer

  //------------------------------
  #define Pin_MFRC522_Reset  25 
  #define Pin_MFRC522_IRQ    26
  #define Pin_MFRC522_CS     2  

  //-----------------------------
  #define SD_CS   14  // SD card select pin

  #define TFT_CS   4
  #define TFT_DC  17
  #define TFT_RST 16

  //------ Touch Driver ------------------
  #define Touch_INT_PIN      0
  #define Touch_RST_PIN     15
  #define Touch_RST_Duration_ms 100
#endif

#ifdef PCB
  #define BUZZZER_PIN  1 // ESP32 pin GPIO1 connected to piezo buzzer

  #define TFT_CS  7
  #define TFT_DC  20//17
  #define TFT_RST 21//16

  #define SD_CS   3
  //------------------------------
  #define Pin_MFRC522_Reset 25 // not used
  #define Pin_MFRC522_IRQ   26 // not used
  #define Pin_MFRC522_CS    2//27  

  //------ Touch Driver ------------------
  #define Touch_INT_PIN   0
  #define Touch_RST_PIN  10
  #define Touch_RST_Duration_ms 100
#endif