

void SolenoidOFF(void)
/****************************************************************************************************
 * void SolenoidOFF(void) - Switch the wifi solenoid off via a webhook 
 ****************************************************************************************************/
{
  int iHttpCode = 0;
  httpClient.begin(strHTTPstart+strIpAddrSolenoid+strSolenoidOff);
  iHttpCode = httpClient.GET();
  delay(100);
  httpClient.end();
  eSolState = SolenoidOff;
  
}

void SolenoidON(void)
/****************************************************************************************************
 * void SolenoidON(void) - Switch the wifi solenoid on via a webhook 
 * The string will be assempled
 ****************************************************************************************************/
{
  int iHttpCode = 0;
  httpClient.begin(strHTTPstart+strIpAddrSolenoid+strSolenoidOn);
  iHttpCode = httpClient.GET();
  delay(100);
  httpClient.end();
  eSolState = SolenoidOn;
}

void SolenoidTOGGLE(void)
{
  int iHttpCode = 0;
  httpClient.begin(strHTTPstart+strIpAddrSolenoid+strSolenoidToggle);
  iHttpCode = httpClient.GET();
  delay(100);
  httpClient.end();
}
