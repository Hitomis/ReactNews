import React, {Component} from 'react';
import {
    View,
    StyleSheet,
    requireNativeComponent,
} from 'react-native';
import PropTypes from 'prop-types';

const ScaleType = {
    CENTER: 1,
    CENTER_CROP: 2,
    CENTER_INSIDE: 3,
    FIT_CENTER: 4,
    FIT_END: 5,
    FIT_START: 6,
    FIT_XY: 7,
    MATRIX: 8
};

const merge = require('merge');
const resolveAssetSource = require('resolveAssetSource');
const flattenStyle = require('flattenStyle');
const GlideImage = requireNativeComponent('GlideImage', App);

class App extends Component {

    attr;

    constructor(props) {
        super(props)
        this.processAttr();
    }

    processAttr() {
        let source = resolveAssetSource(this.props.source);
        let placeholder = resolveAssetSource(this.props.placeholder);
        let errorImg = resolveAssetSource(this.props.errorImg);

        if (source && source.uri === '') {
            console.warn('source.uri should not be an empty string');
        }

        if (this.props.src) {
            console.warn(
                'The <GlideImage> component requires a `source` property.',
            );
        }

        if (this.props.children) {
            throw new Error(
                'The <GlideImage> component cannot contain children. If you want to render content on top of the image, consider using the <ImageBackground> component or absolute positioning.',
            );
        }

        if (source && source.uri) {
            const {width, height} = source;
            let style = flattenStyle([{width, height}, styles.base, this.props.style]);

            let config = {
                source: source,
                scaleType: this.props.scaleType,
                targetSize: this.props.targetSize,
            };

            if (placeholder && placeholder.uri) {
                config.placeholder = placeholder;
            }

            if (errorImg && errorImg.uri) {
                config.errorImg = errorImg;
            }

            this.attr = merge(this.props, {
                style,
                glideConfig: config,
            });
        }
    }


    render() {
        return <GlideImage
            ref={(glideImage) => {
                this.glideImage = glideImage
            }}
            {...this.attr}
        />
    }
}

const styles = StyleSheet.create({
    base: {
        overflow: 'hidden',
    },
});

/**
 Boolean -> Bool
 Integer -> Number
 Double -> Number
 Float -> Number
 String -> String
 Callback -> function
 ReadableMap -> Object
 ReadableArray -> Array
 */
GlideImage.propTypes = {
    ...View.propTypes,
    glideConfig: PropTypes.Object,//必须
    onLoadStart: PropTypes.func,
    onLoadEnd: PropTypes.func,
    onError: PropTypes.func,
};

export {
    App as GlideImage,
    ScaleType
}