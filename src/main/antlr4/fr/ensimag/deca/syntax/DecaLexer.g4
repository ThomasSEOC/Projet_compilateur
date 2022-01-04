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
OPARENT:            '('; // parenthèse ouvrante
CPARENT:            ')'; // parenthèse ouvrante
OBRACE:             '{'; // crochet ouvrabt
CBRACE:             '}'; // crochet fermant

PRINTLN:            'println'; // instruction println

SCOL:               ';'; // point virgule
ESPACE:             ' '; // espace
COL:                ','; // espace
TAB:                '\t'; // tabulation
EOL:                '\n'; // fin de ligne

LETTRE:             ('a'..'z' | 'A'..'Z'); // lettres
fragment CHIFFRE:   '0' .. '9'; // chiffres
NOMBRE:             CHIFFRE+; // nombres composés de chiffres
SPECIAL_CAR:        '!' | ESPACE | SCOL | COL;
STRING_CAR:         LETTRE | NOMBRE | SPECIAL_CAR;
STRING:             '"' (STRING_CAR | '\\"' | '\\\\')* '"';

UNKNOWN:            .;