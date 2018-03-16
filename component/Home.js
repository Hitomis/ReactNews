import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    SectionList,
    Dimensions
} from 'react-native';
import {
    gankHistoryDay,
    gankHomeList
} from './util/Cons'
import TitleBar from './widget/TitleBar'

const width = Dimensions.get('window').width;

class Home extends Component {
    static navigationOptions = ({navigation}) => {
        return {
            header: null,
            tabBarLabel: '首页',
            tabBarIcon: ({tintColor, focused}) => (
                focused ?
                    <Image source={require("./assets/tab/tabbar_home_highlighted.png")} style={styles.imageSize}/>
                    :
                    <Image source={require("./assets/tab/tabbar_home.png")} style={styles.imageSize}/>
            )
        }
    }

    constructor(props) {
        super(props);
        this.state = {
            homeDataList: [],
            girlImg: {},
            restVideo: {},
            androidData: [],
            iosData: [],
            frontEndData: [],
            appData: [],
            extData: [],
            randRecoData: [],
        };
    }

    renderHeader() {
        return (<View>
            <Text style={styles.section}>{this.state.girlImg.type}</Text>
            <Image source={{uri: `${this.state.girlImg.url}`}} style={styles.girlImage}/>
        </View>);
    }

    renderFooter() {
        return (<View>
            <Text style={styles.section}>{this.state.restVideo.type}</Text>
            <Text style={styles.restVideoDesc}>{this.state.restVideo.desc}</Text>
            <View style={styles.restVideoInfo}>
                <Text style={styles.restInfoLeft}>{this.state.restVideo.createdAt}</Text>
                <Text style={styles.restInfoRight}>{this.state.restVideo.who}</Text>
            </View>
        </View>)
    }

    renderSectionHeader(sectionData) {
        return <Text style={styles.section}>{sectionData.title}</Text>;
    }

    renderCommonContent(item) {
        console.log('==============', item);
        return (<View style={styles.contentContainer}>
            <Text style={styles.contentDesc}>{item.item.desc}</Text>
            <Text style={styles.contentAuthor}>—— {item.item.who == null ? 'none' : item.item.who}</Text>
            {
                item.item.images != null
                    ? <Image source={{uri: `${item.item.images[0]}`}} style={styles.contentImg}/>
                    : null
            }
            <View style={styles.contentDivider}/>
        </View>)
    }

    renderContent() {
        let androidContent = {
            title: 'Android',
            data: this.state.androidData,
            renderItem: this.renderCommonContent
        };
        let iosContent = {
            title: 'IOS',
            data: this.state.iosData,
            renderItem: this.renderCommonContent
        };
        let frontEndContent = {
            title: '前端',
            data: this.state.frontEndData,
            renderItem: this.renderCommonContent
        };
        let appContent = {
            title: 'App',
            data: this.state.appData,
            renderItem: this.renderCommonContent
        };
        let extContent = {
            title: '拓展资源',
            data: this.state.extData,
            renderItem: this.renderCommonContent
        };
        let randReContent = {
            title: '瞎推荐',
            data: this.state.randRecoData,
            renderItem: this.renderCommonContent
        };
        return [androidContent, iosContent, frontEndContent, appContent, extContent, randReContent];
    }

    render() {
        const {navigate} = this.props.navigation;
        return <View style={styles.container}>
            <TitleBar title={'主页'}/>
            <SectionList
                ListHeaderComponent={this.renderHeader()}
                ListFooterComponent={this.renderFooter()}
                sections={this.renderContent()}
                renderSectionHeader={({section}) => this.renderSectionHeader(section)}
            />
        </View>;
    }

    queryGankPostHistoryDays() {
        return new Promise((resolve, reject) => {
            fetch(gankHistoryDay)
                .then((response) => {
                    return response.json();
                })
                .then((responseJson) => {
                    let flag;
                    let {error, results: resultDays} = responseJson;
                    let dateArray = resultDays[0].split('-');
                    let url = gankHomeList;
                    url = url.replace('{yyyy}', dateArray[0]);
                    url = url.replace('{MM}', dateArray[1]);
                    url = url.replace('{dd}', dateArray[2]);
                    console.log(url);
                    resolve(url);
                })
                .catch((error) => {
                    reject(error)
                });
        });
    }

    queryGankHomeData(recentDayUrl) {
        return new Promise((resolve, reject) => {
            fetch(recentDayUrl)
                .then((response) => {
                    return response.json();
                })
                .then((responseJson) => {
                    let {category: cateList, error, results: dataList} = responseJson;
                    resolve({cateList, dataList});
                }).catch((error) => {
                reject(error)
            });
        });
    }

    async loadHomeData() {
        let recentDayUrl = await this.queryGankPostHistoryDays();
        console.log('==================', recentDayUrl);

        let homeData = await this.queryGankHomeData(recentDayUrl);
        console.log('==================', homeData);

        let girImg, androidData, iosData, frontData, appData, extData, randData, restData;
        for (cate of homeData.cateList) {
            let data = homeData.dataList[cate];
            if (cate == '福利') {
                girImg = homeData.dataList[cate];
            } else if (cate == 'Android') {
                androidData = data;
            } else if (cate == 'iOS') {
                iosData = data;
            } else if (cate == '前端') {
                frontData = data;
            } else if (cate == 'App') {
                appData = data;
            } else if (cate == '拓展资源') {
                extData = data;
            } else if (cate == '瞎推荐') {
                randData = data;
            } else if (cate == '休息视频') {
                restData = homeData.dataList[cate];
            }
        }
        ;

        console.log('==================', girImg[0]);
        console.log('==================', restData[0]);

        this.setState({
            girlImg: girImg[0],
            androidData: androidData,
            iosData: iosData,
            frontEndData: frontData,
            appData: appData,
            extData: extData,
            randRecoData: randData,
            restVideo: restData[0]
        });

    }

    componentDidMount() {
        this.loadHomeData();
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
    section: {
        marginTop: 10,
        fontSize: 24,
        color: 'black',
        marginLeft: 10,
    },
    girlImage: {
        marginTop: 8,
        width: width,
        height: 200,
    },
    contentContainer: {
        marginTop: 12,
    },
    contentDesc: {
        margin: 12,
        marginTop: 0,
        fontSize: 16,
        color: '#4183c4'
    },
    contentAuthor: {
        alignSelf: 'flex-end',
        marginRight: 12,
        marginBottom: 12,
    },
    contentImg: {
        alignSelf: 'center',
        marginBottom: 12,
        width: width - 24,
        height: 180,
    },
    contentDivider: {
        width: width,
        height: 10,
        backgroundColor: '#f5f5f5'
    },
    restVideoDesc: {
        color: '#89abff',
        fontSize: 18,
        alignSelf: 'center',
        margin: 15,
    },
    restVideoInfo: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        paddingBottom: 12,
    },
    restInfoLeft: {
        marginLeft: 12,
    },
    restInfoRight: {
        marginRight: 12,
    }
});


export default Home;