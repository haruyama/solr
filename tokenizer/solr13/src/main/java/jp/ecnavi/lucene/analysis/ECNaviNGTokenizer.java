package jp.ecnavi.lucene.analysis;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;

/**
 * Solr向けTokenizer. http://twistbendcoupling.net/501/cjktokenizer を元に,
 * いわゆる半角カナの取扱いを改善した.
 * 
 * @author HARUYAMA Seigo Seigo_Haruyama@ecnavi.co.jp
 * 
 */
public final class ECNaviNGTokenizer extends Tokenizer {

	/**
	 * Tokenの種類
	 */
	private enum TokenType {
		NULL, BIGRAM_SINGLE, BIGRAM_DOUBLE
	};

	/**
	 * 記号などの文字のタイプ
	 */
	private static final int CHARTYPE_SYMBOL = 0;
	
	/**
	 * ASCIIなどの文字タイプ
	 */
	private static final int CHARTYPE_SINGLE = 1;
	/**
	 * 日本語などの文字タイプ
	 */
	private static final int CHARTYPE_DOUBLE = 2;

	/**
	 * 半角->全角カナテープル
	 */
	private static final int[] KANA_TABLE = { 
			12530, 12449, 12451, 12453, 12455, 12457, 12515, 12517,
			12519, 12483, 12540, 12450, 12452, 12454, 12456, 12458, 12459,
			12461, 12463, 12465, 12467, 12469, 12471, 12473, 12475, 12477,
			12479, 12481, 12484, 12486, 12488, 12490, 12491, 12492, 12493,
			12494, 12495, 12498, 12501, 12504, 12507, 12510, 12511, 12512,
			12513, 12514, 12516, 12518, 12520, 12521, 12522, 12523, 12524,
			12525, 12527, 12531, 12443, 12444 };

	/**
	 * 文字列をどこまで読み進めたかを示す値
	 */
	private transient int offset = 0;
	/**
	 * Token切り出し用バッファ
	 */
	private transient final char[] buffer = new char[2];
	/**
	 * 最後に読んだ文字タイプ
	 */
	private transient int lastCharType = CHARTYPE_SYMBOL;
	/**
	 * Pushbackが可能なリーダー
	 */
	private transient final PushbackReader pbinput;


	/**
	 * 英数字文字列に含む記号のテーブルを取得する
	 * 
	 * @return 記号のテーブル
	 */
	private static SignTable getSignTable() {
		Config c = Config.getInstance();
		return SignTable.getInstance(c.get("allowed_sign", "+_#"));
	}

	/**
	 * カナを正規化(大文字化)するかどうかのフラグ
	 */
	private static boolean doCapitalizeKana = Config.getInstance().get(
			"capitalize_kana", "true").equals("true") ? true : false;

	
	/**
	 * 前後の文字を連結する文字のテーブル
	 */

	private static String concatCharTable = Config.getInstance().get(
			"concat_char", "・＝=☆★･");
	
	
	/**
	 * いわゆる半角カナの濁点・半濁点をまとめたときの offsetの補正
	 */
	private transient int hankakuOffset = 0;

	/**
	 * 符号のテーブル
	 */
	private static final SignTable st = getSignTable();
	
	/**
	 * コンストラクタ
	 * 
	 * @param reader リーダー
	 */
	public ECNaviNGTokenizer(final Reader reader) {
		super();
		pbinput = new PushbackReader(reader, 2);
		input = pbinput;
	}

