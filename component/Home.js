import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    Text,
    Image,
    ScrollView,
    Dimensions
} from 'react-native';
import {
    gankHistoryDay,
    gankHomeList
} from './util/Cons'
import TitleBar from './widget/TitleBar'

const width = Dimensions.get('window').width;

class ItemContent extends Component {
    render() {
        return <View>
            <Text>
            </Text>
        </View>
    }
}

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
            girlImgUrl: '',
            iosData: [],
            androidData: [],
        };
    }

    render() {
        const {navigate} = this.props.navigation;
        return <View style={styles.container}>
            <TitleBar title={'主页'}/>
            <ScrollView contentContainerStyle={styles.content}>
                <Image source={{uri: `${this.state.girlImgUrl}`}} style={styles.girlImage}/>
            </ScrollView>
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
        let girImg;
        for (cate of homeData.cateList) {
            let data = homeData.dataList[cate];
            if (cate == '福利') {
                girImg = data[0].url;
            }
        }
        this.setState({
            girlImgUrl: girImg
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
        fontSize: 24,
        color: 'black',
        marginLeft: 16,
    },
    content: {
        flex: 1,
        alignItems: 'center'
    },
    girlImage: {
        marginTop: 10,
        width: width,
        height: 200,
    }
});


export default Home;