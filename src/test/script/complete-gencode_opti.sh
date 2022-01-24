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

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;36m'
YELLOW='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}TESTS DE CODEGEN VALIDES OPTIMISATION NIVEAU 1 :${NC}"
if [ "$(ls -p ./src/test/deca/codegen/valid/ | grep -v /)" != "" ]
then
  for i in ./src/test/deca/codegen/valid/*.deca
  do
  FILENAME=$(basename -- $i)
  REF_IMA="./src/test/deca/codegen/valid/result/${FILENAME%.*}.ima"
  ASS="${i%.*}.ass"
  IMA="${i%.*}.ima"
  if [ -f "$REF_IMA" ]
  then
    echo "TEST: $i"
    if [ "$#" = 1 ]
    then
      decac -O1 "$1" "$i" || exit 1
    else
      decac -O1 "$i" || exit 1
    fi
    ima "$ASS" 2>"$IMA" 1>"$IMA"
    RES=$(diff "$REF_IMA" "$IMA")
    if [ "$RES" != "" ]
    then
      echo -e "${RED}-> ERROR${NC}"
      echo -e "${YELLOW}$RES${NC}"
      RESULT=0
    else
      echo -e "${GREEN}-> OK${NC}"
    fi
    rm -f "$ASS" 2>/dev/null
    rm -f "$IMA" 2>/dev/null
#  else
    #  echo "Fichier .ass non généré."
    #  exit 1
  fi
  done
else
  echo -e "${BLUE}AUCUN TEST TROUVE${NC}"
fi

echo -e "${BLUE}TESTS DE CODEGEN VALIDES OPTIMISATION NIVEAU 2 :${NC}"
if [ "$(ls -p ./src/test/deca/codegen/valid/ | grep -v /)" != "" ]
then
  for i in ./src/test/deca/codegen/valid/*.deca
  do
  FILENAME=$(basename -- $i)
  REF_IMA="./src/test/deca/codegen/valid/result/${FILENAME%.*}.ima"
  ASS="${i%.*}.ass"
  IMA="${i%.*}.ima"
  if [ -f "$REF_IMA" ]
  then
    echo "TEST: $i"
    if [ "$#" = 1 ]
    then
      decac -O2 "$1" "$i" || exit 1
    else
      decac -O2 "$i" || exit 1
    fi
    ima "$ASS" 2>"$IMA" 1>"$IMA"
    RES=$(diff "$REF_IMA" "$IMA")
    if [ "$RES" != "" ]
    then
      echo -e "${RED}-> ERROR${NC}"
      echo -e "${YELLOW}$RES${NC}"
      RESULT=0
    else
      echo -e "${GREEN}-> OK${NC}"
    fi
    rm -f "$ASS" 2>/dev/null
    rm -f "$IMA" 2>/dev/null
#  else
    #  echo "Fichier .ass non généré."
    #  exit 1
  fi
  done
else
  echo -e "${BLUE}AUCUN TEST TROUVE${NC}"
fi

#echo -e "${BLUE}TESTS DE CODEGEN PERF :${NC}"
#if [ "$(ls -p ./src/test/deca/codegen/perf/ | grep -v /)" != "" ]
#then
#  for i in ./src/test/deca/codegen/perf/*.deca
#    do
#    FILENAME=$(basename -- $i)
#    REF_ASS="./src/test/deca/codegen/perf/result/${FILENAME%.*}.ass"
#    ASS="${i%.*}.ass"
#    if [ -f "$REF_ASS" ]
#    then
#      decac "$i" || exit 1
#      echo "TEST: $i"
#      RES=$(diff "$REF_ASS" "$ASS")
#      if [ "$RES" != "" ]
#      then
#        echo "-> ERROR"
#        echo "$RES"
#        RESULT=0
#      else
#        echo "-> OK"
#      fi
#      rm -f "$ASS" 2>/dev/null
#  #  else
#      #  echo "Fichier .ass non généré."
#      #  exit 1
#    fi
#    done
#else
#  echo "AUCUN TEST TROUVE"
#fi
#
#if [ "$RESULT" = 0 ]
#then
#  exit 1
#else
#  exit 0
#fi

