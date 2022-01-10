// Generated from /user/2/dijst/gl54/src/main/antlr4/fr/ensimag/deca/syntax/DecaLexer.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DecaLexer extends AbstractDecaLexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ASM=1, CLASS=2, EXTENDS=3, IF=4, ELSE=5, TRUE=6, FALSE=7, INSTANCEOF=8, 
		NEW=9, NULL=10, READINT=11, READFLOAT=12, PRINT=13, PRINTLN=14, PRINTX=15, 
		PRINTLNX=16, PROTECTED=17, RETURN=18, THIS=19, WHILE=20, ESPACE=21, EOL=22, 
		TAB=23, CR=24, FORMAT=25, WS=26, IDENT=27, LT=28, GT=29, EQUALS=30, PLUS=31, 
		MINUS=32, TIMES=33, SLASH=34, PERCENT=35, DOT=36, COMMA=37, OPARENT=38, 
		CPARENT=39, OBRACE=40, CBRACE=41, EXCLAM=42, SEMI=43, OR=44, AND=45, EQEQ=46, 
		NEQ=47, LEQ=48, GEQ=49, INT=50, FLOAT=51, STRING=52, MULTI_LINE_STRING=53, 
		COMMENT=54, MULTI_LINE_COMMENT=55, INCLUDE=56;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"EPS", "ASM", "CLASS", "EXTENDS", "IF", "ELSE", "TRUE", "FALSE", "INSTANCEOF", 
			"NEW", "NULL", "READINT", "READFLOAT", "PRINT", "PRINTLN", "PRINTX", 
			"PRINTLNX", "PROTECTED", "RETURN", "THIS", "WHILE", "ESPACE", "EOL", 
			"TAB", "CR", "FORMAT", "WS", "LETTER", "DIGIT", "IDENT", "LT", "GT", 
			"EQUALS", "PLUS", "MINUS", "TIMES", "SLASH", "PERCENT", "DOT", "COMMA", 
			"OPARENT", "CPARENT", "OBRACE", "CBRACE", "EXCLAM", "SEMI", "OR", "AND", 
			"EQEQ", "NEQ", "LEQ", "GEQ", "POSITIVE_DIGIT", "INT", "NUM", "SIGN", 
			"EXP", "DEC", "FLOATDEC", "DIGITHEX", "NUMHEX", "FLOATHEX", "FLOAT", 
			"STRING_CAR", "STRING", "MULTI_LINE_STRING", "COMMENT", "MULTI_LINE_COMMENT", 
			"FILENAME", "INCLUDE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'asm'", "'class'", "'extends'", "'if'", "'else'", "'true'", "'false'", 
			"'instanceof'", "'new'", "'null'", "'readInt'", "'readFloat'", "'print'", 
			"'println'", "'printx'", "'printlnx'", "'protected'", "'return'", "'this'", 
			"'while'", "' '", "'\n'", "'\t'", "'\r'", null, null, null, "'<'", "'>'", 
			"'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'.'", "','", "'('", "')'", 
			"'{'", "'}'", "'!'", "';'", "'||'", "'&&'", "'=='", "'!='", "'<='", "'>='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ASM", "CLASS", "EXTENDS", "IF", "ELSE", "TRUE", "FALSE", "INSTANCEOF", 
			"NEW", "NULL", "READINT", "READFLOAT", "PRINT", "PRINTLN", "PRINTX", 
			"PRINTLNX", "PROTECTED", "RETURN", "THIS", "WHILE", "ESPACE", "EOL", 
			"TAB", "CR", "FORMAT", "WS", "IDENT", "LT", "GT", "EQUALS", "PLUS", "MINUS", 
			"TIMES", "SLASH", "PERCENT", "DOT", "COMMA", "OPARENT", "CPARENT", "OBRACE", 
			"CBRACE", "EXCLAM", "SEMI", "OR", "AND", "EQEQ", "NEQ", "LEQ", "GEQ", 
			"INT", "FLOAT", "STRING", "MULTI_LINE_STRING", "COMMENT", "MULTI_LINE_COMMENT", 
			"INCLUDE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}




	public DecaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DecaLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 26:
			WS_action((RuleContext)_localctx, actionIndex);
			break;
		case 66:
			COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 67:
			MULTI_LINE_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 skip(); 
			break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 skip();
			break;
		}
	}
	private void MULTI_LINE_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			 skip();
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2:\u0212\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3"+
		"\31\3\32\3\32\3\33\3\33\3\33\5\33\u0122\n\33\3\34\3\34\5\34\u0126\n\34"+
		"\3\34\3\34\3\35\3\35\3\36\3\36\3\37\6\37\u012f\n\37\r\37\16\37\u0130\3"+
		"\37\6\37\u0134\n\37\r\37\16\37\u0135\3\37\3\37\3\37\6\37\u013b\n\37\r"+
		"\37\16\37\u013c\3\37\6\37\u0140\n\37\r\37\16\37\u0141\3\37\6\37\u0145"+
		"\n\37\r\37\16\37\u0146\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3"+
		"%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3"+
		"\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\7\67\u0182\n\67\f\67\16\67\u0185"+
		"\13\67\5\67\u0187\n\67\38\68\u018a\n8\r8\168\u018b\39\39\39\59\u0191\n"+
		"9\3:\3:\3:\3:\3;\3;\3;\3;\3<\3<\3<\3<\5<\u019f\n<\3<\3<\5<\u01a3\n<\3"+
		"=\3=\5=\u01a7\n=\3>\6>\u01aa\n>\r>\16>\u01ab\3?\3?\3?\3?\5?\u01b2\n?\3"+
		"?\3?\3?\3?\3?\3?\3?\3?\5?\u01bc\n?\3@\3@\5@\u01c0\n@\3A\3A\3B\3B\3B\3"+
		"B\3B\3B\7B\u01ca\nB\fB\16B\u01cd\13B\3B\3B\3C\3C\3C\3C\3C\3C\3C\7C\u01d8"+
		"\nC\fC\16C\u01db\13C\3C\3C\3D\3D\3D\3D\7D\u01e3\nD\fD\16D\u01e6\13D\3"+
		"D\3D\3D\3E\3E\3E\3E\7E\u01ef\nE\fE\16E\u01f2\13E\3E\3E\3E\3E\3E\3F\3F"+
		"\3F\3F\3F\5F\u01fe\nF\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\7G\u020a\nG\fG\16"+
		"G\u020d\13G\3G\3G\3G\3G\4\u01e4\u01f0\2H\3\2\5\3\7\4\t\5\13\6\r\7\17\b"+
		"\21\t\23\n\25\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23\'\24)\25+\26"+
		"-\27/\30\61\31\63\32\65\33\67\349\2;\2=\35?\36A\37C E!G\"I#K$M%O&Q\'S"+
		"(U)W*Y+[,]-_.a/c\60e\61g\62i\63k\2m\64o\2q\2s\2u\2w\2y\2{\2}\2\177\65"+
		"\u0081\2\u0083\66\u0085\67\u00878\u00899\u008b\2\u008d:\3\2\b\4\2C\\c"+
		"|\4\2GGgg\4\2HHhh\4\2CHch\4\2RRrr\5\2\f\f$$))\2\u0225\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2m\3\2\2\2\2\177\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2"+
		"\2\2\2\u0089\3\2\2\2\2\u008d\3\2\2\2\3\u008f\3\2\2\2\5\u0091\3\2\2\2\7"+
		"\u0095\3\2\2\2\t\u009b\3\2\2\2\13\u00a3\3\2\2\2\r\u00a6\3\2\2\2\17\u00ab"+
		"\3\2\2\2\21\u00b0\3\2\2\2\23\u00b6\3\2\2\2\25\u00c1\3\2\2\2\27\u00c5\3"+
		"\2\2\2\31\u00ca\3\2\2\2\33\u00d2\3\2\2\2\35\u00dc\3\2\2\2\37\u00e2\3\2"+
		"\2\2!\u00ea\3\2\2\2#\u00f1\3\2\2\2%\u00fa\3\2\2\2\'\u0104\3\2\2\2)\u010b"+
		"\3\2\2\2+\u0110\3\2\2\2-\u0116\3\2\2\2/\u0118\3\2\2\2\61\u011a\3\2\2\2"+
		"\63\u011c\3\2\2\2\65\u0121\3\2\2\2\67\u0125\3\2\2\29\u0129\3\2\2\2;\u012b"+
		"\3\2\2\2=\u012e\3\2\2\2?\u014a\3\2\2\2A\u014c\3\2\2\2C\u014e\3\2\2\2E"+
		"\u0150\3\2\2\2G\u0152\3\2\2\2I\u0154\3\2\2\2K\u0156\3\2\2\2M\u0158\3\2"+
		"\2\2O\u015a\3\2\2\2Q\u015c\3\2\2\2S\u015e\3\2\2\2U\u0160\3\2\2\2W\u0162"+
		"\3\2\2\2Y\u0164\3\2\2\2[\u0166\3\2\2\2]\u0168\3\2\2\2_\u016a\3\2\2\2a"+
		"\u016d\3\2\2\2c\u0170\3\2\2\2e\u0173\3\2\2\2g\u0176\3\2\2\2i\u0179\3\2"+
		"\2\2k\u017c\3\2\2\2m\u0186\3\2\2\2o\u0189\3\2\2\2q\u0190\3\2\2\2s\u0192"+
		"\3\2\2\2u\u0196\3\2\2\2w\u019e\3\2\2\2y\u01a6\3\2\2\2{\u01a9\3\2\2\2}"+
		"\u01b1\3\2\2\2\177\u01bf\3\2\2\2\u0081\u01c1\3\2\2\2\u0083\u01c3\3\2\2"+
		"\2\u0085\u01d0\3\2\2\2\u0087\u01de\3\2\2\2\u0089\u01ea\3\2\2\2\u008b\u01fd"+
		"\3\2\2\2\u008d\u01ff\3\2\2\2\u008f\u0090\3\2\2\2\u0090\4\3\2\2\2\u0091"+
		"\u0092\7c\2\2\u0092\u0093\7u\2\2\u0093\u0094\7o\2\2\u0094\6\3\2\2\2\u0095"+
		"\u0096\7e\2\2\u0096\u0097\7n\2\2\u0097\u0098\7c\2\2\u0098\u0099\7u\2\2"+
		"\u0099\u009a\7u\2\2\u009a\b\3\2\2\2\u009b\u009c\7g\2\2\u009c\u009d\7z"+
		"\2\2\u009d\u009e\7v\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7p\2\2\u00a0\u00a1"+
		"\7f\2\2\u00a1\u00a2\7u\2\2\u00a2\n\3\2\2\2\u00a3\u00a4\7k\2\2\u00a4\u00a5"+
		"\7h\2\2\u00a5\f\3\2\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a8\7n\2\2\u00a8\u00a9"+
		"\7u\2\2\u00a9\u00aa\7g\2\2\u00aa\16\3\2\2\2\u00ab\u00ac\7v\2\2\u00ac\u00ad"+
		"\7t\2\2\u00ad\u00ae\7w\2\2\u00ae\u00af\7g\2\2\u00af\20\3\2\2\2\u00b0\u00b1"+
		"\7h\2\2\u00b1\u00b2\7c\2\2\u00b2\u00b3\7n\2\2\u00b3\u00b4\7u\2\2\u00b4"+
		"\u00b5\7g\2\2\u00b5\22\3\2\2\2\u00b6\u00b7\7k\2\2\u00b7\u00b8\7p\2\2\u00b8"+
		"\u00b9\7u\2\2\u00b9\u00ba\7v\2\2\u00ba\u00bb\7c\2\2\u00bb\u00bc\7p\2\2"+
		"\u00bc\u00bd\7e\2\2\u00bd\u00be\7g\2\2\u00be\u00bf\7q\2\2\u00bf\u00c0"+
		"\7h\2\2\u00c0\24\3\2\2\2\u00c1\u00c2\7p\2\2\u00c2\u00c3\7g\2\2\u00c3\u00c4"+
		"\7y\2\2\u00c4\26\3\2\2\2\u00c5\u00c6\7p\2\2\u00c6\u00c7\7w\2\2\u00c7\u00c8"+
		"\7n\2\2\u00c8\u00c9\7n\2\2\u00c9\30\3\2\2\2\u00ca\u00cb\7t\2\2\u00cb\u00cc"+
		"\7g\2\2\u00cc\u00cd\7c\2\2\u00cd\u00ce\7f\2\2\u00ce\u00cf\7K\2\2\u00cf"+
		"\u00d0\7p\2\2\u00d0\u00d1\7v\2\2\u00d1\32\3\2\2\2\u00d2\u00d3\7t\2\2\u00d3"+
		"\u00d4\7g\2\2\u00d4\u00d5\7c\2\2\u00d5\u00d6\7f\2\2\u00d6\u00d7\7H\2\2"+
		"\u00d7\u00d8\7n\2\2\u00d8\u00d9\7q\2\2\u00d9\u00da\7c\2\2\u00da\u00db"+
		"\7v\2\2\u00db\34\3\2\2\2\u00dc\u00dd\7r\2\2\u00dd\u00de\7t\2\2\u00de\u00df"+
		"\7k\2\2\u00df\u00e0\7p\2\2\u00e0\u00e1\7v\2\2\u00e1\36\3\2\2\2\u00e2\u00e3"+
		"\7r\2\2\u00e3\u00e4\7t\2\2\u00e4\u00e5\7k\2\2\u00e5\u00e6\7p\2\2\u00e6"+
		"\u00e7\7v\2\2\u00e7\u00e8\7n\2\2\u00e8\u00e9\7p\2\2\u00e9 \3\2\2\2\u00ea"+
		"\u00eb\7r\2\2\u00eb\u00ec\7t\2\2\u00ec\u00ed\7k\2\2\u00ed\u00ee\7p\2\2"+
		"\u00ee\u00ef\7v\2\2\u00ef\u00f0\7z\2\2\u00f0\"\3\2\2\2\u00f1\u00f2\7r"+
		"\2\2\u00f2\u00f3\7t\2\2\u00f3\u00f4\7k\2\2\u00f4\u00f5\7p\2\2\u00f5\u00f6"+
		"\7v\2\2\u00f6\u00f7\7n\2\2\u00f7\u00f8\7p\2\2\u00f8\u00f9\7z\2\2\u00f9"+
		"$\3\2\2\2\u00fa\u00fb\7r\2\2\u00fb\u00fc\7t\2\2\u00fc\u00fd\7q\2\2\u00fd"+
		"\u00fe\7v\2\2\u00fe\u00ff\7g\2\2\u00ff\u0100\7e\2\2\u0100\u0101\7v\2\2"+
		"\u0101\u0102\7g\2\2\u0102\u0103\7f\2\2\u0103&\3\2\2\2\u0104\u0105\7t\2"+
		"\2\u0105\u0106\7g\2\2\u0106\u0107\7v\2\2\u0107\u0108\7w\2\2\u0108\u0109"+
		"\7t\2\2\u0109\u010a\7p\2\2\u010a(\3\2\2\2\u010b\u010c\7v\2\2\u010c\u010d"+
		"\7j\2\2\u010d\u010e\7k\2\2\u010e\u010f\7u\2\2\u010f*\3\2\2\2\u0110\u0111"+
		"\7y\2\2\u0111\u0112\7j\2\2\u0112\u0113\7k\2\2\u0113\u0114\7n\2\2\u0114"+
		"\u0115\7g\2\2\u0115,\3\2\2\2\u0116\u0117\7\"\2\2\u0117.\3\2\2\2\u0118"+
		"\u0119\7\f\2\2\u0119\60\3\2\2\2\u011a\u011b\7\13\2\2\u011b\62\3\2\2\2"+
		"\u011c\u011d\7\17\2\2\u011d\64\3\2\2\2\u011e\u0122\5/\30\2\u011f\u0122"+
		"\5\61\31\2\u0120\u0122\5\63\32\2\u0121\u011e\3\2\2\2\u0121\u011f\3\2\2"+
		"\2\u0121\u0120\3\2\2\2\u0122\66\3\2\2\2\u0123\u0126\5-\27\2\u0124\u0126"+
		"\5\65\33\2\u0125\u0123\3\2\2\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2\2\2"+
		"\u0127\u0128\b\34\2\2\u01288\3\2\2\2\u0129\u012a\t\2\2\2\u012a:\3\2\2"+
		"\2\u012b\u012c\4\62;\2\u012c<\3\2\2\2\u012d\u012f\59\35\2\u012e\u012d"+
		"\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131"+
		"\u0133\3\2\2\2\u0132\u0134\7&\2\2\u0133\u0132\3\2\2\2\u0134\u0135\3\2"+
		"\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0137\3\2\2\2\u0137"+
		"\u0138\7a\2\2\u0138\u013a\3\2\2\2\u0139\u013b\59\35\2\u013a\u0139\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d"+
		"\u013f\3\2\2\2\u013e\u0140\5;\36\2\u013f\u013e\3\2\2\2\u0140\u0141\3\2"+
		"\2\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0144\3\2\2\2\u0143"+
		"\u0145\7&\2\2\u0144\u0143\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0144\3\2"+
		"\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u0149\7a\2\2\u0149"+
		">\3\2\2\2\u014a\u014b\7>\2\2\u014b@\3\2\2\2\u014c\u014d\7@\2\2\u014dB"+
		"\3\2\2\2\u014e\u014f\7?\2\2\u014fD\3\2\2\2\u0150\u0151\7-\2\2\u0151F\3"+
		"\2\2\2\u0152\u0153\7/\2\2\u0153H\3\2\2\2\u0154\u0155\7,\2\2\u0155J\3\2"+
		"\2\2\u0156\u0157\7\61\2\2\u0157L\3\2\2\2\u0158\u0159\7\'\2\2\u0159N\3"+
		"\2\2\2\u015a\u015b\7\60\2\2\u015bP\3\2\2\2\u015c\u015d\7.\2\2\u015dR\3"+
		"\2\2\2\u015e\u015f\7*\2\2\u015fT\3\2\2\2\u0160\u0161\7+\2\2\u0161V\3\2"+
		"\2\2\u0162\u0163\7}\2\2\u0163X\3\2\2\2\u0164\u0165\7\177\2\2\u0165Z\3"+
		"\2\2\2\u0166\u0167\7#\2\2\u0167\\\3\2\2\2\u0168\u0169\7=\2\2\u0169^\3"+
		"\2\2\2\u016a\u016b\7~\2\2\u016b\u016c\7~\2\2\u016c`\3\2\2\2\u016d\u016e"+
		"\7(\2\2\u016e\u016f\7(\2\2\u016fb\3\2\2\2\u0170\u0171\7?\2\2\u0171\u0172"+
		"\7?\2\2\u0172d\3\2\2\2\u0173\u0174\7#\2\2\u0174\u0175\7?\2\2\u0175f\3"+
		"\2\2\2\u0176\u0177\7>\2\2\u0177\u0178\7?\2\2\u0178h\3\2\2\2\u0179\u017a"+
		"\7@\2\2\u017a\u017b\7?\2\2\u017bj\3\2\2\2\u017c\u017d\4\63;\2\u017dl\3"+
		"\2\2\2\u017e\u0187\7\62\2\2\u017f\u0183\5k\66\2\u0180\u0182\5;\36\2\u0181"+
		"\u0180\3\2\2\2\u0182\u0185\3\2\2\2\u0183\u0181\3\2\2\2\u0183\u0184\3\2"+
		"\2\2\u0184\u0187\3\2\2\2\u0185\u0183\3\2\2\2\u0186\u017e\3\2\2\2\u0186"+
		"\u017f\3\2\2\2\u0187n\3\2\2\2\u0188\u018a\5;\36\2\u0189\u0188\3\2\2\2"+
		"\u018a\u018b\3\2\2\2\u018b\u0189\3\2\2\2\u018b\u018c\3\2\2\2\u018cp\3"+
		"\2\2\2\u018d\u0191\5E#\2\u018e\u0191\5G$\2\u018f\u0191\5\3\2\2\u0190\u018d"+
		"\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u018f\3\2\2\2\u0191r\3\2\2\2\u0192"+
		"\u0193\t\3\2\2\u0193\u0194\5q9\2\u0194\u0195\5o8\2\u0195t\3\2\2\2\u0196"+
		"\u0197\5o8\2\u0197\u0198\5O(\2\u0198\u0199\5o8\2\u0199v\3\2\2\2\u019a"+
		"\u019f\5u;\2\u019b\u019c\5u;\2\u019c\u019d\5s:\2\u019d\u019f\3\2\2\2\u019e"+
		"\u019a\3\2\2\2\u019e\u019b\3\2\2\2\u019f\u01a2\3\2\2\2\u01a0\u01a3\t\4"+
		"\2\2\u01a1\u01a3\5\3\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a1\3\2\2\2\u01a3"+
		"x\3\2\2\2\u01a4\u01a7\5;\36\2\u01a5\u01a7\t\5\2\2\u01a6\u01a4\3\2\2\2"+
		"\u01a6\u01a5\3\2\2\2\u01a7z\3\2\2\2\u01a8\u01aa\5y=\2\u01a9\u01a8\3\2"+
		"\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ab\u01ac\3\2\2\2\u01ac"+
		"|\3\2\2\2\u01ad\u01ae\7\62\2\2\u01ae\u01b2\7z\2\2\u01af\u01b0\7\62\2\2"+
		"\u01b0\u01b2\7Z\2\2\u01b1\u01ad\3\2\2\2\u01b1\u01af\3\2\2\2\u01b2\u01b3"+
		"\3\2\2\2\u01b3\u01b4\5{>\2\u01b4\u01b5\5O(\2\u01b5\u01b6\5{>\2\u01b6\u01b7"+
		"\t\6\2\2\u01b7\u01b8\5q9\2\u01b8\u01bb\5o8\2\u01b9\u01bc\t\4\2\2\u01ba"+
		"\u01bc\5\3\2\2\u01bb\u01b9\3\2\2\2\u01bb\u01ba\3\2\2\2\u01bc~\3\2\2\2"+
		"\u01bd\u01c0\5w<\2\u01be\u01c0\5}?\2\u01bf\u01bd\3\2\2\2\u01bf\u01be\3"+
		"\2\2\2\u01c0\u0080\3\2\2\2\u01c1\u01c2\n\7\2\2\u01c2\u0082\3\2\2\2\u01c3"+
		"\u01cb\7$\2\2\u01c4\u01ca\5\u0081A\2\u01c5\u01c6\7^\2\2\u01c6\u01ca\7"+
		"$\2\2\u01c7\u01c8\7^\2\2\u01c8\u01ca\7^\2\2\u01c9\u01c4\3\2\2\2\u01c9"+
		"\u01c5\3\2\2\2\u01c9\u01c7\3\2\2\2\u01ca\u01cd\3\2\2\2\u01cb\u01c9\3\2"+
		"\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01ce\3\2\2\2\u01cd\u01cb\3\2\2\2\u01ce"+
		"\u01cf\7$\2\2\u01cf\u0084\3\2\2\2\u01d0\u01d9\7$\2\2\u01d1\u01d8\5\u0081"+
		"A\2\u01d2\u01d8\5/\30\2\u01d3\u01d4\7^\2\2\u01d4\u01d8\7$\2\2\u01d5\u01d6"+
		"\7^\2\2\u01d6\u01d8\7^\2\2\u01d7\u01d1\3\2\2\2\u01d7\u01d2\3\2\2\2\u01d7"+
		"\u01d3\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d8\u01db\3\2\2\2\u01d9\u01d7\3\2"+
		"\2\2\u01d9\u01da\3\2\2\2\u01da\u01dc\3\2\2\2\u01db\u01d9\3\2\2\2\u01dc"+
		"\u01dd\7$\2\2\u01dd\u0086\3\2\2\2\u01de\u01df\7\61\2\2\u01df\u01e0\7\61"+
		"\2\2\u01e0\u01e4\3\2\2\2\u01e1\u01e3\13\2\2\2\u01e2\u01e1\3\2\2\2\u01e3"+
		"\u01e6\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e4\u01e2\3\2\2\2\u01e5\u01e7\3\2"+
		"\2\2\u01e6\u01e4\3\2\2\2\u01e7\u01e8\5/\30\2\u01e8\u01e9\bD\3\2\u01e9"+
		"\u0088\3\2\2\2\u01ea\u01eb\7\61\2\2\u01eb\u01ec\7,\2\2\u01ec\u01f0\3\2"+
		"\2\2\u01ed\u01ef\13\2\2\2\u01ee\u01ed\3\2\2\2\u01ef\u01f2\3\2\2\2\u01f0"+
		"\u01f1\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f1\u01f3\3\2\2\2\u01f2\u01f0\3\2"+
		"\2\2\u01f3\u01f4\7,\2\2\u01f4\u01f5\7\61\2\2\u01f5\u01f6\3\2\2\2\u01f6"+
		"\u01f7\bE\4\2\u01f7\u008a\3\2\2\2\u01f8\u01fe\59\35\2\u01f9\u01fe\5;\36"+
		"\2\u01fa\u01fe\5O(\2\u01fb\u01fe\5G$\2\u01fc\u01fe\7a\2\2\u01fd\u01f8"+
		"\3\2\2\2\u01fd\u01f9\3\2\2\2\u01fd\u01fa\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd"+
		"\u01fc\3\2\2\2\u01fe\u008c\3\2\2\2\u01ff\u0200\7%\2\2\u0200\u0201\7k\2"+
		"\2\u0201\u0202\7p\2\2\u0202\u0203\7e\2\2\u0203\u0204\7n\2\2\u0204\u0205"+
		"\7w\2\2\u0205\u0206\7f\2\2\u0206\u0207\7g\2\2\u0207\u020b\3\2\2\2\u0208"+
		"\u020a\7\"\2\2\u0209\u0208\3\2\2\2\u020a\u020d\3\2\2\2\u020b\u0209\3\2"+
		"\2\2\u020b\u020c\3\2\2\2\u020c\u020e\3\2\2\2\u020d\u020b\3\2\2\2\u020e"+
		"\u020f\7$\2\2\u020f\u0210\5\u008bF\2\u0210\u0211\7$\2\2\u0211\u008e\3"+
		"\2\2\2\35\2\u0121\u0125\u0130\u0135\u013c\u0141\u0146\u0183\u0186\u018b"+
		"\u0190\u019e\u01a2\u01a6\u01ab\u01b1\u01bb\u01bf\u01c9\u01cb\u01d7\u01d9"+
		"\u01e4\u01f0\u01fd\u020b\5\3\34\2\3D\3\3E\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}