import React, {Component} from 'react';
import {requireNativeComponent, View,} from 'react-native';
import PropTypes from 'prop-types';

const ReactNative = require('ReactNative');

var MultiPage = requireNativeComponent('MultiPage', MultiPageLayout, {
    nativeOnly: {onChange: true}
});

class MultiPageLayout extends Component {
    constructor(props) {
        super(props)
    }

    _onClickCenter = (event) => {
        this.props.onClickCenter && this.props.onClickCenter(event.nativeEvent);
    }

    _onSlideCenter = (event) => {
        this.props.onSlideCenter && this.props.onSlideCenter(event.nativeEvent);
    }

    render() {
        return <MultiPage
            ref={(multi) => {
                this.multiPage = multi
            }}
            {...this.props}
            onClickCenter={this._onClickCenter}
            onSlideCenter={this._onSlideCenter}
        />
    }
}

MultiPage.propTypes = {
    ...View.propTypes,
    key: PropTypes.string.isRequired,//必须 否则监听回调可能无法被调用
    ContentSize: PropTypes.number,
    onClickCenter: PropTypes.func,
    onSlideCenter: PropTypes.func,
};

export default MultiPageLayout;