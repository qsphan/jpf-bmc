#!/bin/bash

TMPFILE=$(pwd)'/tmp.txt'
CLASSPATH=$(pwd)'/src/examples/classic'

# delete old results
if [ -f resultBubble.txt ]
  then
    rm  resultBubble.txt
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
    		echo "SPF time in miliseconds: ${M}" >> resultBubble.txt
    		;;
	"2")
    		echo "JSBMC time in miliseconds: ${M}" >> resultBubble.txt
    		;;
	"3")
    		echo "JCBMC (D = 10) time in miliseconds: ${M}" >> resultBubble.txt
    		;;
	"4")
    		echo "JCBMC (D = 200) time in miliseconds: ${M}" >> resultBubble.txt
    		;;
	esac
	
}

# test a single .jpf file with SPF, JSBMC, JCBMC 
testJPF()
{
	# This should be the .jpf file
	conf=$1

        echo $conf >> resultBubble.txt

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

	echo '' >> resultBubble.txt
}

testSPF()
{
	# This should be the .jpf file
	conf=$1

        #echo $conf >> resultBubble.txt

        # First step: test SPF with z3
        replace 'listener.*' '#listener' $conf
	replace 'symbolic.dp.*' 'symbolic.dp = z3' $conf
	replace 'search.multiple_errors.*' 'search.multiple_errors = false' $conf
	runJPF $conf 1
}

bubble=$CLASSPATH'/BubbleSort.java'
negated=$CLASSPATH'/BubbleSortAssertionNegated.java'

testAll()
{
	echo 'Test BubbleSort n = 5' >> resultBubble.txt
	replace 'static int N =.*;' 'static int N = 5;' $bubble
	./compile
	testJPF $CLASSPATH'/BubbleSort.jpf'
	echo '' >> resultBubble.txt

	echo 'Test BubbleSort n = 6' >> resultBubble.txt
	replace 'static int N =.*;' 'static int N = 6;' $bubble
	./compile
	testJPF $CLASSPATH'/BubbleSort.jpf'
	echo '' >> resultBubble.txt

	echo 'Test BubbleSortAssertionNegated n = 6' >> resultBubble.txt
	replace 'static int N =.*;' 'static int N = 6;' $negated
	./compile
	testJPF $CLASSPATH'/BubbleSortAssertionNegated.jpf'
	echo '' >> resultBubble.txt

	echo 'Test BubbleSortAssertionNegated n = 30' >> resultBubble.txt
	replace 'static int N =.*;' 'static int N = 30;' $negated
	./compile
	testJPF $CLASSPATH'/BubbleSortAssertionNegated.jpf'
	echo '' >> resultBubble.txt

	echo 'Test BubbleSortAssertionNegated n = 100' >> resultBubble.txt
	replace 'static int N =.*;' 'static int N = 100;' $negated
	./compile
	testJPF $CLASSPATH'/BubbleSortAssertionNegated.jpf'
	echo '' >> resultBubble.txt

}

testAll
