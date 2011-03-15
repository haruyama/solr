package jp.ecnavi.lucene.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.io.Reader;
import java.io.PushbackReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Solr向けTokenizer. http://twistbendcoupling.net/501/cjktokenizer を元に,
 * いわゆる半角カナの取扱いを改善した.
 *
 * @author HARUYAMA Seigo Seigo_Haruyama@ecnavi.co.jp
 *
 */
public final class ECNaviTokenizer extends Tokenizer {
	/**
	 * Tokenとして切り出す最大の文字長.
	 */
	private static final int MAX_WORD_LEN = 255;

	/**
	 * Tokenの種類.
	 */
	private enum TokenType {
		/**
		 * なにもない状態.
		 */
		NULL,
		/**
		 * ASCII文字.
		 */
		SINGLE,
		/**
		 * ASCII文字以外.
		 */
		DOUBLE,
		/**
		 * ハイフンで接続されたASCII文字を連結したもの.
		 */
		UNHYPHENATED
	};

	/**
	 * 記号などの文字のタイプ.
	 */
	private static final int CHARTYPE_SYMBOL = 0;
	/**
	 * ASCIIなどの文字タイプ.
	 */
	private static final int CHARTYPE_SINGLE = 1;
	/**
	 * bi-gram対象の文字タイプ.
	 */
	private static final int CHARTYPE_DOUBLE = 2;

	/**
	 * 半角->全角カナテープル.
	 */
	private static final int[] KANA_TABLE = { 12530, 12449, 12451, 12453,
			12455, 12457, 12515, 12517, 12519, 12483, 12540, 12450, 12452,
			12454, 12456, 12458, 12459, 12461, 12463, 12465, 12467, 12469,
			12471, 12473, 12475, 12477, 12479, 12481, 12484, 12486, 12488,
			12490, 12491, 12492, 12493, 12494, 12495, 12498, 12501, 12504,
			12507, 12510, 12511, 12512, 12513, 12514, 12516, 12518, 12520,
			12521, 12522, 12523, 12524, 12525, 12527, 12531, 12443, 12444 };

	/**
	 * 文字列をどこまで読み進めたかを示す値.
	 */
	private transient int offset = 0;
	/**
	 * Token切り出し用バッファ.
	 */
	private final transient char[] buffer = new char[MAX_WORD_LEN];
	/**
	 * 最後に読んだ文字タイプ.
	 */
	private transient int lastCharType = CHARTYPE_SYMBOL;
	/**
	 * 現在の文字タイプ.
	 */
	private transient int charType = CHARTYPE_SYMBOL;
	/**
	 * 1つ前の文字タイプ.
	 */
	private transient int prevCharType = CHARTYPE_SYMBOL;
	/**
	 * Pushbackが可能なリーダー.
	 */
	private transient PushbackReader pbinput;

	/**
	 * ハイフン関係の状態.
	 *
	 * NORMAL: 通常('', 'A') NORMAL_HYPHEN: 通常から'-'になった状態('A-', ただし '-' は除く)
	 * HYPHEN: '-'の後でASCIIが連続している除隊('A-B', 'A-B-C') HYPHEN_HYPHEN: ('A-B-')
	 * RETURN: Token情報を返している状態
	 *
	 */
	private enum HyphenState {
		/**
		 * 通常('', 'A').
		 */
		NORMAL,
		/**
		 * 通常から'-'になった状態('A-', ただし '-' は除く).
		 */
		NORMAL_HYPHEN,
		/**
		 * '-'の後でASCIIが連続している除隊('A-B', 'A-B-C').
		 */
		HYPHEN,
		/**
		 * ('A-B-').
		 */
		HYPHEN_HYPHEN,
		/**
		 * Token情報を返している状態.
		 */
		RETURN
	};

	/**
	 * 現在のハイフンの処理状態.
	 */
	private transient HyphenState hyphenState;

	/**
	 * ハイフン抜きで結合されるTokenの候補のリスト.
	 */
	private transient List<TokenCandidate> tokenCandidates;

	/**
	 * ハイフン抜きで結合されたTokenのリスト.
	 */
	private transient Queue<Token> unhyphenatedQueue;

	/**
	 * 英数字文字列に含む記号のテーブルを取得する.
	 *
	 * @return 記号のテーブル
	 */
	private static SignTable getSignTable() {
		return SignTable.getInstance(Config.getInstance().get("allowed_sign",
				"+_#"));
	}

