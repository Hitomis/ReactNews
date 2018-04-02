import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    FlatList,
    TouchableOpacity,
    DeviceEventEmitter,
    ToastAndroid,
    Dimensions,
} from 'react-native';
import TitleBar from './widget/TitleBar'
import MultiPage from './widget/MultiPage'
import Pulllayout from './widget/PullLayout'
import {GlideImage, ScaleType} from './widget/GlideImage'
import {gankTypeList, GankType, getTypeValueByIndex} from './util/Cons'

const typeImgSize = 60;
const width = Dimensions.get('window').width;

class Message extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '消息',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_message_center_highlighted.png")}
                           style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_message_center.png")} style={styles.imageSize}/>
            )
        }
    }

    constructor(props) {
        super(props);
        this.KEY = "gank_message";
        this.currPage = 1;
        this.count = 10;
        this.gankType = GankType.girl;
        this.state = ({
            typeResult: [],
        });
    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'消息'}/>
            <MultiPage
                style={styles.multiView}
                ref={(multi) => {
                    this.multiLayout = multi;
                }}
                Key={this.KEY}
                ContentSize={typeImgSize}
            />
            <Pulllayout
                Key={this.KEY}
                ref={(pull) => {
                    this.pullLayout = pull
                }}
                EnableRefresh={false}
                EnableLoadMore={true}>

                <FlatList
                    data={this.state.typeResult}
                    renderItem={this.renderTypeItem}
                    keyExtractor={this.keyGenerator}
                    ItemSeparatorComponent={this.typeListDivider}
                />

            </Pulllayout>
        </View>
    }

    componentDidMount() {
        DeviceEventEmitter.addListener(this.KEY + "onCenterItemClick", this.onCenterItemClick);
        DeviceEventEmitter.addListener(this.KEY + "onLoadMoreReleased", this.loadMoreReleased);
        this.getGanDataFromServer();
    }

    componentWillUnmount() {
        DeviceEventEmitter.removeAllListeners();
    }

    onCenterItemClick = (event) => {
        this.gankType = getTypeValueByIndex(parseInt(event.index));
        this.currPage = 1;
        this.getGanDataFromServer();
    }

    loadMoreReleased = async (params) => {
        this.currPage++;
        this.loadGankDataByType(this.gankType)
            .then((resultData) => {
                console.log('type gank data', resultData);
                if (resultData === undefined || resultData.length == 0) {
                    ToastAndroid.show('没有更多的信息了', ToastAndroid.SHORT);
                } else {
                    this.setState({
                        typeResult: [...this.state.typeResult, ...resultData],
                    });
                }
                this.pullLayout && this.pullLayout.finishLoadMore(this.KEY);
            })
    };

    keyGenerator = (item) => item._id + new Date().valueOf();

    typeListDivider = () => <View style={styles.listDivider}/>

    renderTypeItem = (item) => {
        if (this.gankType == GankType.girl) {
            return <GlideImage
                style={styles.girlImage}
                source={{uri: item.item.url}}
                scaleType={ScaleType.CENTER_CROP}
                targetSize={[width, 260]}
            />
        } else {
            return <TouchableOpacity
                style={styles.itemContainer}
                onPress={() => {
                    console.log('find item click', item.item.url);
                    const {navigate} = this.props.navigation;
                    navigate("GankDetails", {details: item.item});
                }}>
                <Text style={styles.resultContent}>{item.item.desc}</Text>
                <View style={styles.extraView}>
                    <Text style={styles.createDate}>{item.item.publishedAt}</Text>
                    <Text style={styles.author}>{item.item.who}</Text>
                </View>
            </TouchableOpacity>
        }
    }

    getGankData(typeGankUrl) {
        return new Promise((resolve, reject) => {
            fetch(typeGankUrl)
                .then((response) => {
                    return response.json();
                })
                .then((responseData) => {
                    let {error, results} = responseData;
                    if (!error) {
                        resolve(results)
                    } else {
                        reject(error);
                    }
                })
                .catch((error) => {
                    reject(error);
                });
        });
    }

    async loadGankDataByType(type) {
        let url = gankTypeList.replace('{type}', type);
        url = url.replace('{count}', this.count);
        url = url.replace('{page}', this.currPage);
        let typeGankData = await this.getGankData(url);
        return typeGankData;
    }

    getGanDataFromServer() {
        this.loadGankDataByType(this.gankType)
            .then((resultData) => {
                console.log('type gank data', resultData);
                this.setState({
                    typeResult: resultData,
                });
            });
    }

}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    multiView: {
        height: typeImgSize
    },
    imageSize: {
        width: 24,
        height: 24
    },
    itemContainer: {
        padding: 12,
    },
    resultContent: {
        color: '#4183c4',
        fontSize: 16,
        marginTop: 6,
    },
    extraView: {
        flex: 1,
        marginTop: 6,
        flexDirection: 'row',
        justifyContent: 'space-between'
    },
    createDate: {
        color: '#c0c0c0',
    },
    author: {
        color: '#6f6f6f'
    },
    listDivider: {
        width: '100%',
        height: 6,
        backgroundColor: '#f5f5f5'
    },
    girlImage: {
        width: '100%',
        height: 260,
    }
});


export default Message;