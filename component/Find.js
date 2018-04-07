import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    FlatList,
    TextInput,
    TouchableOpacity,
    ToastAndroid,
} from 'react-native';
import TitleBar from './widget/TitleBar'
import PullLayout from './widget/PullLayout'
import {gankSearchList} from './util/Cons'

class Find extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '发现',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_discover_highlighted.png")} style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_discover.png")} style={styles.imageSize}/>
            )
        }
    }

    currPage = 1;
    count = 10;
    keyword = '';
    onEndReachedCalledDuringMomentum = false;

    constructor(props) {
        super(props);
        this.state = {
            searchResult: [],
            searchBorder: '#aeb0bf'
        };
    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'发现'}/>
            <View style={[styles.searchView, {borderColor: this.state.searchBorder}]}>
                <Image
                    style={styles.searchIcon}
                    source={require('./assets/tab/tabbar_discover.png')}/>
                <TextInput
                    style={styles.searchInput}
                    placeholder={'搜索真的好了！不骗你！'}
                    returnKeyType='search'
                    underlineColorAndroid='transparent'
                    onChangeText={(text) => {
                        this.keyword = text;
                    }}
                    onSubmitEditing={(event) => {
                        // 每次重新搜索，需要将当前页面重置为第一页
                        this.currPage = 1;
                        this.searchKeyword()
                            .then((resultData) => {
                                console.log('find search result', resultData);
                                if (resultData === undefined || resultData.length == 0) {
                                    ToastAndroid.show('没有相关信息', ToastAndroid.SHORT);
                                } else {
                                    this.setState({
                                        searchResult: resultData
                                    });
                                }

                            })
                    }}
                    onFocus={() => {
                        this.setState({
                            searchBorder: '#89abff'
                        });
                    }}
                    onBlur={() => {
                        this.setState({
                            searchBorder: '#aeb0bf'
                        });
                    }}
                />
            </View>
            <PullLayout
                ref={(pull) => {
                    this.pullLayout = pull
                }}
                EnableRefresh={false}
                EnableLoadMore={true}
                onLoadmore={() => {
                    this.loadMoreReleased();
                }}
            >
                <FlatList
                    data={this.state.searchResult}
                    renderItem={this.renderSearchItem}
                    keyExtractor={this.keyGenerator}
                    ItemSeparatorComponent={this.searchListDivider}
                    // onScroll={this.onFlatListScroll}
                    // onEndReachedThreshold={0.01}
                    // onEndReached={this.onLoadMore}
                />
            </PullLayout>
        </View>
    }

    onFlatListScroll = () => this.onEndReachedCalledDuringMomentum = true

    searchListDivider = () => <View style={styles.listDivider}/>

    renderSearchItem = (item) => {
        return <TouchableOpacity
            style={styles.itemContainer}
            onPress={() => {
                console.log('find item click', item.item.url);
                const {navigate} = this.props.navigation;
                navigate("GankDetails", {details: item.item});
            }}>
            <Text style={styles.resultType}>{item.item.type}</Text>
            <Text style={styles.resultContent}>{item.item.desc}</Text>
            <View style={styles.extraView}>
                <Text style={styles.createDate}>{item.item.publishedAt}</Text>
                <Text style={styles.author}>{item.item.who}</Text>
            </View>
        </TouchableOpacity>
    }

    keyGenerator = (item) => item.ganhuo_id + new Date().valueOf();

    loadMoreReleased = async (params) => {
        console.log('loadMore.....', params);

        this.currPage++;
        this.searchKeyword()
            .then((resultData) => {
                console.log('find load more', resultData);
                if (resultData === undefined || resultData.length == 0) {
                    ToastAndroid.show('没有更多的信息了', ToastAndroid.SHORT);
                } else {
                    this.setState({
                        searchResult: [...this.state.searchResult, ...resultData]
                    })
                }
                this.pullLayout && this.pullLayout.finishLoadMore();
            })
    };

    // onLoadMore = () => {
    //     if (this.onEndReachedCalledDuringMomentum) {
    //         this.currPage++;
    //         this.searchKeyword()
    //             .then((resultData) => {
    //                 console.log('find load more', resultData);
    //                 if (resultData === undefined || resultData.length == 0) {
    //                     ToastAndroid.show('没有更多的信息了', ToastAndroid.SHORT);
    //                 } else this.setState({
    //                     searchResult: [...this.state.searchResult, ...resultData]
    //                 })
    //             })
    //     }
    // };

    async searchKeyword() {
        if (this.keyword === '') return;
        let resultData = await this.searchGankData();
        return resultData;
    }

    searchGankData() {
        let url = gankSearchList;
        url = url.replace('{query}', this.keyword);
        url = url.replace('{category}', 'all');
        url = url.replace('{count}', this.count);
        url = url.replace('{page}', this.currPage);
        console.log('find search url', url);
        return new Promise((resolve, reject) => {
            fetch(url)
                .then((response) => {
                    return response.json();
                })
                .then((responseData) => {
                    let {count, error, results} = responseData;
                    if (!error) {
                        resolve(results);
                    } else {
                        reject(error);
                    }
                })
                .catch((error) => {
                    reject(error);
                })
        })
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
        marginBottom: 5,
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
    },
    itemContainer: {
        padding: 12,
    },
    resultType: {
        color: '#8d8d8d',
        fontSize: 12,
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
    }
});

export default Find;