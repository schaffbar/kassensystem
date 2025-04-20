from pydantic import BaseModel
from datetime import datetime


class DeviceInitRequest(BaseModel):
    MACADDR: str
    STATE: str
    DEVUSECASE: str


class DeviceResponse(BaseModel):
    DATEY: int = 0
    DATEM: int = 0
    DATED: int = 0
    TIMEH: int = 0
    TIMEM: int = 0
    TIMES: int = 0

    def __init__(self, **data):
        # Call the parent constructor to handle field validation and assignment
        super().__init__(**data)
        # Set the `now` field to the current datetime
        now = datetime.now()
        self.DATEY = now.year
        self.DATEM = now.month
        self.DATED = now.day
        self.TIMEH = now.hour
        self.TIMEM = now.minute
        self.TIMES = now.second


class DeviceInitResponse(DeviceResponse):
    STATE: str
    DEVNAME: str
    DEVUSECASE: str
    STARTHTTP: str
    DEVIP: str
    SWITCHON: str
    SWITCHOFF: str
    TERMINAL: str
    ERROR: str


class DeviceCardRequest(BaseModel):
    MACADDR: str
    STATE: str
    DEVUSECASE: str
    RFID: str
    ICON: str
    ERROR: str
    CUSTOMERNAME: str
    CUSTOMERSTARTSTOP: str


class DeviceCardResponse(BaseModel):
    CUSTOMERNAME: str
    CUSTOMERSTARTSTOP: str
    ICON: str
    STATE: str
    UNITS: str
    ERROR: str
    DEVUSECASE: str
