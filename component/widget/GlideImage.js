import React, {Component} from 'react';
import {
    View,
    StyleSheet,
    requireNativeComponent,
} from 'react-native';
import * as _ from "lodash";
import PropTypes from 'prop-types';

const ScaleType = {
    CENTER: 1,         // 图片居中显示
    CENTER_CROP: 2,    // 对应 默认(cover)
    CENTER_INSIDE: 3,  // 对应 contain(center)
    FIT_CENTER: 4,     // 对应 contain(center)
    FIT_END: 5,
    FIT_START: 6,
    FIT_XY: 7,         // 对应 stretch
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
        this.processAttr(props);
    }

    processAttr(props) {
        let source = resolveAssetSource(props.source);
        let placeholder = resolveAssetSource(props.placeholder);
        let errorImg = resolveAssetSource(props.errorImg);

        if (source && source.uri === '') {
            console.warn('source.uri should not be an empty string');
        }

        if (props.src) {
            console.warn(
                'The <GlideImage> component requires a `source` property.',
            );
        }

        if (props.children) {
            throw new Error(
                'The <GlideImage> component cannot contain children. If you want to render content on top of the image, consider using the <ImageBackground> component or absolute positioning.',
            );
        }

        if (source && source.uri) {
            const {width, height} = source;
            let style = flattenStyle([{width, height}, styles.base, props.style]);

            let config = {
                source: source,
                scaleType: props.scaleType ? props.scaleType : ScaleType.CENTER_CROP,
                placeholderScaleType: props.placeholderScaleType ? props.placeholderScaleType : ScaleType.CENTER_CROP,
                errorImgScaleType: props.errorImgScaleType ? props.errorImgScaleType : ScaleType.CENTER_CROP,
                circleCrop: props.circleCrop,
                targetSize: props.targetSize,
            };

            if (placeholder && placeholder.uri) {
                config.placeholder = placeholder;
            }

            if (errorImg && errorImg.uri) {
                config.errorImg = errorImg;
            }

            this.attr = merge(props, {
                style,
                glideConfig: config,
            });
        }
    }

    shouldComponentUpdate(nextProps, nextState) {
        if (!_.isEqual(nextProps.source, this.props.source)) {
            this.processAttr(nextProps);
            return true;
        }
        return false;
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
 Boolean -> Boolean
 Integer -> Number
 Double -> Number
 Float -> Number
 String -> String
 Callback -> func
 ReadableMap -> Object
 ReadableArray -> Array
 */
GlideImage.propTypes = {
    ...View.propTypes,
    source: PropTypes.Object,
    placeholder: PropTypes.Object,
    errorImg: PropTypes.Object,
    scaleType: PropTypes.number,
    placeholderScaleType: PropTypes.number,
    errorImgScaleType: PropTypes.number,
    circleCrop: PropTypes.Boolean,
    targetSize: PropTypes.Array,
    onLoadStart: PropTypes.func,
    onLoadEnd: PropTypes.func,
    onError: PropTypes.func,
};

export {
    App as GlideImage,
    ScaleType
}