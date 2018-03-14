import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image
} from 'react-native';
import TitleBar from './widget/TitleBar'

class Home extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header:null,
            tabBarLabel: '首页',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_home_highlighted.png")} style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_home.png")} style={styles.imageSize}/>
            )
        }
    }

    constructor(props) {
        super(props);
    }

    render() {
        const {navigate} = this.props.navigation;
        return <View style={styles.container}>
            <TitleBar title={'主页'}/>
        </View>;
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


export default Home;