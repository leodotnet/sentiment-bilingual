import sys
import re
from sentiment import Sentiment
from optparse import OptionParser

#words = sys.stdin.readlines()

usage = "Need to fill this out."
parser = OptionParser(usage=usage)
parser.add_option("-u", "--unique-id", type="string", help="Unique identifier for each sentence.", default="## Tweet", dest="UniqueID")
parser.add_option("-l", "--language", type="string", help="Sentiment language.", default="es", dest="langa")
parser.add_option("-i", "--input", type="string", help="input.", default="tweet.en.data.train.annotate", dest="inputfilename")
parser.add_option("-o", "--output", type="string", help="output.", default="tweet.en.data.train.word", dest="outputfilename")
(options, args) = parser.parse_args()
(options, args) = parser.parse_args()
UniqueID = options.UniqueID
langa = options.langa
inputfilename = options.inputfilename
outputfilename = options.outputfilename

#print(inputfilename, outputfilename)

input_file = open(inputfilename, 'r', encoding='utf-8')
output_file = open(outputfilename, 'w', encoding='utf-8')
output_file_bc = open(outputfilename + '.bc', 'w', encoding='utf-8')
#s = Sentiment(langa)

line_num = 0
instance_id = 0
instance = []
for word in input_file:
    line_num += 1
    word = word.strip()
    if word.startswith(UniqueID):
        output_file.write('##\n')
        continue
    elif word == "":
        output_file.write('\n')
        output_file_bc.write(' '.join(instance) + '\n')
        #print(instance_id)
        instance_id += 1
        instance = []
    else:
        #print(word)
        split_line = word.split('\t')
        #print(str(split_line) + ' ' + str(len(split_line)))
        #if len(split_line) > 1:
        #    sys.stderr.write("\n\nPossible error on line + " + str(line_num) + ":  Expected a single word, but found " + word + ".\n")
        word = split_line[0]
        output_file.write(word + '\n')

        if (word.startswith('http://') or word.startswith('https://')):
            word = '<URL>'
        elif word.startswith('@') and len(word) > 1:
            word = '<USERNAME>'
        instance.append(word)
        #output_file_bc.write(word + '\n')


input_file.close()
output_file.close()
output_file_bc.close()
