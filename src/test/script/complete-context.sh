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

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;36m'
YELLOW='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}TESTS CONTEXT VALIDES :${NC}"
if [ "$(ls ./src/test/deca/context/valid/)" != "" ]
then
  for i in ./src/test/deca/context/valid/*.deca
  do
  LIS="${i%.*}.lis"
  if [ -f "$LIS" ]; then
    echo "TEST: $i"
    RES=$(test_context "$i" 2>&1 | diff - "$LIS")
    if [ "$RES" != "" ]
    then
      echo -e "${RED}-> ERROR : ${NC}"
      echo -e "${YELLOW}$RES${NC}"
      RESULT=0
    else
      echo -e "${GREEN}-> OK${NC}"
    fi
  fi
  done
else
  echo -e "${YELLOW}AUCUN TEST TROUVE${NC}"
fi

echo -e "${BLUE}TESTS CONTEXT INVALIDES :${NC}"

if [ "$(ls ./src/test/deca/context/invalid/)" != "" ]
then
  for i in ./src/test/deca/context/invalid/*.deca
  do
  LIS="${i%.*}.lis"
  if [ -f "$LIS" ]; then
    echo "TEST: $i"
    RES=$(test_context "$i" 2>&1 | diff - "$LIS")
    if [ "$RES" != "" ]
    then
      echo -e "${RED}-> ERROR : ${NC}"
      echo -e "${YELLOW}$RES${NC}"
      RESULT=0
    else
      echo -e "${GREEN}-> OK${NC}"
    fi
  fi
  done
else
  echo -e "${BLUE}AUCUN TEST TROUVE${NC}"
fi

if [ "$RESULT" = 0 ]
then
  exit 1
else
  exit 0
fi