	/**
	 * カナを正規化(大文字化)するかどうかのフラグ.
	 */
	private static boolean doCapitalizeKana = Config.getInstance()
			.get("capitalize_kana", "true").equals("true") ? true : false;

	/**
	 * 前後のかなカナ漢字を連結する文字のテーブル.
	 */

	private static String concatCharTable = Config.getInstance().get(
			"concat_char", "・＝=☆★･");

	/**
	 * ハイフンの連結をいくつまで許容するか ハイフンの数であって連結される文字列は +1 なことに注意.
	 */
	private static final int MAX_HYPHEN = 5;

	/**
	 * いわゆる半角カナの濁点・半濁点をまとめたときの offsetの補正.
	 */
	private transient int hankakuOffset = 0;

	/**
	 * いわゆる半角カナの濁点・半濁点をまとめたかどうか.
	 */
	private transient boolean isHankakuMerged;

	/**
	 * 符号のテーブル.
	 */
	private static final SignTable ST = getSignTable();

	/**
	 * 語の属性.
	 */
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	/**
	 * オフセットの属性.
	 */
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

	/**
	 * タイプの属性.
	 */
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	/**
	 * 現在処理中のトークンの種類.
	 */
	private TokenType tokenType;
	/**
	 * 現在処理中の接続文字カウント.
	 */
	private int concatCharCount;
	private int start;
	private int length;

	/**
	 * コンストラクタ.
	 *
	 * @param reader リーダー
	 */
	public ECNaviTokenizer(final Reader reader) {
		super();
		init(reader);
	}

	/**
	 * コンストラクタ.
	 *
	 * @param source
	 *            ソース
	 * @param reader
	 *            リーダー
	 */
	public ECNaviTokenizer(final AttributeSource source, final Reader reader) {
		super(source);
		init(reader);
	}

	/**
	 * コンストラクタ.
	 *
	 * @param factory
	 *            ファクトリ
	 * @param reader
	 *            リーダー
	 */
	public ECNaviTokenizer(final AttributeFactory factory, final Reader reader) {
		super(factory);
		init(reader);
	}

	/**
	 * 初期化.
	 *
	 * @param reader
	 *            リーダー
	 */
	private void init(final Reader reader) {

		pbinput = new PushbackReader(reader, 2);
		unhyphenatedQueue = new LinkedList<Token>();
		tokenCandidates = new ArrayList<TokenCandidate>();
		hyphenState = HyphenState.NORMAL;
		input = pbinput;
		offset = 0;
		lastCharType = CHARTYPE_SYMBOL;
	}

	/**
	 * ハイフンの処理状態をリセットする.
	 */
	private void resetHyphenState() {
		if (hyphenState != HyphenState.NORMAL) {
			hyphenState = HyphenState.NORMAL;
			tokenCandidates.clear();
			// unhyphenatedTokenQueue.clear();
		}
	}

	/**
	 * 内部的にToken情報を保持するクラス.
	 *
	 * @author s-haruyama
	 *
	 */
	private static class Token {

		/**
		 * buffer.
		 */
		private transient char[] buffer;

		/**
		 * bufferを返す.
		 *
		 * @return buffer
		 */
		public char[] getBuffer() {
			// 最終的にLucene側でSystem.arraycopy()するので
			// コピーは返さない.
			return buffer;
		}

		/**
		 * 長さ.
		 */
		private transient int length;

		/**
		 * 長さを返す.
		 *
		 * @return 長さ
		 */
		public int getLength() {
			return length;
		}

		/**
		 * 開始位置.
		 */
		private transient int start;

		/**
		 * 開始位置を返す.
		 *
		 * @return 開始位置
		 */
		public int getStart() {
			return start;
		}

		/**
		 * 終了位置.
		 */
		private transient int end;

		/**
		 * 終了位置を返す.
		 *
		 * @return 終了位置
		 */
		public int getEnd() {
			return end;
		}

		/**
		 * 種類.
		 */
		private transient String type;

		/**
		 * 種類を返す.
		 *
		 * @return 種類
		 */
		public String getType() {
			return type;
		}

		/**
		 * コンストラクタ.
		 *
		 * @param buffer
		 *            buffer
		 * @param length
		 *            長さ
		 * @param start
		 *            開始位置
		 * @param end
		 *            終了位置
		 * @param type
		 *            種類
		 */
		public Token(final char[] buffer, final int length, final int start,
				final int end, final String type) {
			this.buffer = buffer;
			this.length = length;
			this.start = start;
			this.end = end;
			this.type = type;
		}

	}

