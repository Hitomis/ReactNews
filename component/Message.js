import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image
} from 'react-native';
import TitleBar from './widget/TitleBar'

class Message extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '消息',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_message_center_highlighted.png")}
                           style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_message_center.png")} style={styles.imageSize}/>
            )
        }
    }

    constructor(props) {
        super(props);

    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'消息'}/>
        </View>
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    imageSize: {
        width: 24,
        height: 24
    },
});


export default Message;