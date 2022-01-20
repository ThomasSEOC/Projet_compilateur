#! /bin/sh

# blablabla

echo "TESTS CONTEXT VALIDES :"
if [ "$(ls ./src/test/deca/context/valid/)" != "" ]
then
  for i in ./src/test/deca/context/valid/*.deca
  do
  LIS="${i%.*}.lis"
  test_context "$i" 2> "$LIS" 1> "$LIS"
  echo "LIS GENERATED FOR $I"
  done
else
  echo "AUCUN TEST TROUVE"
fi

echo "TESTS CONTEXT INVALIDES :"
if [ "$(ls ./src/test/deca/context/invalid/)" != "" ]
then
  for i in ./src/test/deca/context/invalid/*.deca
  do
  LIS="${i%.*}.lis"
  test_context "$i" 2> "$LIS" 1> "$LIS"
  echo "LIS GENERATED FOR $I"
  done
else
  echo "AUCUN TEST TROUVE"
fi