	/**
	 * ハイフン抜きで結合されたToken候補を保持するクラス.
	 *
	 * @author HARUYAMA Seigo Seigo_Haruyama@ecnavi.co.jp
	 */
	private static class TokenCandidate {
		/**
		 * 文字列用バッファ Tokenに渡す際に char[] のほうが都合がよいのでCharBufferで実装.
		 */
		private final transient CharBuffer charBuffer;
		/**
		 * TokenのStartOffset.
		 */
		private final transient int start;
		/**
		 * TokenのendOffset apeendされると拡張する.
		 */
		private transient int end;

		/**
		 * コンストラクタ.
		 *
		 * @param chars
		 *            文字列バッファ
		 * @param offset
		 *            バッファのオフセット
		 * @param length
		 *            文字列長
		 * @param start
		 *            TokenのstartOffset
		 */
		protected TokenCandidate(final char[] chars, final int offset,
				final int length, final int start) {
			charBuffer = CharBuffer.allocate(MAX_WORD_LEN * (MAX_HYPHEN + 1));
			charBuffer.put(chars, offset, length);
			this.start = start;
			end = start + length;
		}

		/**
		 * 文字列を追加する.
		 *
		 * @param chars
		 *            追加する文字列バッファ
		 * @param offset
		 *            バッファのオフセット
		 * @param length
		 *            文字列長
		 */
		private void append(final char[] chars, final int offset,
				final int length) {
			charBuffer.put(chars, offset, length);
			// + 1 はハイフンの分
			end += length + 1;
		}

		/**
		 * ハイフン抜きのTokenを作成する.
		 *
		 * @return ハイフン抜きToken
		 */
		private Token createUnhyphenatedToken() {
			Token token = new Token(charBuffer.array(), charBuffer.position(),
					start, end, TokenType.UNHYPHENATED.name());
			return token;
		}

	}

	/**
	 * キューから次のハイフン抜きTokenを取り出す. 残りTokenが0になったらハイフンの状態をリセット.
	 *
	 * @return ハイフン抜きToken. ない場合は null
	 */
	private Token nextUnhyphenatedToken() {
		// pollは要素がなければ null を返す. 例外は返さない.
		Token result = unhyphenatedQueue.poll();
		if (unhyphenatedQueue.isEmpty()) {
			resetHyphenState();
		} else {
			hyphenState = HyphenState.RETURN;
		}
		return result;
	}

	/**
	 * Tokenから属性を設定する.
	 *
	 * @param token
	 *            トークン情報
	 */
	private void setAttribute(final Token token) {
		setAttribute(token.getBuffer(), token.getLength(), token.getStart(),
				token.getEnd(), token.getType());
	}

	/**
	 * 要素から属性を設定する.
	 *
	 * @param buffer
	 *            文字列buffer
	 * @param length
	 *            長さ
	 * @param start
	 *            開始位置
	 * @param end
	 *            終了位置
	 * @param type
	 *            種類
	 */
	private void setAttribute(final char[] buffer, final int length,
			final int start, final int end, final String type) {
		termAtt.copyBuffer(buffer, 0, length);
		offsetAtt.setOffset(start, end);
		typeAtt.setType(type);
	}

	/**
	 * 文字を1文字読んでないことにする.
	 *
	 * @param c 文字
	 * @throws IOException I/O例外
	 */
	private void unread(int c) throws IOException {
		--offset;
		if (isHankakuMerged) {
			--offset;
		}
		pbinput.unread(c);
		charType = prevCharType;
	}

	/**
	 * 文字をスキャンする.
	 *
	 * @param c スキャンする文字
	 * @return スキャン済み文字
	 * @throws IOException I/O例外
	 */
	private int scanChar(int c) throws IOException {

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
				} else if (Character.isLetterOrDigit(c) || ST.isSign(c)) {
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
		return c;
	}

