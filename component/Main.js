import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
} from 'react-native';
import {StackNavigator, TabNavigator,} from 'react-navigation';

import Home from './Home'
import GankDetails from './GankDetails'
import Message from './Message'
import Find from './Find'
import Mine from './Mine'


class Main extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <View style={styles.container}>
            <AppNavigator/>
        </View>
    }

}

const TabContainer = TabNavigator(
    {
        HomeTab: {screen: Home},
        MsgTab: {screen: Message},
        FindTab: {screen: Find},
        MineTab: {screen: Mine},
    },
    {
        tabBarPosition: "bottom",
        animationEnabled: false,
        swipeEnabled: false,
        lazy: true,
        tabBarOptions: {
            activeTintColor: 'rgb(55,151,240)',
            inactiveTintColor: 'rgb(195,200,208)',
            showLabel: true,
            showIcon: true,
            labelStyle: {
                fontSize: 10,
                marginTop: -2
            },
            style: {
                height: 47,
                backgroundColor: '#fff',
                padding: 0,
                borderTopWidth: 1,
                borderColor: "rgb(235,235,235)"
            },
            indicatorStyle: {
                opacity: 0
            },
            iconStyle: {
                width: 24,
                height: 24,
                padding: 0,
                paddingBottom: 2
            },
            pressOpacity: 1,
        }

    }
);

const AppNavigator = StackNavigator({
    Main: {
        screen: TabContainer
    },
    GankDetails: {
        screen: GankDetails
    },
});

const styles = StyleSheet.create({
    container: {
        position: "absolute",
        top: 0,
        left: 0,
        bottom: 0,
        right: 0
    }
});


export default Main;