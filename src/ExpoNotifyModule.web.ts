import { registerWebModule, NativeModule } from 'expo';

import { ExpoNotifyModuleEvents } from './ExpoNotify.types';

class ExpoNotifyModule extends NativeModule<ExpoNotifyModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(ExpoNotifyModule);
