lexer grammar DecaLexer;

options {
   language = Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.
//DUMMY_TOKEN: .; // A FAIRE : Règle bidon qui reconnait tous les caractères.
//                // A FAIRE : Il faut la supprimer et la remplacer par les vraies règles.


fragment EPS :       /* Rien */;

//Mots réservés :
ASM:                 'asm';
CLASS:               'class';
EXTENDS:             'extends';
IF:                  'if';
ELSE:                'else';
TRUE:                'true';
FALSE:               'false';
INSTANCEOF:          'instanceof';
NEW:                 'new';
NULL:                'null';
READINT:             'readInt';
READFLOAT:           'readFloat';
PRINT:               'print';
PRINTLN:             'println'; // instruction println
PRINTX:              'printx';
PRINTLNX:            'printlnx';
PROTECTED:           'protected';
RETURN:              'return';
THIS:                'this';
WHILE:               'while';

//Séparateurs : 
ESPACE:              ' '; // espace
EOL:                 '\n'; // fin de ligne
TAB:                 '\t'; // tabulation
CR:                  '\r'; // retour chariot

FORMAT:              (EOL | TAB | CR);
WS  :   ( ESPACE | FORMAT) 
         { skip(); };

//Identificateurs :
fragment LETTER :    ('a'..'z' | 'A'..'Z'); // lettres
fragment DIGIT:    '0' .. '9'; // chiffres
IDENT:               (LETTER + '$' + '_')(LETTER + DIGIT + '$' + '_');

//Symboles spéciaux :
LT:                  '<'; // comparateur 'inférieur à'
GT:                  '>'; // comparateur 'supérieur à'
EQUALS:              '='; // égal
PLUS:                '+'; // plus
MINUS:               '-'; // moins
TIMES:               '*'; // multiplie
SLASH:               '/'; // divise
PERCENT:             '%'; // modulo ou pourcent
DOT:                 '.'; // point
COMMA:               ','; // virgule aussi ?
OPARENT:             '('; // parenthèse ouvrante
CPARENT:             ')'; // parenthèse ouvrante
OBRACE:              '{'; // crochet ouvrant
CBRACE:              '}'; // crochet fermant
EXCLAM:              '!'; // point d'exclamation
SEMI:                ';'; // point virgule aussi ?
OR:                  '||'; // comparateur 'ou'
AND:                 '&&'; // comparateur 'et'
EQEQ:                '=='; // comparateur 'égal'
NEQ:                 '!='; // comparateur 'différent
LEQ:                 '<='; // comparateur 'inférieur ou égal à'
GEQ:                 '>='; // comparateur 'supérieur ou égal à'

//Littéraux entiers :
fragment POSITIVE_DIGIT: '1'..'9';
INT:                 '0' | POSITIVE_DIGIT DIGIT*;

//Littéraux flottants :
fragment NUM:        DIGIT+;
fragment SIGN:       (PLUS | MINUS | EPS);
fragment EXP:        ('E' | 'e') SIGN NUM;
fragment DEC:        NUM DOT NUM; // a voir si ca marche, sinon NUM ',' NUM
fragment FLOATDEC:   (DEC | DEC EXP)('F' | 'f' | EPS);
fragment DIGITHEX:   (DIGIT | 'A'..'F' | 'a'..'f');
fragment NUMHEX:     DIGITHEX+;
fragment FLOATHEX:   ('0x' | '0X') NUMHEX DOT NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' |EPS);
FLOAT :              (FLOATDEC | FLOATHEX);

//Chaines de caractère :
fragment STRING_CAR: ~('"' | '\'' | '\n' ); //caractère d'une chaine de caractères
STRING:              '"' (STRING_CAR | '\\"' | '\\\\')* '"'; //chaine de caractère
MULTI_LINE_STRING:   '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"'; //chaine de caractère sur plusieurs lignes

//Commentaires :
COMMENT:             '//' .*? EOL  
                     { skip();};

MULTI_LINE_COMMENT:  '/*' .*? '*/'  
                     { skip();};



//Inclusion de fichier
fragment FILENAME:   (LETTER | DIGIT | DOT | MINUS | '_');
INCLUDE:             '#include' (' ')* '"' FILENAME '"';


UNKNOWN:            .;

