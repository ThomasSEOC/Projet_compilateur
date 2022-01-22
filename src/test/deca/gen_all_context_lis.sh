#! /bin/sh

# blablabla

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

mvn compile
mvn test-compile

echo "TESTS CONTEXT VALIDES :"
if [ "$(ls ./src/test/deca/context/valid/)" != "" ]
then
  for i in ./src/test/deca/context/valid/*.deca
  do
  LIS="${i%.*}.lis"
  RES=$(test_context "$i" 2>&1)
  # test_context "$i" 2> "$LIS" 1> "$LIS"
  rm "$LIS"
  echo "$RES" > "$LIS"
  echo "LIS GENERATED FOR $i"
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
  #test_context "$i" 2> "$LIS" 1> "$LIS"
  RES=$(test_context "$i" 2>&1)
  # test_context "$i" 2> "$LIS" 1> "$LIS"
  rm "$LIS"
  echo "$RES" > "$LIS"
  echo "LIS GENERATED FOR $i"
  done
else
  echo "AUCUN TEST TROUVE"
fi