package jp.ecnavi.lucene.analysis;

//import java.util.HashMap;
//import java.util.Map;

/**
 * 記号のうちASCIIと同様に扱うもののテーブルを保持するクラス.
 */
public final class SignTable {
    /**
     * 記号のうちASCIIと同様に扱うもののテーブル.
     */
    private final transient String table;

    /**
     * インスタンスを生成し返す.
     * @param allowed 記号のうちASCIIと同列に扱うものを連結した文字列
     * @return 新しいインスタンス
     */
    public static SignTable getInstance(final String allowed) {
        return new SignTable(allowed);
    }

    /**
     * コンストラクタ.
     * @param allowed 記号のうちASCIIと同様に扱うものを連結した文字列
     */
    private SignTable(final String allowed) {
        if (allowed == null) {
            table = "";
        } else {
            table = allowed;
        }
    }

    /*
    public SignTable load(final String[] allowed) {
        if (allowed != null) {
            for (String i : allowed) {
                if (!empty(i)) {
                    table.put(i, i);
                }
            }
        }
        return this;
    }
    */

    /**
     * 入力が, 記号のうちASCIIと同様に扱うものかどうかを判定する.
     * @param cp 記号のchar値ないしコードポイント値
     * @return ASCIIと同様に扱うならtrue
     */
    public boolean isSign(final int cp) {
        if (table.indexOf(cp) >= 0) {
            return true;
        }
        return false;
    }
    /*
    public boolean isSign(final char ch) {
        return isSign((int)ch);
    }
    */
/*
    private boolean isSign(final String sign) {
        return table.containsKey(sign);
    }
*/
    /*
    public static boolean empty(final String s) {
        return (s == null) || "".equals(s);
    }
    */
}
