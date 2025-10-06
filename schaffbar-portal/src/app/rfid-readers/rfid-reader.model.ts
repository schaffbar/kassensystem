export interface RfidReader {
  id: string;
  macAddress: string;
  type: RfidReaderType;
}

export enum RfidReaderType {
  RfidTagRegister = 'RFID_TAG_REGISTER',
  RfidTagAssigner = 'RFID_TAG_ASSIGNER',
  GateKeeper = 'GATE_KEEPER',
  GateKeeperIn = 'GATE_KEEPER_IN',
  GateKeeperOut = 'GATE_KEEPER_OUT',
  SwitchBox = 'SWITCH_BOX',
}
