import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image
} from 'react-native';
import TitleBar from './widget/TitleBar'
import {GlideImage, ScaleType} from "./widget/GlideImage";

class Mine extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '我的',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_profile_highlighted.png")} style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_profile.png")} style={styles.imageSize}/>
            )
        }
    }


    constructor(props) {
        super(props);

    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'我的'}/>
            <Image
                style={{width: 300, height: 180, resizeMode: 'contain'}}
                source={{uri: 'http://7xi8d6.com1.z0.glb.clouddn.com/20180102083655_3t4ytm_Screenshot.jpeg'}}/>
            <GlideImage
                style={{width: 300, height: 180,}}
                scaleType={ScaleType.CENTER_INSIDE}
                source={{uri: 'http://7xi8d6.com1.z0.glb.clouddn.com/20180102083655_3t4ytm_Screenshot.jpeg'}}/>
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


export default Mine;