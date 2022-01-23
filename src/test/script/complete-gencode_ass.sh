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

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

RESULT=1

echo "TESTS DE CODEGEN VALIDES :"
if [ "$(ls -p ./src/test/deca/codegen/valid/ | grep -v /)" != "" ]
then
  for i in ./src/test/deca/codegen/valid/*.deca
  do
  FILENAME=$(basename -- $i)
  REF_ASS="./src/test/deca/codegen/valid/result/${FILENAME%.*}.ass"
  ASS="${i%.*}.ass"
  if [ -f "$REF_ASS" ]
  then
    decac "$i" || exit 1
    echo "TEST: $i"
    RES=$(diff "$REF_ASS" "$ASS")
    if [ "$RES" != "" ]
    then
      echo "-> ERROR"
      echo "$RES"
      RESULT=0
    else
      echo "-> OK"
    fi
    rm -f "$ASS" 2>/dev/null
#  else
    #  echo "Fichier .ass non généré."
    #  exit 1
  fi
  done
else
  echo "AUCUN TEST TROUVE"
fi

echo "TESTS DE CODEGEN PERF :"
if [ "$(ls -p ./src/test/deca/codegen/perf/ | grep -v /)" != "" ]
then
  for i in ./src/test/deca/codegen/perf/*.deca
    do
    FILENAME=$(basename -- $i)
    REF_ASS="./src/test/deca/codegen/perf/result/${FILENAME%.*}.ass"
    ASS="${i%.*}.ass"
    if [ -f "$REF_ASS" ]
    then
      decac "$i" || exit 1
      echo "TEST: $i"
      RES=$(diff "$REF_ASS" "$ASS")
      if [ "$RES" != "" ]
      then
        echo "-> ERROR"
        echo "$RES"
        RESULT=0
      else
        echo "-> OK"
      fi
      rm -f "$ASS" 2>/dev/null
  #  else
      #  echo "Fichier .ass non généré."
      #  exit 1
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