	/**
	 * TokenType.DOUBLE の場合の処理を行なう.
	 *
	 * @param c 文字
	 * @return 一旦Tokenを確定させるか
	 * @throws IOException I/O例外
	 */
	private boolean processTokenTypeDouble(final int c) throws IOException {
		if (charType == CHARTYPE_DOUBLE) {
			buffer[length++] = (char) c;
			unread(c);
			return true;
		} else if (charType == CHARTYPE_SINGLE) {
			unread(c);
		}
		// 現在のバッファの内容は1文字しかない。
		// 前の文字がDOUBLEである場合は、このまま新しいトークンのスキャンに移行。
		if (concatCharTable.indexOf(c) >= 0) {
			if (concatCharCount++ > 0) {
				if (lastCharType == CHARTYPE_DOUBLE) {
					concatCharCount = 0;
					length = 0;
					tokenType = TokenType.NULL;
					lastCharType = charType;
				} else {
					concatCharCount = 0;
					return true;
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
			return true;
		}
		return false;
	}

	/**
	 * TokenType.DOUBLE の場合の処理を行なう.
	 *
	 * @param c 文字
	 * @return 一旦Tokenを確定させるか
	 * @throws IOException I/O例外
	 */
	private boolean processTokenTypeSingle(final int c) throws IOException {

		if (charType == CHARTYPE_SINGLE) {
			// '...a-a'
			if (hyphenState == HyphenState.NORMAL_HYPHEN
					|| hyphenState == HyphenState.HYPHEN_HYPHEN) {
				// 英数単語の後のハイフンの次の最初の英数で
				// 前の単語をadd
				hyphenState = HyphenState.HYPHEN;
				addUnhyphenatedBuffer(buffer, length, start);
				length = 0;
			}
			buffer[length++] = (char) c;
			if (length >= MAX_WORD_LEN) {
				// バッファに空きがないので、ここで一旦トークンとして切り出す。
				if (tokenCandidates.isEmpty()) {
					resetHyphenState();
				} else {
					addUnhyphenatedBuffer(buffer, length, start);
					hyphenState = HyphenState.RETURN;
				}
				return true;
			}
		} else if (charType == CHARTYPE_DOUBLE) {
			unread(c);
			if (!unhyphenatedQueue.isEmpty()) {
				// '...a-あ'
				// ハイフン処理中なら現在処理中のトークンを返却後ハイフン除去トークン返却
				if (hyphenState == HyphenState.HYPHEN) {
					addUnhyphenatedBuffer(buffer, length, start);
				}
				hyphenState = HyphenState.RETURN;
			}
			return true;
		} else if (c == '-') {
			if (tokenCandidates.size() >= MAX_HYPHEN) {
				if (hyphenState == HyphenState.HYPHEN) {
					// '..a--' 以外の場合はadd
					addUnhyphenatedBuffer(buffer, length, start);
				}
				hyphenState = HyphenState.RETURN;
				return true;
			}
			addUnhyphenatedBuffer(buffer, length, start);

			if (hyphenState == HyphenState.NORMAL) {
				hyphenState = HyphenState.NORMAL_HYPHEN;
			} else if (hyphenState == HyphenState.HYPHEN) {
				hyphenState = HyphenState.HYPHEN_HYPHEN;
			}
			return true;
		} else {
			if (tokenCandidates.isEmpty()) {
				resetHyphenState();
			} else {
				addUnhyphenatedBuffer(buffer, length, start);
				hyphenState = HyphenState.RETURN;
			}
			return true;
		}
		return false;
	}

	/**
	 * 次のトークンを切り出す.
	 *
	 * @return トークン情報があればtrue
	 *
	 * @throws IOException I/O例外
	 */
	@Override
	public boolean incrementToken() throws IOException {

		if (hyphenState == HyphenState.RETURN) {
			// ハイフン除去トークンの返却ステータスの場合は,
			// すべてのハイフン除去トークンを返却するまで返却を続ける
			Token token = nextUnhyphenatedToken();
			if (token == null) {
				resetHyphenState();
			} else {
				setAttribute(token);
				return true;
			}
		}

		length = 0;
		start = offset;
		charType = lastCharType;
		concatCharCount = 0;
		tokenType = TokenType.NULL;

		do {
			// 現在の文字でいわゆる半角カナの濁点・半濁点を結合した場合にのみtrue
			isHankakuMerged = false;
			prevCharType = charType;
			int c = scanChar(pbinput.read());

			// 現在のトークンタイプによって分岐
			if (tokenType.equals(TokenType.NULL)) {
				// 現在スキャン中のトークンなし。
				if (c < 0) {
					// end of input
					if (!unhyphenatedQueue.isEmpty()) {
						hyphenState = HyphenState.RETURN;
						Token token = nextUnhyphenatedToken();
						setAttribute(token);
						return true;
					}
					return false;
				}

				// 文字種によって、トークンタイプを決定
				// 記号は読み飛ばす
				if (charType == CHARTYPE_SINGLE) {
					// '...-a'
					if (hyphenState == HyphenState.NORMAL_HYPHEN
							|| hyphenState == HyphenState.HYPHEN_HYPHEN) {
						hyphenState = HyphenState.HYPHEN;
					}
					start = offset - 1;
					length = 1;
					buffer[0] = (char) c;
					tokenType = TokenType.SINGLE;
				} else if (charType == CHARTYPE_DOUBLE) {
					resetHyphenState();
					start = offset - 1;
					length = 1;
					buffer[0] = (char) c;
					tokenType = TokenType.DOUBLE;
				} else if (c == '-') {
					if (hyphenState == HyphenState.HYPHEN_HYPHEN) {
						// '...-a--' の場合は直前までを返す
						Token token = nextUnhyphenatedToken();
						setAttribute(token);
						return true;
					} else if (hyphenState == HyphenState.NORMAL_HYPHEN) {
						// '..--' の場合(1度目のハイフン出現で連続)は, reset
						resetHyphenState();
					}
				} else {
					resetHyphenState();
				}
			} else if (tokenType.equals(TokenType.SINGLE)) {
				if (processTokenTypeSingle(c)) {
					break;
				}
			} else if (tokenType.equals(TokenType.DOUBLE)) {
				if (processTokenTypeDouble(c)) {
					break;
				}
			}

		} while (true);

		lastCharType = charType;

		setAttribute(buffer, length, start, start + length + concatCharCount
				+ hankakuOffset, tokenType.name());

		offset += hankakuOffset;

		// 最後の文字でいわゆる半角カナの濁点・半濁点が結合されている場合は
		// オフセットを残す
		if (isHankakuMerged) {
			hankakuOffset = 1;
		} else {
			hankakuOffset = 0;
		}
		return true;
	}

	/**
	 * ハイフン抜きの文字列を追加する.
	 *
	 * @param buffer
	 *            文字列バッファ
	 * @param length
	 *            文字列長
	 * @param start
	 *            TokenのstartOffset
	 */
	private void addUnhyphenatedBuffer(final char[] buffer, final int length,
			final int start) {

		int s = tokenCandidates.size();
		for (int i = 0; i < s; ++i) {
			TokenCandidate tmp = tokenCandidates.get(i);
			tmp.append(buffer, 0, length);

			unhyphenatedQueue.add(tmp.createUnhyphenatedToken());
		}

		tokenCandidates.add(new TokenCandidate(buffer, 0, length, start));

	}

	/**
	 * ひらがなカナカナの小文字を大文字にする. それ以外の文字はそのまま.
	 *
	 * @param c
	 *            文字
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
	 * @param c1
	 *            変換前の文字
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
	 * @param c1
	 *            変換前の文字
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
	 * デバッグ用 doCapitalizeKana Setter.
	 *
	 * @param doCapitalizeKana
	 *            doCapitalizeKana のデバッグ値
	 */
	public static void setDoCapitalizeKana(final boolean doCapitalizeKana) {
		ECNaviTokenizer.doCapitalizeKana = doCapitalizeKana;
	}

	/**
	 * 最後のオフセットを指定する.
	 */
	@Override
	public void end() {
		this.offsetAtt.setOffset(offset, offset);
	}

	/**
	 * Tokenizerを再利用する.
	 *
	 * @param reader
	 *            新しいreader
	 *
	 * @throws IOException
	 *             I/O例外
	 */
	@Override
	public void reset(final Reader reader) throws IOException {
		super.reset(reader);
		init(reader);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ECNaviTokenizer other = (ECNaviTokenizer) obj;
		if (offsetAtt == null) {
			if (other.offsetAtt != null)
				return false;
		} else if (!offsetAtt.equals(other.offsetAtt))
			return false;
		if (termAtt == null) {
			if (other.termAtt != null)
				return false;
		} else if (!termAtt.equals(other.termAtt))
			return false;
		if (typeAtt == null) {
			if (other.typeAtt != null)
				return false;
		} else if (!typeAtt.equals(other.typeAtt))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((offsetAtt == null) ? 0 : offsetAtt.hashCode());
		result = prime * result + ((termAtt == null) ? 0 : termAtt.hashCode());
		result = prime * result + ((typeAtt == null) ? 0 : typeAtt.hashCode());
		return result;
	}
}
