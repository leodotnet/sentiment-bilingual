import subprocess
import sys

inbound = 2
outbound = 11

filetype='test'

if sys.argv[1]:
    inbound = int(sys.argv[1])

if sys.argv[2]:
    outbound = int(sys.argv[2])

if sys.argv[3]:
    filetype = sys.argv[3]
    

#print("Start stats: [", inbound, ",",outbound, ")")


for i in range(inbound,outbound):
    filename = filetype + '.' + str(i) + '.coll'
    fin = open(filename, "r")
    #fout = open('output//result.' + str(i) + '.out', "w")
    for line in fin:
        line = line.strip('\n')
        print(line)
        #output += '\n'
        #fout.write(output)
    fin.close()
