export interface RfidReader {
  id: string;
  macAddress: string;
  type: 'SWITCH_BOX' | 'GATE_KEEPER' | 'COUNTER';
}
