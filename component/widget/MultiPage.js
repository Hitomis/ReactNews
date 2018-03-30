import React, {Component} from 'react';
import {
    View,
    UIManager,
    StyleSheet,
    requireNativeComponent,
} from 'react-native';
import PropTypes from 'prop-types';

const ReactNative = require('ReactNative');

var MultiPage = requireNativeComponent('MultiPage', MultiPageLayout, {
    nativeOnly: {onChange: true}
});

class MultiPageLayout extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return <MultiPage
            ref={(multi) => {this.multiPage = multi}}
            style={styles.multiLayout}
            {...this.props}
        />
    }
}

const styles = StyleSheet.create({
    multiLayout: {
        flex: 1,
    }
});

MultiPage.propTypes = {
    ...View.propTypes,
    key: PropTypes.string.isRequired,//必须 否则监听回调可能无法被调用
    ContentSize: PropTypes.number,
};

export default MultiPageLayout;