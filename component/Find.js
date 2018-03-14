import React, {Component} from 'react';
import {StyleSheet, View, Text, Image} from 'react-native';

class Find extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '发现',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_discover_highlighted.png")} style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_discover.png")} style={styles.imageSize}/>
            )
        }
    }

    constructor(props) {
        super(props);

    }

    render() {
        return <View style={styles.container}>
            <Text>发现页</Text>
        </View>
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    imageSize: {
        width: 24,
        height: 24
    },
});


export default Find;