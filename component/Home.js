import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    SectionList,
    Dimensions,
    TouchableOpacity
} from 'react-native';
import {
    gankHistoryDay,
    gankHomeList,
    GankType
} from './util/Cons'
import TitleBar from './widget/TitleBar'
import {GlideImage, ScaleType} from './widget/GlideImage'

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
            <TouchableOpacity onPress={() => {
                console.log('click header');
                const {navigate} = this.props.navigation;
                navigate("GankDetails", {details: this.state.girlImg});
            }}>
                <Text style={styles.section}>{this.state.girlImg.type}</Text>

                <GlideImage
                    style={styles.girlImage}
                    source={{uri: this.state.girlImg.url}}
                    scaleType={ScaleType.CENTER_CROP}
                />


            </TouchableOpacity>
        </View>);
    }

    renderFooter() {
        return <TouchableOpacity onPress={() => {
            console.log('click footer');
            const {navigate} = this.props.navigation;
            navigate("GankDetails", {details: this.state.restVideo});
        }}>
            <Text style={styles.section}>{this.state.restVideo.type}</Text>
            <Text style={styles.restVideoDesc}>{this.state.restVideo.desc}</Text>
            <View style={styles.restVideoInfo}>
                <Text>{this.state.restVideo.createdAt}</Text>
                <Text>{this.state.restVideo.who}</Text>
            </View>
        </TouchableOpacity>
    }

    renderCommonContent(item) {
        return (<View style={styles.contentContainer}>
            <TouchableOpacity onPress={() => {
                console.log('click Item');
                const {navigate} = this.props.navigation;
                navigate("GankDetails", {details: item.item});
            }}>
                <Text style={styles.contentDesc}>{item.item.desc}</Text>
                <Text style={styles.contentAuthor}>—— {item.item.who == null ? 'none' : item.item.who}</Text>
                {
                    item.item.images != null
                        ? <GlideImage
                            source={{uri: item.item.images[0]}}
                            style={styles.contentImg}
                        />
                        : null
                }
            </TouchableOpacity>
        </View>)
    }

    renderSectionHeader(sectionData) {
        return <Text style={styles.section}>{sectionData.title}</Text>;
    }

    renderContent() {
        let androidContent = {
            title: 'Android',
            data: this.state.androidData,
            renderItem: this.renderCommonContent.bind(this)
        };
        let iosContent = {
            title: 'IOS',
            data: this.state.iosData,
            renderItem: this.renderCommonContent.bind(this)
        };
        let frontEndContent = {
            title: '前端',
            data: this.state.frontEndData,
            renderItem: this.renderCommonContent.bind(this)
        };
        let appContent = {
            title: 'App',
            data: this.state.appData,
            renderItem: this.renderCommonContent.bind(this)
        };
        let extContent = {
            title: '拓展资源',
            data: this.state.extData,
            renderItem: this.renderCommonContent.bind(this)
        };
        let randReContent = {
            title: '瞎推荐',
            data: this.state.randRecoData,
            renderItem: this.renderCommonContent.bind(this)
        };
        return [androidContent, iosContent, frontEndContent, appContent, extContent, randReContent];
    }

    keyGenerator = (item) => item._id + new Date().valueOf();

    homeListDivider = () => <View style={styles.contentDivider}/>;

    homeSectionListDivider = () => <View style={styles.sectionDivider}/>

    render() {
        return <View style={styles.container}>
            <TitleBar title={'主页'}/>
            <SectionList
                ListHeaderComponent={this.renderHeader()}
                ListFooterComponent={this.renderFooter()}
                sections={this.renderContent()}
                renderSectionHeader={({section}) => this.renderSectionHeader(section)}
                ItemSeparatorComponent={this.homeListDivider}
                SectionSeparatorComponent={this.homeSectionListDivider}
                keyExtractor={this.keyGenerator}
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
                    let {error, results: resultDays} = responseJson;
                    if (!error) {
                        let dateArray = resultDays[0].split('-');
                        let url = gankHomeList;
                        url = url.replace('{yyyy}', dateArray[0]);
                        url = url.replace('{MM}', dateArray[1]);
                        url = url.replace('{dd}', dateArray[2]);
                        console.log(url);
                        resolve(url);
                    } else {
                        reject(error);
                    }
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
            if (cate === GankType.girl) {
                girImg = homeData.dataList[cate];
            } else if (cate === GankType.android) {
                androidData = data;
            } else if (cate === GankType.ios) {
                iosData = data;
            } else if (cate === GankType.frontEnd) {
                frontData = data;
            } else if (cate === GankType.app) {
                appData = data;
            } else if (cate === GankType.extRes) {
                extData = data;
            } else if (cate === GankType.randRec) {
                randData = data;
            } else if (cate === GankType.restVideo) {
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
    sectionDivider: {
        width: width,
        height: 1,
        backgroundColor: '#f5f5f5',
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
        paddingLeft: 12,
        paddingRight: 12,
    },
});


export default Home;