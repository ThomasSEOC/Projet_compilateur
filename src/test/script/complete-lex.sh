#! /bin/sh

# Auteur : gl54
# Version initiale : 01/01/2022

# Base pour un script de test de la lexicographie.
# On teste un fichier valide et un fichier invalide.
# Il est conseillé de garder ce fichier tel quel, et de créer de
# nouveaux scripts (en s'inspirant si besoin de ceux fournis).

# Il faudrait améliorer ce script pour qu'il puisse lancer test_lex
# sur un grand nombre de fichiers à la suite.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

for i in ./src/test/deca/syntax/valid/lexer/*.deca
do
echo "$i"
LIS="${i%.*}.lis"
RES=$(test_lex "$i" 2>&1 | diff - "$LIS")
if [ "$RES" != "" ]
then
  echo "-> ERROR"
else
  echo "-> OK"
fi
done

for i in ./src/test/deca/syntax/invalid/lexer/*.deca
do
echo "$i"
LIS="${i%.*}.lis"
RES=$(test_lex "$i" 2>&1 | diff - "$LIS")
if [ "$RES" != "" ]
then
  echo "-> ERROR : $RES"
else
  echo "-> OK"
fi
done

## /!\ test valide lexicalement
#if test_lex src/test/deca/syntax/valid/lexer/multi-hello.deca 2>&1 \
#    |  grep -q 'INFO'
#then
#    echo "Echec inattendu de test_lex multi-hello"
#    exit 1
#else
#    echo "OK pour test_lex multi-hello"
#fi

## /!\ test valide lexicalement
#if test_lex src/test/deca/syntax/valid/lexer/concatenation.deca 2>&1 \
#    |  grep -q 'INFO'
#then
#    echo "Echec inattendu de test_lex concatenation"
#    exit 1
#else
#    echo "OK pour test_lex concatenation"
#fi
#
#
#
## /!\ test invalide lexicalement
#if test_lex src/test/deca/syntax/invalid/lexer/multi-string.deca 2>&1 \
#    | grep -q ' no token, no LocationException'
#then
#    echo "Echec attendu de test_lex multi-string"
#else
#    echo "Réussite inattendue de test_lex multi-string"
#    exit 1
#fi

exit 0

