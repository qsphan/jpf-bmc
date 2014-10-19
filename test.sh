#!/bin/bash

TMPFILE=$(pwd)'/tmp.txt'
CLASSPATH=$(pwd)'/src/examples/'

# delete old results
if [ -f result.txt ]
  then
    rm  result.txt
fi

# replace $1 by $2 in file $3
replace()
{
	searchterm=$1
	replaceterm=$2
	sed -e "s/$searchterm/$replaceterm/ig" $3 > $TMPFILE
	mv $TMPFILE $3	
}

runJPF()
{
	conf=$1
        step=$2
	# UNIX timestamp concatenated with nanoseconds
	T="$(date +%s%N)"
	jpf $conf
	T="$(($(date +%s%N)-T))"
        # Milliseconds
	M="$((T/1000000))"
	case $step in
	"1")
    		echo "SPF time in miliseconds: ${M}" >> result.txt
    		;;
	"2")
    		echo "JSBMC time in miliseconds: ${M}" >> result.txt
    		;;
	"3")
    		echo "JCBMC (D = 10) time in miliseconds: ${M}" >> result.txt
    		;;
	"4")
    		echo "JCBMC (D = 200) time in miliseconds: ${M}" >> result.txt
    		;;
	esac
	
}

# test a single .jpf file with SPF, JSBMC, JCBMC 
testJPF()
{
	# This should be the .jpf file
	conf=$1

        echo $conf >> result.txt

	testSPF $conf

	replace 'symbolic.dp.*' 'symbolic.dp = no_solver' $conf
	replace 'search.multiple_errors.*' 'search.multiple_errors = true' $conf

	#Third step: test with JCBMC, bmc.batch=10
	replace '.*listener.*' 'listener = uk.ac.qmul.bmc.concur.BmcConcurListenerUsingSmtlib2' $conf
	replace 'bmc.batch.*' 'bmc.batch = 10' $conf
	runJPF $conf 3

	#Fourth step: test with JCBMC, bmc.batch=200
	replace 'bmc.batch.*' 'bmc.batch = 200' $conf
	runJPF $conf 4

	#Second step: test with JSBMC
	replace 'listener.*' 'listener = uk.ac.qmul.bmc.BmcListenerUsingSmtlib2' $conf
	runJPF $conf 2

	echo '' >> result.txt
}

testSPF()
{
	# This should be the .jpf file
	conf=$1

        #echo $conf >> result.txt

        # First step: test SPF with z3
        replace 'listener.*' '#listener' $conf
	replace 'symbolic.dp.*' 'symbolic.dp = z3' $conf
	replace 'search.multiple_errors.*' 'search.multiple_errors = false' $conf
	runJPF $conf 1
}

testAll()
{
	FILES=$CLASSPATH'*.jpf'
	for f in $FILES
	do
  		testJPF $f
	done
}

#testJPF $CLASSPATH'RedBlackTree.jpf'
#testJPF $CLASSPATH'classic/BubbleSortAssertionNegated.jpf'
testAll
#testJPF $CLASSPATH'ArrayFalse.jpf'