	/**
	 * 次のトークンを切り出す
	 */
	public Token next() throws IOException {


		int length = 0;
		int start = offset;
		int prevCharType = lastCharType;
		int charType = lastCharType;
		int concatCharCount = 0;
		TokenType tokenType = TokenType.NULL;
		boolean isHankakuMerged;

		do {
			// 現在の文字でいわゆる半角カナの濁点・半濁点を結合した場合にのみtrue
			isHankakuMerged = false;

			int c = pbinput.read();
			// 文字種の調査
			prevCharType = charType;
			if (c >= 0) {
				++offset;
				// tilde, waveを ーに変更
				if (c == '～' || c == '〜') {
					c = 'ー';
				}
				Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
				if (ub == Character.UnicodeBlock.BASIC_LATIN
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

					// ラテン文字及び記号。単語境界で分割する。

					// いわゆる全角アルファベット
					// unicodeで 'ａ'-'a' = 65248
					// あとで記号の多くは捨てられるのでもっと範囲が狭くてもいいかも
					if (c >= 65281 && c <= 65374) {
						c -= 65248;
					}

					if (c >= 65382 && c <= 65439) {
						// いわゆる半角カナ
						charType = CHARTYPE_DOUBLE;
						int i = c;
						int c2 = pbinput.read();

						if (c2 == 'ﾞ') {
							c = mergeDakuten(i);
						} else if (c2 == 'ﾟ') {
							c = mergeHandakuten(i);
						}

						if (c == i) {
							// 変換が行なわれなかった場合にはいわゆる全角文字に変換し
							// readerに文字を返す
							c = (char) KANA_TABLE[c - 65382];
							if (c2 >= 0) {
								pbinput.unread(c2);
							}
							if (doCapitalizeKana) {
								// カナを正規化する
								c = capitalizeKana(c);
							}
						} else {
							// 変換が行なわれた場合はオフセットの補正を行なう
							isHankakuMerged = true;
							++hankakuOffset;
						}
					} else if (Character.isLetterOrDigit(c) || st.isSign(c)) {
						charType = CHARTYPE_SINGLE;
						c = Character.toLowerCase(c);
					} else {
						charType = CHARTYPE_SYMBOL;
					}
				} else {
					if (Character.isLetter(c)) {
						charType = CHARTYPE_DOUBLE;
						if (doCapitalizeKana) {
							// カナを正規化する
							c = capitalizeKana(c);
						}
					} else {
						charType = CHARTYPE_SYMBOL;
					}
				}
			} else {
				// end of input.
				charType = CHARTYPE_SYMBOL;
			}

			

			// 現在のトークンタイプによって分岐
			if (tokenType.equals(TokenType.NULL)) {
				// 現在スキャン中のトークンなし。
				if (c < 0) {
					return null;
				}

				// 文字種によって、トークンタイプを決定
				// 記号は読み飛ばす
				if (charType == CHARTYPE_SINGLE) {
					start = offset - 1;
					length = 1;
					buffer[0] = (char) c;
					tokenType = TokenType.BIGRAM_SINGLE;
				} else if (charType == CHARTYPE_DOUBLE) {
					start = offset - 1;
					length = 1;
					buffer[0] = (char) c;
					tokenType = TokenType.BIGRAM_DOUBLE;
				} 
			} else if (tokenType.equals(TokenType.BIGRAM_SINGLE)) {

				if (charType == CHARTYPE_SINGLE) {
					buffer[length++] = (char) c;
					pbinput.unread(c);
					--offset;
					if (isHankakuMerged) {
						--offset;
					}
					charType = prevCharType;
					break;
				} else if (charType == CHARTYPE_DOUBLE) {
					pbinput.unread(c);
					--offset;
					charType = prevCharType;				
				}

				// 現在のバッファの内容は1文字しかない。

				if (c == '-') {
					if (concatCharCount++ > 0) {
						if (lastCharType == CHARTYPE_SINGLE) {
							concatCharCount = 0;
							length = 0;
							tokenType = TokenType.NULL;
							lastCharType = charType;
							continue;
						} else {
							concatCharCount = 0;
							break;
						}
					}
					charType = CHARTYPE_SINGLE;
				} else if (lastCharType == CHARTYPE_SINGLE) {
					concatCharCount = 0;
					length = 0;
					tokenType = TokenType.NULL;
					lastCharType = charType;
				} else {
					concatCharCount = 0;
					break;
				}

			} else if (tokenType.equals(TokenType.BIGRAM_DOUBLE)) {

				if (charType == CHARTYPE_DOUBLE) {
					buffer[length++] = (char) c;
					pbinput.unread(c);
					--offset;
					if (isHankakuMerged) {
						--offset;
					}
					charType = prevCharType;
					break;
				} else if (charType == CHARTYPE_SINGLE) {
					pbinput.unread(c);
					--offset;
					charType = prevCharType;				
				}

				// 現在のバッファの内容は1文字しかない。

				if (concatCharTable.indexOf(c) >= 0) {
					if (concatCharCount++ > 0) {
						if (lastCharType == CHARTYPE_DOUBLE) {
							concatCharCount = 0;
							length = 0;
							tokenType = TokenType.NULL;
							lastCharType = charType;
							continue;
						} else {
							concatCharCount = 0;
							break;
						}
					}
					charType = CHARTYPE_DOUBLE;
				} else if (lastCharType == CHARTYPE_DOUBLE) {
					concatCharCount = 0;
					length = 0;
					tokenType = TokenType.NULL;
					lastCharType = charType;
				} else {
					concatCharCount = 0;
					break;
				}
			} 
			//else {
			//throw new IOException();
			//}
		} while (true);

		lastCharType = charType;

		Token token = new Token(buffer, 0, length, start, start + length
				+ concatCharCount + hankakuOffset);
		token.setType(tokenType.name());

		// オフセットの補正をする.
		offset += hankakuOffset;

		// 最後の文字でいわゆる半角カナの濁点・半濁点が結合されている場合は
		// オフセットを残す
		if (isHankakuMerged) {
			hankakuOffset = 1;
		} else {
			hankakuOffset = 0;
		}
		return token;

	}


