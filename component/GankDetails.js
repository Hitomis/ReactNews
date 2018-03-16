import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    WebView
} from 'react-native';

class GankDetails extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            isLoad: false,
        }
    }

    constructor(props) {
        super(props)
        this.state = {
            details: this.props.navigation.state.params.details,
        };
        console.log('details info:', this.state.details);
    }

    render() {
        return <View style={styles.container}>
            <WebView
                mediaPlaybackRequiresUserAction={false}
                automaticallyAdjustContentInsets={true}
                domStorageEnabled={true}
                javaScriptEnabled={true}
                scalesPageToFit={true}
                startInLoadingState={true}
                style={styles.webView}
                source={{uri: `${this.state.details.url}`}}
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