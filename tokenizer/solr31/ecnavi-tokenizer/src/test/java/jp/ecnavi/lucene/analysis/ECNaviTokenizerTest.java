package jp.ecnavi.lucene.analysis;

import java.io.IOException;

import junit.framework.TestCase;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

public final class ECNaviTokenizerTest extends TestCase {

    private static class TestToken {
        String termText;
        int start;
        int end;
    }

    public TestToken newToken(String termText, int start, int end) {
        TestToken token = new TestToken();
        token.termText = termText;
        token.start = start;
        token.end = end;
        return token;
    }

    public void checkToken(final String str, final TestToken[] outTokens)
        throws IOException {
        ECNaviTokenizer tokenizer = new ECNaviTokenizer(
                new java.io.StringReader(str));
        CharTermAttribute termAtt = (CharTermAttribute) tokenizer
            .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = (OffsetAttribute) tokenizer
            .getAttribute(OffsetAttribute.class);
        /*
         * TypeAttribute typeAtt = (TypeAttribute) tokenizer
         * .getAttribute(TypeAttribute.class);
         */
        for (int i = 0; i < outTokens.length; i++) {
            assertTrue("incrementToken() True", tokenizer.incrementToken());
            assertEquals("termText", outTokens[i].termText, termAtt.toString());
            assertEquals("start", outTokens[i].start, offsetAtt.startOffset());
            assertEquals("end", outTokens[i].end, offsetAtt.endOffset());
            // assertEquals(outTokens[i].type, typeAtt.type());
        }
        assertFalse("incrementToken() False", tokenizer.incrementToken());
    }

    public void checkTokenReusable(final String str1, final TestToken[] outTokens1,
            final String str2, final TestToken[] outTokens2)
        throws IOException {
        ECNaviTokenizer tokenizer = new ECNaviTokenizer(
                new java.io.StringReader(str1));
        CharTermAttribute termAtt = (CharTermAttribute) tokenizer
            .getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = (OffsetAttribute) tokenizer
            .getAttribute(OffsetAttribute.class);
        /*
         * TypeAttribute typeAtt = (TypeAttribute) tokenizer
         * .getAttribute(TypeAttribute.class);
         */
        for (int i = 0; i < outTokens1.length; i++) {
            assertTrue(tokenizer.incrementToken());
            assertEquals("termText", outTokens1[i].termText, termAtt.toString());
            assertEquals(outTokens1[i].start, offsetAtt.startOffset());
            assertEquals(outTokens1[i].end, offsetAtt.endOffset());
        }
        assertFalse(tokenizer.incrementToken());

        tokenizer.reset(new java.io.StringReader(str2));

        for (int i = 0; i < outTokens2.length; i++) {
            assertTrue(tokenizer.incrementToken());
            assertEquals("termText", outTokens2[i].termText, termAtt.toString());
            assertEquals(outTokens2[i].start, offsetAtt.startOffset());
            assertEquals(outTokens2[i].end, offsetAtt.endOffset());
        }
        assertFalse(tokenizer.incrementToken());

    }


    public void testAsciiString1() throws IOException {

        String testString = "uml";

        TestToken[] outTokens = { newToken("uml", 0, 3), };
        checkToken(testString, outTokens);

    }


    public void testAsciiString2() throws IOException {

        String testString = "Apache lucene Nyuumon 1";

        TestToken[] outTokens = {
            newToken("apache", 0, 6),
            newToken("lucene", 7, 13),
            newToken("nyuumon", 14, 21),
            newToken("1", 22, 23),
        };
        checkToken(testString, outTokens);

    }

    public void testFullWidthRomanAlphabet1() throws IOException {

        String testString = "ＡＢＣ　ａＡAaａ iii   eee \n ＺｚＹｙZzYy";


        TestToken[] outTokens = {
            newToken("abc", 0, 3),
            newToken("aaaaa", 4, 9),
            newToken("iii", 10, 13),
            newToken("eee", 16, 19),
            newToken("zzyyzzyy", 22, 30),
        };
        checkToken(testString, outTokens);



    }

