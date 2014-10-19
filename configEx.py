import os
import subprocess
import glob

z3 = 'z3=' + subprocess.Popen(['which', 'z3'], stdout=subprocess.PIPE).stdout.readline()

def editPath(conf):
	with open(conf,'r') as f:
		newlines = []
		for line in f.readlines():
			if 'z3=' in line:
				line = z3
        		newlines.append(line)
	with open(conf, 'w') as f:
    		for line in newlines:
        		f.write(line)

curdir = os.getcwd()

array_conf = glob.glob(curdir + '/src/examples/*.jpf')

for conf in array_conf:
	editPath (conf)

array_conf = glob.glob(curdir + '/src/examples/classic/*.jpf')

for conf in array_conf:
	editPath (conf)


