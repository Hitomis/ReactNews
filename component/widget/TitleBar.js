import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Dimensions
} from 'react-native';

const width = Dimensions.get("window").width;

class TitleBar extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <View style={styles.container}>
            <Text style={styles.title}>
                {this.props.title}
            </Text>
        </View>
    }

}

const styles = StyleSheet.create({
    container: {
        width: width,
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#efefef',
        height: 45,
    },
    title: {
        color: '#000000',
        fontSize: 18,
    }
});

export default TitleBar;
