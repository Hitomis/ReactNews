const gankBaseApi = 'http://gank.io/api/';
const GankType = {
    girl: '福利',
    android: 'Android',
    ios: 'iOS',
    frontEnd: '前端',
    app: 'App',
    extRes: '拓展资源',
    randRec: '瞎推荐',
    restVideo: '休息视频'
};

const gankHistoryDay = gankBaseApi + 'day/history'; // 获取发过干货日期接口
const gankHomeList = gankBaseApi + 'day/{yyyy}/{MM}/{dd}'; // 获取指定日期的干货内容接口
const gankSearchList = gankBaseApi + 'search/query/{query}/category/{category}/count/{count}/page/{page}'; // 干活搜索接口
const gankTypeList = gankBaseApi + 'data/{type}/{count}/{page}' //按类型获取干活数据接口

export {
    GankType,
    gankHistoryDay,
    gankHomeList,
    gankSearchList,
    gankTypeList,
}



