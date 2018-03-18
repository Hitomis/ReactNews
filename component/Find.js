import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    FlatList,
    TextInput,
    TouchableOpacity
} from 'react-native';
import TitleBar from './widget/TitleBar'
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

    constructor(props) {
        super(props);
        this.state = {
            keyword: '',
            currPage: 1,
            searchResult: [],
        };
    }

    render() {
        return <View style={styles.container}>
            <TitleBar title={'发现'}/>
            <View style={styles.searchView}>
                <Image
                    style={styles.searchIcon}
                    source={require('./assets/tab/tabbar_discover.png')}/>
                <TextInput
                    style={styles.searchInput}
                    placeholder={'搜索真的好了！不骗你！'}
                    returnKeyType='search'
                    underlineColorAndroid='transparent'
                    onChangeText={(text) => {
                        this.setState({
                            keyword: text
                        });
                    }}
                    onSubmitEditing={(event) => {
                        this.searchKeyword();
                    }}
                    onFocu={() => {

                    }}
                    onBlur={() => {

                    }}
                />
            </View>
            <FlatList
                data={this.state.searchResult}
                renderItem={this.renderSearchItem}
                keyExtractor={this.keyGenerator}
                ItemSeparatorComponent={this.searchListDivider}
                // onEndReachedThreshold={0.5}
                // onEndReached={this.onLoadMore}
            />
        </View>
    }

    searchListDivider = () => <View style={styles.listDivider}/>

    renderSearchItem = (item) => {
        return <TouchableOpacity
            style={styles.itemContainer}
            onPress={() => {
                console.log('click', item.item.url);
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

    onLoadMore = () => {
        this.setState({
            currPage: this.state.currPage++
        });
        this.searchKeyword(true);
    };

    async searchKeyword(loadMore = false) {
        let resultData = await this.searchGankData();
        if (resultData === undefined || resultData.length == 0) return;
        if (loadMore) {
            this.setState({
                searchResult: [...this.state.searchResult, resultData]
            })
        } else {
            this.setState({
                searchResult: resultData
            });
        }

    }

    searchGankData() {
        let url = gankSearchList;
        url = url.replace('{query}', this.state.keyword);
        url = url.replace('{category}', 'all');
        url = url.replace('{count}', 10);
        url = url.replace('{page}', this.state.currPage);
        console.log('find', url);
        return new Promise((resolve, reject) => {
            fetch(url)
                .then((response) => {
                    return response.json();
                })
                .then((responseData) => {
                    console.log('find', responseData)
                    let {count, error, results} = responseData;
                    resolve(results);
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