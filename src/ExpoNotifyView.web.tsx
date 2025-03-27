import * as React from 'react';

import { ExpoNotifyViewProps } from './ExpoNotify.types';

export default function ExpoNotifyView(props: ExpoNotifyViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
