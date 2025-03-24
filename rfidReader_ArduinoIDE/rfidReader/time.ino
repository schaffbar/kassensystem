void initTime()
/****************************************************************************************************
 * void initTime() - Initialization of the Timer Object with a start date and time
 * and also calling the functions to init the associated time  and unit counter 
 ****************************************************************************************************/
{
  timeproj_t tInitTime;
  tInitTime.uiDateYear    = 2025;
  tInitTime.uiDateMonth   = 1;
  tInitTime.uiDateDay     = 1;
  tInitTime.uiDateHour    = 9;
  tInitTime. uiDateMinute = 0;
  tInitTime.uiDateSecond  = 0;
  setTime(tInitTime);  // 1st Jan 2025 09:00:00 
  //initTimeCounter();
  //initTimeUnitLength();
}

void setTime(timeproj_t currenttime)
/****************************************************************************************************
 * void setTime(timeproj_t currenttime)
 * setting the system time to given value 
 ****************************************************************************************************/
{
  rtc.setTime( currenttime.uiDateSecond, currenttime.uiDateMinute, currenttime.uiDateHour, currenttime.uiDateDay, currenttime.uiDateMonth, currenttime.uiDateYear );
}

void setStartTimestamp()
{
  ulTimeStart = rtc.getLocalEpoch();
}

double dGetCurrentUnit()
{
  unsigned long ulTimeDiff = (rtc.getLocalEpoch()-ulTimeStart); // time diff in seconds
  double retVal = (ulTimeDiff/ 21600);                          // convert seconds to 6 min intervall (6 min = 21600 seconds)
  return retVal;
}

String strGetCurrentUnit()
{
  return String(dGetCurrentUnit(),2);
}

/*
void initTimeCounter()
#/****************************************************************************************************
 * void initTimeCounter() - Initialization of the counter for the time and units
 ****************************************************************************************************
{
  iCountSec  = 0;
  iCountMin  = 0;
  iCountHour = 0;
  iCountDay  = 0;

  iUnitCountMin   = 0;
  iUnitCountHour  = 0;
  iUnitCountDay   = 0;
}

void initTimeUnitLength()
****************************************************************************************************
 * void initTimeUnitLength() - Initialization of the unit definition 
 ****************************************************************************************************
{
  iUnitLengthMin  = 0;
  iUnitLengthHour = 0;
  iUnitLengthDay  = 0;
}

String getTimePlain()
#****************************************************************************************************
 * String getTimePlain() - convert the content of the timer object into a string  
 ****************************************************************************************************#
{
  String strReturn = "";
  strReturn = "D:"+String(iCountDay)+"/H:"+String(iCountHour)+"/M:"+String(iCountMin)+"/S:"+String(iCountSec);
}

int getUnitCounterMin()
#****************************************************************************************************
 * int getUnitCounterMin() - get the value of the unit counter for minutes
 ****************************************************************************************************#
{
  return iUnitCountMin;
}

int getUnitCounterHour()
#****************************************************************************************************
 * int getUnitCounterHour() - get the value of the unit counter for hours
 ****************************************************************************************************#
{
  return iUnitCountHour;
}

int getUnitCounterDay()
#****************************************************************************************************
 * int iUnitCountHour() - get the value of the unit counter for days
 ****************************************************************************************************#
{
  return iUnitCountDay;
}
*/