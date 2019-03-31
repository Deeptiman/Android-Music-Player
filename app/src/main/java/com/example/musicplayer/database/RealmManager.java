package com.example.musicplayer.database;

import com.example.musicplayer.appmanager.MusicAppManager;
import io.realm.DynamicRealm;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;

public class RealmManager {

    public static RealmConfiguration realmConfig;
    public static Realm realm;

    public void initialization() {

        Realm.init(MusicAppManager.getContext());

        realmConfig = new RealmConfiguration.Builder()
                .name("music.realm")
                .schemaVersion(42)
                .migration(migration)
                .build();

        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getInstance(realmConfig);
    }


    public static Realm getRealm() {
        return realm;
    }


    RealmMigration migration = new RealmMigration() {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        }
    };


    public static RealmResults getModelList(Class clazz) {
        return getRealm().where(clazz).findAll();
    }

    public static RealmResults getDistinctModelList(Class clazz, String fieldName) {
        return getRealm().where(clazz).distinct(fieldName).findAll();
    }

    public static String[] getArtistName() {

        OrderedRealmCollection<Music> musicList = getDistinctModelList(Music.class, "artistName");

        String[] artistNames = new String[musicList.size()];
        int c = 0;
        for (Music music : musicList) {
            artistNames[c++] = music.getArtistName();
        }

        return artistNames;
    }


    public static String getMusicKey() {
        return "M" + getLatestId(Music.class);
    }

    public static int getLatestId(Class clazz) {
        return getRealm().where(clazz).findAll().size() + 1;
    }

    public static Object getModel(Class clazz, String fieldName, boolean value) {
        return getRealm().where(clazz).findFirst();
    }

    public static Object getModel(Class clazz, String fieldName, String value) {
        return getRealm().where(clazz).findFirst();
    }

    public static RealmResults getAllModelList(Class clazz, String fieldName, boolean value) {
        return getRealm().where(clazz).equalTo(fieldName, value).findAll();
    }

    public static RealmResults getAllModelList(Class clazz, String fieldName, String value) {
        return getRealm().where(clazz).equalTo(fieldName, value).findAll();
    }

    public static void clearTable(Realm realm, Class clazz) {
        realm.where(clazz).findAll().deleteAllFromRealm();
    }

}
