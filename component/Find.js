import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    FlatList,
    TextInput
} from 'react-native';
import TitleBar from './widget/TitleBar'

class Find extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '搜索',
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
        this.state = {
            keyWord: '',
        };
    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'搜索'}/>
            <View style={styles.searchView}>
                <Image
                    style={styles.searchIcon}
                    source={require('./assets/tab/tabbar_discover.png')}/>
                <TextInput
                    style={styles.searchInput}
                    placeholder={'搜索真的好了！不骗你！'}
                    returnKeyType='search'
                    underlineColorAndroid='transparent'
                    onChangeText={(text) => {

                    }}
                    onSubmitEditing={(event) => {

                    }}
                    onFocu={() => {

                    }}
                    onBlur={() => {

                    }}
                />
            </View>
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
    searchView: {
        borderRadius: 22,
        borderWidth: 1,
        borderColor: '#aeb0bf',
        flexDirection: 'row',
        paddingLeft: 8,
        paddingRight: 8,
        alignItems: 'center',
        marginTop: 5,
        marginLeft: 10,
        marginRight: 10,
    },
    searchIcon: {
        width: 18,
        height: 18,
    },
    searchInput: {
        padding: 0,
        paddingLeft: 3,
        flex: 1,
        height: 26,
        marginBottom: 1,
    }
});


export default Find;