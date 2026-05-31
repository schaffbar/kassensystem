export interface Tool {
  id: string;
  name: string;
  description: string;
  area: ToolArea;
  certificationRequirement: CertificationRequirement;
  rfidReaderId?: string;
  ipAddress?: string;
  wlanRelaisType?: WlanRelaisType;
  httpStartCommand?: string;
  onCommand?: string;
  offCommand?: string;
  instructors?: string[];
  createdAt: Date;
  updatedAt: Date;
}

// ----------------------------------------------------------------------------
// enums

export enum WlanRelaisType {
  Shelly1 = 'SHELLY_1',
  Shelly2 = 'SHELLY_2',
  ShellyPro = 'SHELLY_PRO',
}

export enum ToolArea {
  Holz = 'HOLZ',
  Metall = 'METALL',
  Kreativ = 'KREATIV',
  FabLab = 'FABLAB',
}

export enum CertificationRequirement {
  Yellow = 'YELLOW',
  Red = 'RED',
}

export interface WlanRelaisTemplate {
  httpStartCommand: string;
  onCommand: string;
  offCommand: string;
}

export const WLAN_RELAIS_TEMPLATES: Record<WlanRelaisType, WlanRelaisTemplate> = {
  [WlanRelaisType.Shelly1]: {
    httpStartCommand: 'http://{{ipAddress}}/relay/0?',
    onCommand: 'turn=on',
    offCommand: 'turn=off',
  },
  [WlanRelaisType.Shelly2]: {
    httpStartCommand: 'http://{{ipAddress}}/relay/0?',
    onCommand: 'turn=on',
    offCommand: 'turn=off',
  },
  [WlanRelaisType.ShellyPro]: {
    httpStartCommand: 'http://{{ipAddress}}/rpc/Switch.Set?',
    onCommand: 'id=0&on=true',
    offCommand: 'id=0&on=false',
  },
};

// ----------------------------------------------------------------------------
// commands

export interface CreateToolCommand {
  name: string;
  description?: string;
  area: ToolArea;
  certificationRequirement: CertificationRequirement;
}

export interface UpdateToolCommand {
  id: string;
  name?: string;
  description?: string;
  area: ToolArea;
  certificationRequirement: CertificationRequirement;
}

export interface ChangeRfidReaderCommand {
  toolId: string;
  rfidReaderId?: string;
}

export interface SetWlanRelaisCommand {
  toolId: string;
  wlanRelaisType: WlanRelaisType;
  ipAddress: string;
}
