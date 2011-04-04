package jp.ecnavi.solr.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenizerFactory;

import jp.ecnavi.lucene.analysis.ECNaviNGTokenizer;

/**
 * ECNaviTokenizer の Factory.
 * @author HARUYAMA Seigo Seigo_Haruyama@ecnavi.co.jp
 *
 */
public class ECNaviNGTokenizerFactory extends BaseTokenizerFactory {

    /**
     * ECNaviNGTokernizerを生成する.
     * @param reader リーダー
     * @return 生成された ECNaviNGTokernizer
     */
    public final TokenStream create(final Reader reader) {
        return new ECNaviNGTokenizer(reader);
    }
}
