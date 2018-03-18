const gankBaseApi = 'http://gank.io/api/';
const gankHistoryDay = gankBaseApi + 'day/history';
const gankHomeList = gankBaseApi + 'day/{yyyy}/{MM}/{dd}';
const gankSearchList = gankBaseApi + 'search/query/{query}/category/{category}/count/{count}/page/{page}';

export {
    gankHistoryDay,
    gankHomeList,
    gankSearchList
}



