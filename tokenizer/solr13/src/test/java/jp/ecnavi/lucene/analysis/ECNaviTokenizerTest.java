package jp.ecnavi.lucene.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Token;

import junit.framework.TestCase;

public class ECNaviTokenizerTest extends TestCase {

	public void testAsciiString1() throws IOException {

		String testString = "uml";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		/*
		 * System.out.println(Integer.toString(token.startOffset()) + "-" +
		 * Integer.toString(token) + ": " + new
		 * String(token.termBuffer()));
		 */
		assertEquals(0, token.startOffset());
		assertEquals(testString.length(), token.endOffset());
		assertEquals(testString, token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testAsciiString2() throws IOException {

		String testString = "Apache lucene Nyuumon 1";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("apache", token.term());

		token = tokenizer.next();

		assertEquals(7, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("lucene", token.term());

		token = tokenizer.next();
		assertEquals(14, token.startOffset());
		assertEquals(21, token.endOffset());
		assertEquals("nyuumon", token.term());

		token = tokenizer.next();
		assertEquals(22, token.startOffset());
		assertEquals(23, token.endOffset());
		assertEquals("1", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testFullWidthRomanAlphabet1() throws IOException {

		String testString = "ＡＢＣ　ａＡAaａ iii   eee \n ＺｚＹｙZzYy";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("abc", token.term());

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("aaaaa", token.term());

		token = tokenizer.next();
		assertEquals(10, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("iii", token.term());

		token = tokenizer.next();
		assertEquals(16, token.startOffset());
		assertEquals(19, token.endOffset());
		assertEquals("eee", token.term());

		token = tokenizer.next();

		assertEquals(22, token.startOffset());
		assertEquals(30, token.endOffset());
		assertEquals("zzyyzzyy", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testFullWidthRomanAlphabet2() throws IOException {

		String testString = "０９ＡＺａｚ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("09azaz", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testNihongoString1() throws IOException {

		String testString = "あやら わ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("あや", token.term());

		token = tokenizer.next();

		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("やら", token.term());

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("わ", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testNihongoString2() throws IOException {

		String testString = "私の名前は中野です.";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("私の", token.term());

		token = tokenizer.next();
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("の名", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("名前", token.term());

		token = tokenizer.next();
		assertEquals(3, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("前は", token.term());

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("は中", token.term());

		token = tokenizer.next();
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("中野", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("野で", token.term());

		token = tokenizer.next();
		assertEquals(7, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("です", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testHankakuNihongoString1() throws IOException {

		String testString = "ﾊﾟｰｸ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("パー", token.term());

		
		token = tokenizer.next();

		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ーク", token.term());
		
		token = tokenizer.next();
		assertNull(token);

	}

	public void testHankakuNihongoString2() throws IOException {

		String testString = "ﾅｶｶﾞﾐ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("ナカ", token.term());

		token = tokenizer.next();

		assertEquals(1, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("カガ", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("ガミ", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testHankakuNihongoString3() throws IOException {

		String testString = "ｸﾟｰｸ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("ク゜", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ーク", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testHankakuNihongoString4() throws IOException {

		String testString = "ﾎﾞﾎﾟ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ボポ", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testHankakuNihongoString5() throws IOException {

		String testString = "ﾊﾞｰｶ nyo";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("バー", token.term());
		
		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ーカ", token.term());

		token = tokenizer.next();
		assertEquals(5, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("nyo", token.term());
		
		token = tokenizer.next();
		assertNull(token);

	}
	
	public void testHankakuNihongoString6() throws IOException {

		String testString = "ﾎﾞﾎﾟﾎﾟ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ボポ", token.term());

		
		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ポポ", token.term());

		
		token = tokenizer.next();
		assertNull(token);

	}

	
	public void testHankakuNihongoString7() throws IOException {

		String testString = "ﾎﾞﾎﾟﾎﾟにょ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ボポ", token.term());

		
		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ポポ", token.term());

		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("ポに", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("にょ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);

	}

	
	public void testHankakuNihongoString8() throws IOException {

		String testString = "ﾎﾞﾎﾟﾎﾟ☆にょ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ボポ", token.term());

		
		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ポポ", token.term());

		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("ポに", token.term());

		token = tokenizer.next();
		assertEquals(7, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("にょ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);

	}

	
	public void testHankakuNihongoString9() throws IOException {

		String testString = "ﾎﾟ☆ﾎﾟ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("ポポ", token.term());

		
		token = tokenizer.next();
		assertNull(token);

	}

	public void testHankakuNihongoString10() throws IOException {

		String testString = "nyo ﾎﾟ☆ﾎﾟ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("nyo", token.term());
			
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("ポポ", token.term());

		
		token = tokenizer.next();
		assertNull(token);

	}
	
	public void testHankakuNihongoString11() throws IOException {


		
		String testString = "ｯﾞ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("ッ゛", token.term());
			

		token = tokenizer.next();
		assertNull(token);

	}

	public void testHankakuNihongoString12() throws IOException {

		//ちゃんとした対応していないことの確認のテスト
		//他の変更により結果が変わるならそれもよし
		
		
		String testString = "ｯﾞ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);

		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("ツ゛", token.term());
			

		token = tokenizer.next();
		assertNull(token);
		ECNaviTokenizer.setDoCapitalizeKana(false);
	}

	
	
	/**
	 * 
	 * @throws IOException
	 */

	public void testMixedString1() throws IOException {

		String testString = "楽々ERDレッスン";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(false);
		
		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("楽々", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("erd", token.term());

		token = tokenizer.next();
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("レッ", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("ッス", token.term());

		token = tokenizer.next();
		assertEquals(7, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("スン", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testMixedString2() throws IOException {

		String testString = "楽々ERDレッ スン";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("楽々", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("erd", token.term());

		token = tokenizer.next();
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("レッ", token.term());

		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(10, token.endOffset());
		assertEquals("スン", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testSimbolString1() throws IOException {

		String testString = "ａ%Ａ％）";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals(")", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testＬｏｎｇString1() throws IOException {

		String testString = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(255, token.endOffset());

		token = tokenizer.next();
		assertEquals(255, token.startOffset());
		assertEquals(300, token.endOffset());

		token = tokenizer.next();
		assertNull(token);

	}

	public void testSignTable() {
		try{
			String NULL_STR = null;
			//String[] NULL_ARR = null;
			//new SignTable().load(NULL_ARR);
			SignTable.getInstance(NULL_STR);
			SignTable.getInstance("");
		}catch(Exception e){
			fail();
		}
		SignTable table = SignTable.getInstance("()+_");
		//文字列
		//assertTrue(table.isSign("("));
		//assertTrue(table.isSign(")"));
		//assertTrue(table.isSign("+"));
		//assertTrue(table.isSign("_"));

		//コードポイント
		assertTrue(table.isSign("(".codePointAt(0)));
		assertTrue(table.isSign(")".codePointAt(0)));
		assertTrue(table.isSign("+".codePointAt(0)));
		assertTrue(table.isSign("_".codePointAt(0)));

		//char
		assertTrue(table.isSign('('));
		assertTrue(table.isSign(')'));
		assertTrue(table.isSign('+'));
		assertTrue(table.isSign('_'));

		//assertFalse(table.isSign(null));
		//assertFalse(table.isSign(""));
		//assertFalse(table.isSign("a"));
		assertFalse(table.isSign("a".codePointAt(0)));
		assertFalse(table.isSign('a'));
	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testSymbol1() throws IOException {

		String testString = "_+#,(#+_)";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("_+#", token.term());

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("(#+_)", token.term());

		token = tokenizer.next();
		assertNull(token);

	}	
	
	/**
	 * 
	 * @throws IOException
	 */

	public void testCapitalizeKana1() throws IOException {

		String testString = "ぁァ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("あア", token.term());

		token = tokenizer.next();
		assertNull(token);

	}

	/**
	 * 
	 * @throws IOException
	 */

	public void testCapitalizeKana2() throws IOException {

		String testString = "ぁァ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(false);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("ぁァ", token.term());

		token = tokenizer.next();
		assertNull(token);

	}	
	
	/**
	 * 
	 * @throws IOException
	 */

	public void testCapitalizeKana3() throws IOException {

		String testString = "キャノン";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("キヤ", token.term());

		token = tokenizer.next();
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("ヤノ", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ノン", token.term());
		
		
		
		token = tokenizer.next();
		assertNull(token);

	}		

	
	public void testCapitalizeKana4() throws IOException {

		String testString = "ゎヮヵヶ";


		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("わワ", token.term());

		token = tokenizer.next();
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("ワカ", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("カケ", token.term());
		
		
		
		token = tokenizer.next();
		assertNull(token);

	}		

	public void testCapitalizeKana5() throws IOException {

		String testString = "ヵ゛ヶ゛";
		//ちゃんとした対応していないことの確認のテスト
		//他の変更により結果が変わるならそれもよし

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("カ", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("ケ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
		ECNaviTokenizer.setDoCapitalizeKana(false);
	}		
	
	
	public void testNomarilizeWave1() throws IOException {

		String testString = "～";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("ー", token.term());
		assertEquals("DOUBLE", token.type());
		
		token = tokenizer.next();
		assertNull(token);

	}		

	public void testNomarilizeWave2() throws IOException {

		String testString = "ー";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("ー", token.term());
		assertEquals("DOUBLE", token.type());
		
		token = tokenizer.next();
		assertNull(token);

	}		
	
	public void testNomarilizeWave3() throws IOException {

		String testString = "〜";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("ー", token.term());
		assertEquals("DOUBLE", token.type());
		
		token = tokenizer.next();
		assertNull(token);

	}	
	
	public void testNomarilizeWave4() throws IOException {

		String testString = "うぉ〜か〜";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);
		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("うお", token.term());

		token = tokenizer.next();
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("おー", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ーか", token.term());
		
		token = tokenizer.next();
		assertEquals(3, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("かー", token.term());

		
		token = tokenizer.next();
		assertNull(token);

	}
	
	
	public void testNomarilizeWave5() throws IOException {

		String testString = "うぉ～か～";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);
		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("うお", token.term());

		token = tokenizer.next();
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("おー", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ーか", token.term());
		
		token = tokenizer.next();
		assertEquals(3, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("かー", token.term());

		
		token = tokenizer.next();
		assertNull(token);

	}		

	
	public void testNomarilizeWave6() throws IOException {

		String testString = "~";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);
		Token token = tokenizer.next();

		assertNull(token);

	}		
	
	
	public void testUnHyphenation1() throws IOException {

		String testString = "A-B";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals(2, token.term().length());
		assertEquals("ab", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);

	}		

	public void testUnHyphenation2() throws IOException {

		String testString = "A-B-C";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());


		
		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		
		assertEquals("ab", token.term());

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(5, token.endOffset());
		
		assertEquals("abc", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("bc", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);

	}		
	
	public void testUnHyphenation3() throws IOException {

		String testString = "A- B";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("b", token.term());		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		

	public void testUnHyphenation4() throws IOException {

		String testString = "A -B";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("b", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	

	public void testUnHyphenation5() throws IOException {

		String testString = "Cyber-Shot";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("cyber", token.term());

		token = tokenizer.next();

		assertEquals(6, token.startOffset());
		assertEquals(10, token.endOffset());
		assertEquals("shot", token.term());
		
		token = tokenizer.next();

		assertEquals(0, token.startOffset());
		assertEquals(10, token.endOffset());
		assertEquals("cybershot", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	public void testUnHyphenation6() throws IOException {

		String testString = "あ-a";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("あ", token.term());

		token = tokenizer.next();

		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	

	
	public void testUnHyphenation7() throws IOException {

		String testString = "a-あ";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("あ", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	public void testUnHyphenation8() throws IOException {

		String testString = "a--B";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();

		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("b", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);

	}		
	
	
	
	public void testUnHyphenation9() throws IOException {

		String testString = "a-1";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("1", token.term());
		

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a1", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		

	public void testUnHyphenation10() throws IOException {

		String testString = "1-a";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("1a", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	
	public void testUnHyphenation11() throws IOException {

		String testString = "1-a-";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("1a", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		

	public void testUnHyphenation12() throws IOException {

		String testString = "-1-a";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(1, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("1", token.term());

		token = tokenizer.next();
		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("a", token.term());
		
		token = tokenizer.next();
		
		assertEquals(1, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("1a", token.term());
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	
	public void testUnHyphenation13() throws IOException {

		String testString = "A-B-C-D-E-F";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		token = tokenizer.next();
		assertEquals(10, token.startOffset());
		assertEquals(11, token.endOffset());
		assertEquals("f", token.term());
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
						
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		token = tokenizer.next();
		assertEquals("abcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("bcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("cdef", token.term());
		
		token = tokenizer.next();
		assertEquals("def", token.term());
				
		token = tokenizer.next();
		assertEquals("ef", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	

	
	public void testUnHyphenation14() throws IOException {

		String testString = "A-B-C-D-E-F-G";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		token = tokenizer.next();
		assertEquals(10, token.startOffset());
		assertEquals(11, token.endOffset());
		assertEquals("f", token.term());
		
		
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
		
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		token = tokenizer.next();
		assertEquals("abcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("bcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("cdef", token.term());
		
		token = tokenizer.next();
		assertEquals("def", token.term());
				
		token = tokenizer.next();
		assertEquals("ef", token.term());

		//----

		token = tokenizer.next();
		assertEquals(12, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("g", token.term());
		
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	

	public void testUnHyphenation15() throws IOException {

		String testString = "A-B-C-D-E-F-G-H";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		token = tokenizer.next();
		assertEquals(10, token.startOffset());
		assertEquals(11, token.endOffset());
		assertEquals("f", token.term());
		
		
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
		
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		token = tokenizer.next();
		assertEquals("abcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("bcdef", token.term());
		
		token = tokenizer.next();
		assertEquals("cdef", token.term());
		
		token = tokenizer.next();
		assertEquals("def", token.term());
				
		token = tokenizer.next();
		assertEquals("ef", token.term());

		//----

		token = tokenizer.next();
		assertEquals(12, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("g", token.term());
		
		token = tokenizer.next();
		assertEquals(14, token.startOffset());
		assertEquals(15, token.endOffset());
		assertEquals("h", token.term());
		
		token = tokenizer.next();
		assertEquals("gh", token.term());
		
		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	
	public void testUnHyphenation16() throws IOException {

		String testString = "0123456789"
				+ "-012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);

		Token token = tokenizer.next();
		
		assertEquals("0123456789", token.term());

		token = tokenizer.next();
		assertEquals(
				"0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
			+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
			+ "0123456789012345678901234567890123456789012345678901234"
				, token.term());
		
		token = tokenizer.next();
		assertEquals("0123456789" 
				+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
		+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
		+ "0123456789012345678901234567890123456789012345678901234"
			, token.term());
		
		token = tokenizer.next();
		
 		assertEquals("56789012345678901234567890123456789"
			, token.term());
		

		token = tokenizer.next();
		assertNull(token);

	}
	
	public void testUnHyphenation17() throws IOException {
		//System.out.println("17");
		String testString = "A-B-C-D-E--F-G";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
		
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		//----

		token = tokenizer.next();
		assertEquals(11, token.startOffset());
		assertEquals(12, token.endOffset());
		assertEquals("f", token.term());
		
		token = tokenizer.next();
		assertEquals(13, token.startOffset());
		assertEquals(14, token.endOffset());
		assertEquals("g", token.term());

		token = tokenizer.next();
		assertEquals("fg", token.term());		
		assertEquals(11, token.startOffset());
		assertEquals(14, token.endOffset());
		token = tokenizer.next();
		
		assertNull(token);

	}		
	public void testUnHyphenation18() throws IOException {
		String testString = "A-B-C-D-E-- F-G";


		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
		
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		//----

		token = tokenizer.next();
		assertEquals(12, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("f", token.term());
		
		token = tokenizer.next();
		assertEquals(14, token.startOffset());
		assertEquals(15, token.endOffset());
		assertEquals("g", token.term());

		token = tokenizer.next();
		assertEquals(12, token.startOffset());
		assertEquals(15, token.endOffset());
		assertEquals("fg", token.term());		

		token = tokenizer.next();
		
		assertNull(token);

	}		
	
	
	public void testUnHyphenation19() throws IOException {
		String testString = "A-B-C-D-E--あい";


		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		

		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("b", token.term());
		

		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("c", token.term());

		token = tokenizer.next();
		assertEquals(6, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("d", token.term());

		
		token = tokenizer.next();
		assertEquals(8, token.startOffset());
		assertEquals(9, token.endOffset());
		assertEquals("e", token.term());

		
		
		//------
		token = tokenizer.next();
		assertEquals("ab", token.term());
		
		token = tokenizer.next();
		assertEquals("abc", token.term());
		
		token = tokenizer.next();
		assertEquals("bc", token.term());
		
		token = tokenizer.next();
		assertEquals("abcd", token.term());
		
		token = tokenizer.next();
		assertEquals("bcd", token.term());

		token = tokenizer.next();
		assertEquals("cd", token.term());

		
		token = tokenizer.next();
		assertEquals("abcde", token.term());
		
		token = tokenizer.next();
		assertEquals("bcde", token.term());

		token = tokenizer.next();
		assertEquals("cde", token.term());

		token = tokenizer.next();
		assertEquals("de", token.term());

		//----

		token = tokenizer.next();
		assertEquals(11, token.startOffset());
		assertEquals(13, token.endOffset());
		assertEquals("あい", token.term());
		
		token = tokenizer.next();
		
		assertNull(token);

	}	
	
	public void testUnHyphenation20() throws IOException {

		String testString = "1-a 2-b";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());

		token = tokenizer.next();
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		

		token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("1a", token.term());
		
		token = tokenizer.next();
		assertEquals("2", token.term());
		token = tokenizer.next();
		assertEquals("b", token.term());
		
		token = tokenizer.next();
		assertEquals("2b", token.term());
		
		token = tokenizer.next();		
		assertNull(token);

	}		
	

	public void testUnHyphenation21() throws IOException {

		String testString = "1- 2-b";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());


		
		token = tokenizer.next();
		assertEquals("2", token.term());
		token = tokenizer.next();
		assertEquals("b", token.term());
		
		token = tokenizer.next();
		assertEquals("2b", token.term());
		
		token = tokenizer.next();		
		assertNull(token);

	}		
	
	public void testUnHyphenation22() throws IOException {

		String testString = "1-2--b";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());
		
		token = tokenizer.next();
		assertEquals("2", token.term());
		
		
		token = tokenizer.next();
		assertEquals("12", token.term());
		
		token = tokenizer.next();
		assertEquals("b", token.term());
		
		token = tokenizer.next();		
		assertNull(token);

	}		
	
	
	public void testUnHyphenation23() throws IOException {

		String testString = "1--2-b";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("1", token.term());
		
		token = tokenizer.next();
		assertEquals("2", token.term());
		
		
		token = tokenizer.next();
		assertEquals("b", token.term());
		
		token = tokenizer.next();
		assertEquals("2b", token.term());
		
		token = tokenizer.next();		
		assertNull(token);

	}		

	
	public void testUnHyphenation24() throws IOException {

		String testString = "PK-UG-M052互換";

		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("pk", token.term());
		
		token = tokenizer.next();
		assertEquals("ug", token.term());
		
		
		token = tokenizer.next();
		assertEquals("m052", token.term());
		
		token = tokenizer.next();
		assertEquals("pkug", token.term());

		token = tokenizer.next();
		assertEquals("pkugm052", token.term());

		token = tokenizer.next();
		assertEquals("ugm052", token.term());
		
		token = tokenizer.next();
		assertEquals("互換", token.term());
		
		token = tokenizer.next();		
		assertNull(token);

	}		
	

	
	
	
	
	public void testSpecialSymbol1() throws IOException {
		String testString = "だ☆ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol2() throws IOException {
		String testString = "つのだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}		
	
	
	public void testSpecialSymbol3() throws IOException {
		String testString = "だ☆☆ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("だ", token.term());

		token = tokenizer.next();
		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ひ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
	}

	public void testSpecialSymbol4() throws IOException {
		String testString = "つのだ☆☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}		
	
	public void testSpecialSymbol5() throws IOException {
		String testString = "だ☆ ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("だ", token.term());

		token = tokenizer.next();
		
		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ひ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
	}		
	

	public void testSpecialSymbol6() throws IOException {
		String testString = "つの だ☆☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("だ", token.term());

		token = tokenizer.next();	
		
		assertEquals(6, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}
	
	public void testSpecialSymbol7() throws IOException {
		String testString = "だ☆ ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("だ", token.term());

		token = tokenizer.next();	
		
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}	
	
	public void testSpecialSymbol8() throws IOException {
		String testString = "つのだ☆ ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		
		token = tokenizer.next();
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}		

	public void testSpecialSymbol9() throws IOException {
		String testString = "つのだ☆A";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("a", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}		
	


	public void testSpecialSymbol10() throws IOException {
		String testString = "A☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();		
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ひろ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		
	public void testSpecialSymbol11() throws IOException {
		String testString = "Aだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("a", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("だひ", token.term());
		
		
		token = tokenizer.next();		
		assertEquals(3, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("ひろ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		
	
	public void testSpecialSymbol12() throws IOException {
		String testString = "つのだ☆";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		
		token = tokenizer.next();
		assertNull(token);
	}	
	
	public void testSpecialSymbol13() throws IOException {
		String testString = "つのだ☆☆";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		
		token = tokenizer.next();
		assertNull(token);
	}
	
	public void testSpecialSymbol14() throws IOException {
		String testString = "☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("ひろ", token.term());


		token = tokenizer.next();
		assertNull(token);
	}	
	
	public void testSpecialSymbol15() throws IOException {
		String testString = "☆☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(2, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("ひろ", token.term());


		token = tokenizer.next();
		assertNull(token);
	}		


	public void testSpecialSymbol16() throws IOException {
		String testString = "つのaだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		
		token = tokenizer.next();		
		assertEquals(3, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();	
		
		assertEquals(5, token.startOffset());
		assertEquals(7, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}
	

	public void testSpecialSymbol17() throws IOException {
		String testString = "つのaだ☆☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(2, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("a", token.term());
		
		token = tokenizer.next();		
		assertEquals(3, token.startOffset());
		assertEquals(4, token.endOffset());
		assertEquals("だ", token.term());

		token = tokenizer.next();	
		
		assertEquals(6, token.startOffset());
		assertEquals(8, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}	

	
	public void testSpecialSymbol18() throws IOException {
		String testString = "だ★ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol19() throws IOException {
		String testString = "つのだ★ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}			

	public void testSpecialSymbol20() throws IOException {
		String testString = "つのだ★ひろ つのだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertEquals("つの", token.term());
		token = tokenizer.next();
		assertEquals("のだ", token.term());
		token = tokenizer.next();
		assertEquals("だひ", token.term());
		token = tokenizer.next();
		assertEquals("ひろ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
	}			
	
	public void testSpecialSymbol21() throws IOException {
		String testString = "つのだ★s つのだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		assertEquals("s", token.term());
		
		token = tokenizer.next();
		assertEquals("つの", token.term());
		token = tokenizer.next();
		assertEquals("のだ", token.term());
		token = tokenizer.next();
		assertEquals("だひ", token.term());
		token = tokenizer.next();
		assertEquals("ひろ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
	}			

	public void testSpecialSymbol22() throws IOException {
		String testString = "つのだ★☆ つのだ☆ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();
		assertEquals("つの", token.term());
		token = tokenizer.next();
		assertEquals("のだ", token.term());
		token = tokenizer.next();
		assertEquals("だひ", token.term());
		token = tokenizer.next();
		assertEquals("ひろ", token.term());
		
		
		token = tokenizer.next();
		assertNull(token);
	}			
	
	
	public void testSpecialSymbol23() throws IOException {
		String testString = "キラッ☆【";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		ECNaviTokenizer.setDoCapitalizeKana(true);
		
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("キラ", token.term());
		
		token = tokenizer.next();
		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("ラツ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}

	
	public void testSpecialSymbol24() throws IOException {
		String testString = "だ=ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol25() throws IOException {
		String testString = "つのだ=ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}			

	
	public void testSpecialSymbol26() throws IOException {
		String testString = "だ＝ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol27() throws IOException {
		String testString = "つのだ＝ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}			


	
	public void testSpecialSymbol28() throws IOException {
		String testString = "だ・ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol29() throws IOException {
		String testString = "つのだ・ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}			


	
	public void testSpecialSymbol30() throws IOException {
		String testString = "だ･ひ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(3, token.endOffset());

		assertEquals("だひ", token.term());

		token = tokenizer.next();
		assertNull(token);
	}		

	
	public void testSpecialSymbol31() throws IOException {
		String testString = "つのだ･ひろ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		Token token = tokenizer.next();
		
		assertEquals(0, token.startOffset());
		assertEquals(2, token.endOffset());
		assertEquals("つの", token.term());

		token = tokenizer.next();		
		assertEquals(1, token.startOffset());
		assertEquals(3, token.endOffset());
		assertEquals("のだ", token.term());

		token = tokenizer.next();	
		
		assertEquals(2, token.startOffset());
		assertEquals(5, token.endOffset());
		assertEquals("だひ", token.term());
		
		token = tokenizer.next();
		assertEquals(4, token.startOffset());
		assertEquals(6, token.endOffset());
		assertEquals("ひろ", token.term());
		
		token = tokenizer.next();
		assertNull(token);
	}			

	
	public void testDakuten1() throws IOException {
		String testString = "゛";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		
		assertEquals(false, Character.isLetterOrDigit(testString.charAt(0)));
		
		Token token = tokenizer.next();
		
		assertNull(token);
	}			
	
	public void testDakuten2() throws IOException {
		String testString = "ﾞ";
		java.io.StringReader reader = new java.io.StringReader(testString);
		ECNaviTokenizer tokenizer = new ECNaviTokenizer(reader);
		
		//ちゃんとした対応していないことの確認のテスト
		//他の変更により結果が変わるならそれもよし
		//Javaはいわゆる半角の濁点をCharacter.isLetterOrDigit()をtrueにしてるので
		//スルーしても
		
		assertEquals(true, Character.isLetterOrDigit(testString.charAt(0)));
		
		Token token = tokenizer.next();
		assertEquals(0, token.startOffset());
		assertEquals(1, token.endOffset());
		assertEquals("゛", token.term());
		token = tokenizer.next();
		assertNull(token);
	}			
	
}
