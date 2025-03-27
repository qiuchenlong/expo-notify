import { requireNativeView } from 'expo';
import * as React from 'react';

import { ExpoNotifyViewProps } from './ExpoNotify.types';

const NativeView: React.ComponentType<ExpoNotifyViewProps> =
  requireNativeView('ExpoNotify');

export default function ExpoNotifyView(props: ExpoNotifyViewProps) {
  return <NativeView {...props} />;
}
