import React, {Component} from 'react';
import {
    View,
    UIManager,
    StyleSheet,
    requireNativeComponent,
    DeviceEventEmitter,
} from 'react-native';

const ReactNative = require('ReactNative');
import PropTypes from 'prop-types';

var PullLayout = requireNativeComponent('PullLayout', App);
export default class App extends Component {
    constructor(props) {
        super(props);
    }

    //数据获取后回调 刷新结束
    finishRefresh = (key) => {
        console.log("结束下拉" + key);
        UIManager.dispatchViewManagerCommand(ReactNative.findNodeHandle(this),
            UIManager.PullLayout.Commands.FinishRefresh, [key])
    };

    finishLoadMore = (key) => {
        console.log("结束上拉" + key);
        UIManager.dispatchViewManagerCommand(ReactNative.findNodeHandle(this),
            UIManager.PullLayout.Commands.FinishLoadMore, [key])
    }


    render() {
        return (
            <PullLayout
                ref={(pull) => {
                    this.pullLayout = pull
                }}
                style={[styles.pullLayout, this.props.style]}
                EnableOverScrollDrag={true}
                EnableOverScrollBounce={false}
                DisableContentWhenRefresh={false}
                {...this.props}
            >
                <View style={{flex: 1}}>
                    {this.props.children}
                </View>
            </PullLayout>
        )
    }
}

const styles = StyleSheet.create({
    pullLayout: {
        flex: 1,
        backgroundColor: 'white'
    }
});

PullLayout.propTypes = {
    ...View.propTypes,
    Key: PropTypes.string.isRequired,//必须 否则监听回调可能无法被调用
    onRefreshReleased: PropTypes.func,//下拉刷新回调，使用 DeviceEventEmitter 添加
    onLoadMoreReleased: PropTypes.func,//上拉加载回调，使用 DeviceEventEmitter 添加
    EnableOverScrollDrag: PropTypes.bool,//设置是否启用越界拖动（仿苹果效果）
    EnableOverScrollBounce: PropTypes.bool,//设置是否启用越界回弹
    DragRate: PropTypes.number, //显示拖动高度/真实拖动高度（默认0.5，阻尼效果）
    HeaderMaxDragRate: PropTypes.number,//设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
    HeaderTriggerRate: PropTypes.number,//设置 触发刷新距离 与 HeaderHieght 的比率
    ReboundDuration: PropTypes.number,//设置回弹动画时长
    EnableRefresh: PropTypes.bool,//是否启用下拉刷新（默认启用）
    EnableLoadMore: PropTypes.bool,//是否启用上拉加载（默认关闭）
    EnableHeaderTranslationContent: PropTypes.bool,//设置是否启在下拉Header的同时下拉内容
    DisableContentWhenRefresh: PropTypes.bool,//设置是否开启在刷新时候禁止操作内容视图
    EnablePureScrollMode: PropTypes.bool,//设置是否开启纯滚动模式
    EnableNestedScroll: PropTypes.bool,//设置是会否启用嵌套滚动功能（默认关闭+智能开启）
};