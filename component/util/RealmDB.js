import Realm from 'realm'

class MainGank extends Realm.Object {}
MainGank.schema = {
    name: 'MainGank',
    primaryKey: 'id',
    properties: {
        id: 'string',
        date: 'string',  // 哪一天发布的
        createdAt: 'string',
        publishedAt: 'string',
        desc: 'string',
        source: 'string',
        type: 'string',
        url: 'string',
        who: 'string',
    }
};

export default new Realm({schema: [MainGank]});