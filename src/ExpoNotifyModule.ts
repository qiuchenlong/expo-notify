import { NativeModule, requireNativeModule } from 'expo';

import { ExpoNotifyModuleEvents } from './ExpoNotify.types';

declare class ExpoNotifyModule extends NativeModule<ExpoNotifyModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;

  /** 启动服务 */
  startMqttService(serverUrl: string, clientId: string, topic: string): Promise<void>;
  /** 停止服务 */
  stopMqttService(): Promise<void>;
  
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoNotifyModule>('ExpoNotify');