    public void testFullWidthRomanAlphabet2() throws IOException {

        String testString = "０９ＡＺａｚ";


        TestToken[] outTokens = {
            newToken("09azaz", 0, 6),

        };
        checkToken(testString, outTokens);

    }

    public void testNihongoString1() throws IOException {

        String testString = "あやら わ";


        TestToken[] outTokens = {
            newToken("あや", 0, 2),
            newToken("やら", 1, 3),
            newToken("わ", 4, 5),

        };
        checkToken(testString, outTokens);

    }

    public void testNihongoString2() throws IOException {

        String testString = "私の名前は中野です.";

        TestToken[] outTokens = {
            newToken("私の", 0, 2),
            newToken("の名", 1, 3),
            newToken("名前", 2, 4),
            newToken("前は", 3, 5),
            newToken("は中", 4, 6),
            newToken("中野", 5, 7),
            newToken("野で", 6, 8),
            newToken("です", 7, 9),

        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString1() throws IOException {

        String testString = "ﾊﾟｰｸ";

        TestToken[] outTokens = {
            newToken("パー", 0, 3),
            newToken("ーク", 2, 4),

        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString2() throws IOException {

        String testString = "ﾅｶｶﾞﾐ";

        TestToken[] outTokens = {
            newToken("ナカ", 0, 2),
            newToken("カガ", 1, 4),
            newToken("ガミ", 2, 5),

        };
        checkToken(testString, outTokens);
    }

    /**
     *
     * @throws IOException
     */

    public void testHankakuNihongoString3() throws IOException {

        String testString = "ｸﾟｰｸ";
        TestToken[] outTokens = {
            newToken("ク゜", 0, 2),
            newToken("ーク", 2, 4),
        };
        checkToken(testString, outTokens);
    }

    /**
     *
     * @throws IOException
     */

    public void testHankakuNihongoString4() throws IOException {

        String testString = "ﾎﾞﾎﾟ";
        TestToken[] outTokens = {
            newToken("ボポ", 0, 4),
        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString5() throws IOException {

        String testString = "ﾊﾞｰｶ nyo";

        TestToken[] outTokens = {
            newToken("バー", 0, 3),
            newToken("ーカ", 2, 4),
            newToken("nyo", 5, 8),
        };
        checkToken(testString, outTokens);


    }

    public void testHankakuNihongoString6() throws IOException {

        String testString = "ﾎﾞﾎﾟﾎﾟ";
        TestToken[] outTokens = {
            newToken("ボポ", 0, 4),
            newToken("ポポ", 2, 6),
        };
        checkToken(testString, outTokens);
    }

    public void testHankakuNihongoString7() throws IOException {

        String testString = "ﾎﾞﾎﾟﾎﾟにょ";
        TestToken[] outTokens = {
            newToken("ボポ", 0, 4),
            newToken("ポポ", 2, 6),
            newToken("ポに", 4, 7),
            newToken("にょ", 6, 8),
        };
        checkToken(testString, outTokens);
    }

    public void testHankakuNihongoString8() throws IOException {

        String testString = "ﾎﾞﾎﾟﾎﾟ☆にょ";

        TestToken[] outTokens = {
            newToken("ボポ", 0, 4),
            newToken("ポポ", 2, 6),
            newToken("ポに", 4, 8),
            newToken("にょ", 7, 9),
        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString9() throws IOException {

        String testString = "ﾎﾟ☆ﾎﾟ";

        TestToken[] outTokens = {
            newToken("ポポ", 0, 5),
        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString10() throws IOException {

        String testString = "nyo ﾎﾟ☆ﾎﾟ";

        TestToken[] outTokens = {
            newToken("nyo", 0, 3),
            newToken("ポポ", 4, 9),
        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString11() throws IOException {

        String testString = "ｯﾞ";

        TestToken[] outTokens = {
            newToken("ッ゛", 0, 2),
        };
        checkToken(testString, outTokens);

    }

    public void testHankakuNihongoString12() throws IOException {

        // ちゃんとした対応していないことの確認のテスト
        // 他の変更により結果が変わるならそれもよし

        String testString = "ｯﾞ";


        TestToken[] outTokens = {
            newToken("ツ゛", 0, 2),
        };
        ECNaviTokenizer.setDoCapitalizeKana(true);
        checkToken(testString, outTokens);
        ECNaviTokenizer.setDoCapitalizeKana(false);
    }

    /**
     *
     * @throws IOException
     */

    public void testMixedString1() throws IOException {

        String testString = "楽々ERDレッスン";


        TestToken[] outTokens = {
            newToken("楽々", 0, 2),
            newToken("erd", 2, 5),
            newToken("レッ", 5, 7),
            newToken("ッス", 6, 8),
            newToken("スン", 7, 9),
        };
        checkToken(testString, outTokens);

    }

    /**
     *
     * @throws IOException
     */

    public void testMixedString2() throws IOException {

        String testString = "楽々ERDレッ スン";

        TestToken[] outTokens = {
            newToken("楽々", 0, 2),
            newToken("erd", 2, 5),
            newToken("レッ", 5, 7),
            newToken("スン", 8, 10),
        };
        checkToken(testString, outTokens);

    }

    /**
     *
     * @throws IOException
     */

    public void testSimbolString1() throws IOException {

        String testString = "ａ%Ａ％）";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("a", 2, 3),
            newToken(")", 4, 5),
        };
        checkToken(testString, outTokens);



    }

    /**
     *
     * @throws IOException
     */

    public void testLongString1() throws IOException {

        String testString = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
            + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
            + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

        TestToken[] outTokens = {
            newToken("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234"
                    , 0, 255),
            newToken("567890123456789012345678901234567890123456789", 255, 300),
        };
        checkToken(testString, outTokens);

    }

    public void testSignTable() {
        try {
            // String[] NULL_ARR = null;
            // new SignTable().load(NULL_ARR);
            SignTable.getInstance(null);
            SignTable.getInstance("");
        } catch (Exception e) {
            fail();
        }
        SignTable table = SignTable.getInstance("()+_");
        // 文字列
        // assertTrue(table.isSign("("));
        // assertTrue(table.isSign(")"));
        // assertTrue(table.isSign("+"));
        // assertTrue(table.isSign("_"));

        // コードポイント
        assertTrue(table.isSign("(".codePointAt(0)));
        assertTrue(table.isSign(")".codePointAt(0)));
        assertTrue(table.isSign("+".codePointAt(0)));
        assertTrue(table.isSign("_".codePointAt(0)));

        // char
        assertTrue(table.isSign('('));
        assertTrue(table.isSign(')'));
        assertTrue(table.isSign('+'));
        assertTrue(table.isSign('_'));

        // assertFalse(table.isSign(null));
        // assertFalse(table.isSign(""));
        // assertFalse(table.isSign("a"));
        assertFalse(table.isSign("a".codePointAt(0)));
        assertFalse(table.isSign('a'));
    }

    /**
     *
     * @throws IOException
     */

    public void testSymbol1() throws IOException {

        String testString = "_+#,(#+_)";

        TestToken[] outTokens = {
            newToken("_+#", 0, 3),
            newToken("(#+_)", 4, 9),
        };
        checkToken(testString, outTokens);

    }

    /**
     *
     * @throws IOException
     */

    public void testCapitalizeKana1() throws IOException {

        String testString = "ぁァ";
        ECNaviTokenizer.setDoCapitalizeKana(true);
        TestToken[] outTokens = {
            newToken("あア", 0, 2),
        };
        checkToken(testString, outTokens);
    }

    /**
     *
     * @throws IOException
     */

    public void testCapitalizeKana2() throws IOException {

        String testString = "ぁァ";
        ECNaviTokenizer.setDoCapitalizeKana(false);

        TestToken[] outTokens = {
            newToken("ぁァ", 0, 2),
        };
        checkToken(testString, outTokens);

    }

    /**
     *
     * @throws IOException
     */

    public void testCapitalizeKana3() throws IOException {

        String testString = "キャノン";
        ECNaviTokenizer.setDoCapitalizeKana(true);

        TestToken[] outTokens = {
            newToken("キヤ", 0, 2),
            newToken("ヤノ", 1, 3),
            newToken("ノン", 2, 4),
        };
        checkToken(testString, outTokens);

    }

    public void testCapitalizeKana4() throws IOException {

        String testString = "ゎヮヵヶ";
        ECNaviTokenizer.setDoCapitalizeKana(true);

        TestToken[] outTokens = {
            newToken("わワ", 0, 2),
            newToken("ワカ", 1, 3),
            newToken("カケ", 2, 4),
        };
        checkToken(testString, outTokens);
    }

    public void testCapitalizeKana5() throws IOException {

        String testString = "ヵ゛ヶ゛";
        // ちゃんとした対応していないことの確認のテスト
        // 他の変更により結果が変わるならそれもよし

        ECNaviTokenizer.setDoCapitalizeKana(true);
        TestToken[] outTokens = {
            newToken("カ", 0, 1),
            newToken("ケ", 2, 3),
        };
        checkToken(testString, outTokens);
        ECNaviTokenizer.setDoCapitalizeKana(false);
    }

    public void testNomarilizeWave1() throws IOException {

        String testString = "～";

        TestToken[] outTokens = {
            newToken("ー", 0, 1),
        };
        checkToken(testString, outTokens);

    }

    public void testNomarilizeWave2() throws IOException {

        String testString = "ー";

        TestToken[] outTokens = {
            newToken("ー", 0, 1),
        };
        checkToken(testString, outTokens);
    }

    public void testNomarilizeWave3() throws IOException {

        String testString = "〜";

        TestToken[] outTokens = {
            newToken("ー", 0, 1),
        };
        checkToken(testString, outTokens);
    }

    public void testNomarilizeWave4() throws IOException {

        String testString = "うぉ〜か〜";

        ECNaviTokenizer.setDoCapitalizeKana(true);

        TestToken[] outTokens = {
            newToken("うお", 0, 2),
            newToken("おー", 1, 3),
            newToken("ーか", 2, 4),
            newToken("かー", 3, 5),
        };
        checkToken(testString, outTokens);


    }

    public void testNomarilizeWave5() throws IOException {

        String testString = "うぉ～か～";

        ECNaviTokenizer.setDoCapitalizeKana(true);
        TestToken[] outTokens = {
            newToken("うお", 0, 2),
            newToken("おー", 1, 3),
            newToken("ーか", 2, 4),
            newToken("かー", 3, 5),
        };
        checkToken(testString, outTokens);


    }

    public void testNomarilizeWave6() throws IOException {

        String testString = "~";

        TestToken[] outTokens = {
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation1() throws IOException {

        String testString = "A-B";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("ab", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation2() throws IOException {

        String testString = "A-B-C";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation3() throws IOException {

        String testString = "A- B";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 3, 4),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation4() throws IOException {

        String testString = "A -B";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 3, 4),
        };
        checkToken(testString, outTokens);


    }

    public void testUnHyphenation5() throws IOException {

        String testString = "Cyber-Shot";

        TestToken[] outTokens = {
            newToken("cyber", 0, 5),
            newToken("shot", 6, 10),
            newToken("cybershot", 0, 10),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation6() throws IOException {

        String testString = "あ-a";
        TestToken[] outTokens = {
            newToken("あ", 0, 1),
            newToken("a", 2, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation7() throws IOException {

        String testString = "a-あ";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("あ", 2, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation8() throws IOException {

        String testString = "a--B";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 3, 4),
        };
        checkToken(testString, outTokens);


    }

    public void testUnHyphenation9() throws IOException {

        String testString = "a-1";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("1", 2, 3),
            newToken("a1", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation10() throws IOException {

        String testString = "1-a";
        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("a", 2, 3),
            newToken("1a", 0, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation11() throws IOException {

        String testString = "1-a-";
        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("a", 2, 3),
            newToken("1a", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation12() throws IOException {

        String testString = "-1-a";
        TestToken[] outTokens = {
            newToken("1", 1, 2),
            newToken("a", 3, 4),
            newToken("1a", 1, 4),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation13() throws IOException {

        String testString = "A-B-C-D-E-F";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("f", 10, 11),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("abcdef", 0, 11),
            newToken("bcdef", 2, 11),
            newToken("cdef", 4, 11),
            newToken("def", 6, 11),
            newToken("ef", 8, 11),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation14() throws IOException {

        String testString = "A-B-C-D-E-F-G";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("f", 10, 11),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("abcdef", 0, 11),
            newToken("bcdef", 2, 11),
            newToken("cdef", 4, 11),
            newToken("def", 6, 11),
            newToken("ef", 8, 11),
            newToken("g", 12, 13),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation15() throws IOException {

        String testString = "A-B-C-D-E-F-G-H";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("f", 10, 11),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("abcdef", 0, 11),
            newToken("bcdef", 2, 11),
            newToken("cdef", 4, 11),
            newToken("def", 6, 11),
            newToken("ef", 8, 11),
            newToken("g", 12, 13),
            newToken("h", 14, 15),
            newToken("gh", 12, 15),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation16() throws IOException {

        String testString = "0123456789"
            + "-012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
            + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
            + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

        TestToken[] outTokens = {
            newToken("0123456789", 0, 10),
            newToken(
                    "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234", 11, 266),
            newToken(
                    "0123456789"
                    + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
                    + "0123456789012345678901234567890123456789012345678901234", 0, 266),
            newToken("56789012345678901234567890123456789", 266, 301),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation17() throws IOException {
        String testString = "A-B-C-D-E--F-G";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("f", 11, 12),
            newToken("g", 13, 14),
            newToken("fg", 11, 14),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation18() throws IOException {
        String testString = "A-B-C-D-E-- F-G";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("f", 12, 13),
            newToken("g", 14, 15),
            newToken("fg", 12, 15),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation19() throws IOException {
        String testString = "A-B-C-D-E--あい";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("b", 2, 3),
            newToken("c", 4, 5),
            newToken("d", 6, 7),
            newToken("e", 8, 9),
            newToken("ab", 0, 3),
            newToken("abc", 0, 5),
            newToken("bc", 2, 5),
            newToken("abcd", 0, 7),
            newToken("bcd", 2, 7),
            newToken("cd", 4, 7),
            newToken("abcde", 0, 9),
            newToken("bcde", 2, 9),
            newToken("cde", 4, 9),
            newToken("de", 6, 9),
            newToken("あい", 11, 13),
        };
        checkToken(testString, outTokens);


    }

    public void testUnHyphenation20() throws IOException {

        String testString = "1-a 2-b";

        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("a", 2, 3),
            newToken("1a", 0, 3),
            newToken("2", 4, 5),
            newToken("b", 6, 7),
            newToken("2b", 4, 7),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation21() throws IOException {

        String testString = "1- 2-b";
        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("2", 3, 4),
            newToken("b", 5, 6),
            newToken("2b", 3, 6),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation22() throws IOException {

        String testString = "1-2--b";
        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("2", 2, 3),
            newToken("12", 0, 3),
            newToken("b", 5, 6),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation23() throws IOException {

        String testString = "1--2-b";
        TestToken[] outTokens = {
            newToken("1", 0, 1),
            newToken("2", 3, 4),
            newToken("b", 5, 6),
            newToken("2b", 3, 6),
        };
        checkToken(testString, outTokens);

    }

    public void testUnHyphenation24() throws IOException {

        String testString = "PK-UG-M052互換";
        TestToken[] outTokens = {
            newToken("pk", 0, 2),
            newToken("ug", 3, 5),
            newToken("m052", 6, 10),
            newToken("pkug", 0, 5),
            newToken("pkugm052", 0, 10),
            newToken("ugm052", 3, 10),
            newToken("互換", 10, 12),
        };
        checkToken(testString, outTokens);
    }

    public void testUnHyphenation25() throws IOException {

        String testString = "a-ﾊﾟ";

        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("パ", 2, 4),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol1() throws IOException {
        String testString = "だ☆ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol2() throws IOException {
        String testString = "つのだ☆ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol3() throws IOException {
        String testString = "だ☆☆ひ";
        TestToken[] outTokens = {
            newToken("だ", 0, 1),
            newToken("ひ", 3, 4),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol4() throws IOException {
        String testString = "つのだ☆☆ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("ひろ", 5, 7),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol5() throws IOException {
        String testString = "だ☆ ひ";
        TestToken[] outTokens = {
            newToken("だ", 0, 1),
            newToken("ひ", 3, 4),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol6() throws IOException {
        String testString = "つの だ☆☆ひろ";

        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("だ", 3, 4),
            newToken("ひろ", 6, 8),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol7() throws IOException {
        String testString = "だ☆ ☆ひろ";
        TestToken[] outTokens = {
            newToken("だ", 0, 1),
            newToken("ひろ", 4, 6),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol8() throws IOException {
        String testString = "つのだ☆ ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("ひろ", 5, 7),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol9() throws IOException {
        String testString = "つのだ☆A";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("a", 4, 5),
        };
        checkToken(testString, outTokens);


    }

    public void testSpecialSymbol10() throws IOException {
        String testString = "A☆ひろ";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("ひろ", 2, 4),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol11() throws IOException {
        String testString = "Aだ☆ひろ";
        TestToken[] outTokens = {
            newToken("a", 0, 1),
            newToken("だひ", 1, 4),
            newToken("ひろ", 3, 5),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol12() throws IOException {
        String testString = "つのだ☆";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol13() throws IOException {
        String testString = "つのだ☆☆";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol14() throws IOException {
        String testString = "☆ひろ";
        TestToken[] outTokens = {
            newToken("ひろ", 1, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol15() throws IOException {
        String testString = "☆☆ひろ";
        TestToken[] outTokens = {
            newToken("ひろ", 2, 4),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol16() throws IOException {
        String testString = "つのaだ☆ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("a", 2, 3),
            newToken("だひ", 3, 6),
            newToken("ひろ", 5, 7),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol17() throws IOException {
        String testString = "つのaだ☆☆ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("a", 2, 3),
            newToken("だ", 3, 4),
            newToken("ひろ", 6, 8),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol18() throws IOException {
        String testString = "だ★ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol19() throws IOException {
        String testString = "つのだ★ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),
        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol20() throws IOException {
        String testString = "つのだ★ひろ つのだ☆ひろ";

        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),

            newToken("つの", 7, 9),
            newToken("のだ", 8, 10),
            newToken("だひ", 9, 12),
            newToken("ひろ", 11, 13),

        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol21() throws IOException {
        String testString = "つのだ★s つのだ☆ひろ";

        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("s", 4, 5),

            newToken("つの", 6, 8),
            newToken("のだ", 7, 9),
            newToken("だひ", 8, 11),
            newToken("ひろ", 10, 12),

        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol22() throws IOException {
        String testString = "つのだ★☆ つのだ☆ひろ";

        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),

            newToken("つの", 6, 8),
            newToken("のだ", 7, 9),
            newToken("だひ", 8, 11),
            newToken("ひろ", 10, 12),

        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol23() throws IOException {
        String testString = "キラッ☆【";
        ECNaviTokenizer.setDoCapitalizeKana(true);
        TestToken[] outTokens = {
            newToken("キラ", 0, 2),
            newToken("ラツ", 1, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol24() throws IOException {
        String testString = "だ=ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol25() throws IOException {
        String testString = "つのだ=ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),

        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol26() throws IOException {
        String testString = "だ＝ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol27() throws IOException {
        String testString = "つのだ＝ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),

        };
        checkToken(testString, outTokens);

    }

    public void testSpecialSymbol28() throws IOException {
        String testString = "だ・ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol29() throws IOException {
        String testString = "つのだ・ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),

        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol30() throws IOException {
        String testString = "だ･ひ";
        TestToken[] outTokens = {
            newToken("だひ", 0, 3),
        };
        checkToken(testString, outTokens);
    }

    public void testSpecialSymbol31() throws IOException {
        String testString = "つのだ･ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("だひ", 2, 5),
            newToken("ひろ", 4, 6),

        };
        checkToken(testString, outTokens);
    }

    public void testDakuten1() throws IOException {
        String testString = "゛";
        TestToken[] outTokens = {
        };
        checkToken(testString, outTokens);


    }

    public void testDakuten2() throws IOException {
        // ちゃんとした対応していないことの確認のテスト
        // 他の変更により結果が変わるならそれもよし
        // Javaはいわゆる半角の濁点をCharacter.isLetterOrDigit()をtrueにしてる

        String testString = "ﾞ";
        TestToken[] outTokens = {
            newToken("゛", 0, 1),
        };
        checkToken(testString, outTokens);

    }

    public void testTokenResuable1() throws IOException {

        String testString1 = "０９ＡＺａｚ";


        TestToken[] outTokens1 = {
            newToken("09azaz", 0, 6),

        };

        String testString2 = "あやら わ";


        TestToken[] outTokens2 = {
            newToken("あや", 0, 2),
            newToken("やら", 1, 3),
            newToken("わ", 4, 5),

        };
        checkTokenReusable(testString1, outTokens1,
                testString2, outTokens2);

    }

    public void testFullSizeSpace1() throws IOException {
        String testString = "つのだ　ひろ";
        TestToken[] outTokens = {
            newToken("つの", 0, 2),
            newToken("のだ", 1, 3),
            newToken("ひろ", 4, 6),

        };
        checkToken(testString, outTokens);
    }

    /*
    public void testDuplicatedToken1() throws IOException {
//        String testString = "mie mie";
//        TestToken[] outTokens = {
//            newToken("mie", 0, 3),
//        };
//        checkToken(testString, outTokens, true);
    }

    public void testDuplicatedToken2() throws IOException {
        String testString = "mie三重mie";
        TestToken[] outTokens = {
            newToken("mie", 0, 3),
            newToken("三重", 3, 5),
        };
        checkToken(testString, outTokens, true);
    }

    public void testDuplicatedToken3() throws IOException {
        String testString = "mie三重miei";
        TestToken[] outTokens = {
            newToken("mie", 0, 3),
            newToken("三重", 3, 5),
            newToken("miei", 5, 9),
        };
        checkToken(testString, outTokens, true);
    }

    public void testDuplicatedToken4() throws IOException {
        String testString = "Cha-La-Cha";
        TestToken[] outTokens = {
            newToken("cha", 0, 3),
            newToken("la", 4, 6),
            newToken("chala", 0, 6),
            newToken("chalacha", 0, 10),
            newToken("lacha", 4, 10),
        };
        checkToken(testString, outTokens, true);
    }

    public void testDuplicatedToken5() throws IOException {
        String testString = "Cha-Cha-Cha";
        TestToken[] outTokens = {
            newToken("cha", 0, 3),
            newToken("chacha", 0, 7),
            newToken("chachacha", 0, 11),
        };
        checkToken(testString, outTokens, true);
    }

    public void testDuplicatedToken6() throws IOException {
        String testString = "のの☆の";
        TestToken[] outTokens = {
            newToken("のの", 0, 2),
        };
        checkToken(testString, outTokens, true);
    }
    */
}
