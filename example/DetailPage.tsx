import { memo } from "react";
import { View, Text } from "react-native";


interface IProps_DetailPage {

}
const DetailPage = memo((props: IProps_DetailPage) => {

    return (
        <View>
            <Text>这是详情页</Text>
        </View>
    )
})


export default DetailPage;