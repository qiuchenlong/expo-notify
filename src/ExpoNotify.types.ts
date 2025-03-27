import type { StyleProp, ViewStyle } from 'react-native';

export type OnLoadEventPayload = {
  url: string;
};

export type ExpoNotifyModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;

  /** mqtt消息回调 */
  onMqttMessage: (params: any) => void;
};

export type ChangeEventPayload = {
  value: string;
};

export type ExpoNotifyViewProps = {
  url: string;
  onLoad: (event: { nativeEvent: OnLoadEventPayload }) => void;
  style?: StyleProp<ViewStyle>;
};
