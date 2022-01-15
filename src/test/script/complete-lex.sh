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

RESULT=1

echo "TESTS DU LEXER VALIDES :"
if [ "$(ls ./src/test/deca/syntax/valid/lexer/)" != "" ]
then
  for i in ./src/test/deca/syntax/valid/lexer/*.deca
  do
  LIS="${i%.*}.lis"
  if [ -f "$LIS" ]; then
    echo "TEST: $i"
    RES=$(test_lex "$i" 2>&1 | diff - "$LIS")
    if [ "$RES" != "" ]
    then
      echo "-> ERROR : $RES"
      RESULT=0
    else
      echo "-> OK"
    fi
  fi
  done
else
  echo "AUCUN TEST TROUVE"
fi

echo "TESTS DU LEXER INVALIDES :"
if [ "$(ls ./src/test/deca/syntax/invalid/lexer/)" != "" ]
then
  for i in ./src/test/deca/syntax/invalid/lexer/*.deca
  do
  LIS="${i%.*}.lis"
  if [ -f "$LIS" ]; then
    echo "TEST: $i"
    RES=$(test_lex "$i" 2>&1 | diff - "$LIS")
    if [ "$RES" != "" ]
    then
      echo "-> ERROR : $RES"
      RESULT=0
    else
      echo "-> OK"
    fi
  fi
  done
else
  echo "AUCUN TEST TROUVE"
fi

if [ "$RESULT" = 0 ]
then
  exit 1
else
  exit 0
fi

