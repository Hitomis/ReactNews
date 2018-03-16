import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    WebView
} from 'react-native';
import TitleBar from './widget/TitleBar'

class GankDetails extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
        }
    }

    constructor(props) {
        super(props)
    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'详情页'}/>
            <WebView
                style={styles.webView}
                source={}
            />
        </View>
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    webView: {
        width: '100%',
        height: '100%'
    }
});

export default GankDetails;