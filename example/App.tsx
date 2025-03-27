import { useEvent } from 'expo';
// import ExpoNotify, { ExpoNotifyView } from 'expo-notify';
import { Button, Linking, SafeAreaView, ScrollView, Text, View } from 'react-native';
import Example from './Example';
import { useEffect } from 'react';
import HomePage from './HomePage';

export default function App() {

  return <HomePage />

  // 监听应用启动时的 `deeplink`
  useEffect(() => {
    Linking.getInitialURL().then((url) => {
      console.log("🚀 ~ Linking.getInitialURL ~ url:", url)
      if (url) {
        const params = new URLSearchParams(new URL(url).search);
        console.log("🚀 ~ Linking.getInitialURL ~ params:", params)
        // navigateToDeepLink({ topic: params.get('topic'), message: params.get('message') });
      }
    });

    const subscription = Linking.addEventListener('url', ({ url }) => {
      const params = new URLSearchParams(new URL(url).search);
      console.log("🚀 ~ subscription ~ params:", params)
      // navigateToDeepLink({ topic: params.get('topic'), message: params.get('message') });
    });

    return () => subscription.remove();
  }, []);

  return <Example />;


  // const onChangePayload = useEvent(ExpoNotify, 'onChange');

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>Module API Example</Text>
        {/* <Group name="Constants">
          <Text>{ExpoNotify.PI}</Text>
        </Group>
        <Group name="Functions">
          <Text>{ExpoNotify.hello()}</Text>
        </Group>
        <Group name="Async functions">
          <Button
            title="Set value"
            onPress={async () => {
              await ExpoNotify.setValueAsync('Hello from JS!');
            }}
          />
        </Group>
        <Group name="Events">
          <Text>{onChangePayload?.value}</Text>
        </Group>
        <Group name="Views">
          <ExpoNotifyView
            url="https://www.example.com"
            onLoad={({ nativeEvent: { url } }) => console.log(`Loaded: ${url}`)}
            style={styles.view}
          />
        </Group> */}
      </ScrollView>
    </SafeAreaView>
  );
}

function Group(props: { name: string; children: React.ReactNode }) {
  return (
    <View style={styles.group}>
      <Text style={styles.groupHeader}>{props.name}</Text>
      {props.children}
    </View>
  );
}

const styles = {
  header: {
    fontSize: 30,
    margin: 20,
  },
  groupHeader: {
    fontSize: 20,
    marginBottom: 20,
  },
  group: {
    margin: 20,
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 20,
  },
  container: {
    flex: 1,
    backgroundColor: '#eee',
  },
  view: {
    flex: 1,
    height: 200,
  },
};
