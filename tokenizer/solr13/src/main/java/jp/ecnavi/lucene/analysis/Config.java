package jp.ecnavi.lucene.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 設定を保持するクラス.
 * @author Yoichi_Sudo@ecnavi.co.jp, Seigo_Haruyama@ecnavi.co.jp
 */
public final class Config {
    /**
     * シングルトンなインスタンス.
     */
    private static final Config INSTANCE = new Config();


    /**
     * プロパティ.
     */
    private final transient Properties properties;

    /**
     * コンストラクタ.
     */
    private Config() {
        properties = load(new Properties());
    }

    /**
     * シングルトンなインスタンスを返す.
     * @return インスタンス
     */
    public static Config getInstance() {
        return INSTANCE;
    }

    /**
     * プロパティファイルからプロパティをロードする.
     * @param def 設定をロードされるプロパティ
     * @return    ファイルから設定をロードしたプロパティ
     */
    private static Properties load(final Properties def) {
        InputStream is = Config.class
                .getResourceAsStream("/ECNaviTokenizer.properties");
        try {
            if (is != null) {
                def.load(is);
            }
        } catch (Exception e) {
            // 設定値ごときに例外投げられてうれしい人はいない。
            // ダメならダメでアプリケーション側でデフォルト値を使うこと。
        	} finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
        return def;
    }

    /**
     * 設定を取得する.
     * @param key 設定のキー
     * @param def デフォルト値
     * @return キーに対応する設定
     */
    public String get(final String key, final String def) {
        return properties.getProperty(key, def);
    }
    /*
    public String get(final String key) {
        return get(key, null);
    }

    public Config set(final String key, final String val) {
        properties.setProperty(key, val);
        return this;
    }
    */
}