	/**
	 * ひらがなカナカナの小文字を大文字にする. それ以外の文字はそのまま.
	 * 
	 * @param c 文字
	 * @return かなカナを正規化した文字
	 */
	private int capitalizeKana(final int c) {
		switch (c) {
		case 'ぁ':
			return 'あ';
		case 'ぃ':
			return 'い';
		case 'ぅ':
			return 'う';
		case 'ぇ':
			return 'え';
		case 'ぉ':
			return 'お';
		case 'ゃ':
			return 'や';
		case 'ゅ':
			return 'ゆ';
		case 'ょ':
			return 'よ';
		case 'っ':
			return 'つ';
		case 'ァ':
			return 'ア';
		case 'ィ':
			return 'イ';
		case 'ゥ':
			return 'ウ';
		case 'ェ':
			return 'エ';
		case 'ォ':
			return 'オ';
		case 'ャ':
			return 'ヤ';
		case 'ュ':
			return 'ユ';
		case 'ョ':
			return 'ヨ';
		case 'ッ':
			return 'ツ';
		case 'ゎ':
			return 'わ';
		case 'ヮ':
			return 'ワ';
		case 'ヵ':
			return 'カ';
		case 'ヶ':
			return 'ケ';
		default:
			return c;
		}
	}

	/**
	 * 濁点が付けられるいわゆる半角カナ文字が入力として与えられた場合に対応する濁点を付けた全角カナ文字を返す. その他の入力はそのまま返す.
	 * 
	 * @param c1 変換前の文字
	 * @return 変換後の文字
	 */
	private static int mergeDakuten(final int c1) {

		switch (c1) {
		case 'ｶ':
			return 'ガ';
		case 'ｷ':
			return 'ギ';
		case 'ｸ':
			return 'グ';
		case 'ｹ':
			return 'ゲ';
		case 'ｺ':
			return 'ゴ';
		case 'ｻ':
			return 'ザ';
		case 'ｼ':
			return 'ジ';
		case 'ｽ':
			return 'ズ';
		case 'ｾ':
			return 'ゼ';
		case 'ｿ':
			return 'ゾ';
		case 'ﾀ':
			return 'ダ';
		case 'ﾁ':
			return 'ヂ';
		case 'ﾂ':
			return 'ヅ';
		case 'ﾃ':
			return 'デ';
		case 'ﾄ':
			return 'ド';
		case 'ﾊ':
			return 'バ';
		case 'ﾋ':
			return 'ビ';
		case 'ﾌ':
			return 'ブ';
		case 'ﾍ':
			return 'ベ';
		case 'ﾎ':
			return 'ボ';
		default:
			return c1;
		}

	}

	/**
	 * 半濁点が付けられるいわゆる半角カナ文字が入力として与えられた場合に対応する濁点を付けた全角カナ文字を返す. その他の入力はそのまま返す.
	 * 
	 * @param c1 変換前の文字
	 * @return 変換後の文字
	 */
	private static int mergeHandakuten(final int c1) {

		switch (c1) {
		case 'ﾊ':
			return 'パ';
		case 'ﾋ':
			return 'ピ';
		case 'ﾌ':
			return 'プ';
		case 'ﾍ':
			return 'ペ';
		case 'ﾎ':
			return 'ポ';
		default:
			return c1;
		}

	}

	/**
	 * デバッグ用 doCapitalizeKana Setter
	 * 
	 * @param doCapitalizeKana doCapitalizeKana のデバッグ値
	 */
	public static void setDoCapitalizeKana(final boolean doCapitalizeKana) {
		ECNaviNGTokenizer.doCapitalizeKana = doCapitalizeKana;
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * java.io.StringReader in = new java.io.StringReader(args[0]);
	 * CJKTokenizer2 tokenizer = new CJKTokenizer2(in); Token token; while
	 * ((token=tokenizer.next()) != null) {
	 * System.out.println(Integer.toString(token.startOffset()) + "-" +
	 * Integer.toString(token.endOffset()) + ": " + token.termText()); } }
	 */
}
