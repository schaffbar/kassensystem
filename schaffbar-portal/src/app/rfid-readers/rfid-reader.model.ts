export interface RfidReader {
  id: string;
  macAddress: string;
  type: RfidReaderType;
  name: string;
  socketName: string;
}

export enum RfidReaderType {
  RfidTagRegister = 'RFID_TAG_REGISTER',
  RfidTagAssigner = 'RFID_TAG_ASSIGNER',
  GateKeeperIn = 'GATE_KEEPER_IN',
  GateKeeperOut = 'GATE_KEEPER_OUT',
  SwitchBox = 'SWITCH_BOX',
}

// ----------------------------------------------------------------------------
// commands

export interface UpdateRfidReaderCommand {
  id: string;
  type: RfidReaderType;
  name?: string;
  socketName?: string;
}
