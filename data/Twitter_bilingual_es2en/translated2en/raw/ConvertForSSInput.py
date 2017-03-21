import sys
import re

from optparse import OptionParser


# words = sys.stdin.readlines()

usage = "Need to fill this out."
parser = OptionParser(usage=usage)
parser.add_option("-u", "--unique-id", type="string", help="Unique identifier for each sentence.", default="##",
                  dest="UniqueID")
parser.add_option("-l", "--language", type="string", help="Sentiment language.", default="en", dest="langa")
parser.add_option("-i", "--input", type="string", help="input.", default="", dest="inputfilename")
#parser.add_option("-o", "--output", type="string", help="output.", default="", dest="outputfilename")

(options, args) = parser.parse_args()
UniqueID = options.UniqueID
langa = options.langa
inputfilename = options.inputfilename
#outputfilename = options.outputfilename



input_file = open(inputfilename, 'r', encoding='utf-8')
output_file = open(langa + '/' +inputfilename + '.annotate', 'w', encoding='utf-8')

line_num = 0
instance_id = 1
instance = list()
for line in input_file:
    line_num += 1
    line = line.strip()
    if line.startswith(UniqueID):
        instance = list()
    elif line == "":
        # print(sentence)
        #instances.append(instance)
        output_file.write('## Tweet ' + str(instance_id) +'\n')
        for token in instance:
            output_file.write(token + '\n')
        output_file.write('\n')
        instance = list()
        instance_id += 1
    else:
        tmp = line.split('\t')
        word = tmp[0]
        tag = tmp[1]

        output = word

        if tag == 'O':
            output += '\t' + 'O' + '\t' + '_'
        else:
            tmp1 = tag.split('-')
            sent = tmp1[1]
            if sent == 'very_positive':
                sent = 'positive'
            elif sent == 'very_negative':
                sent = 'negative'
            output += '\t' + tmp1[0] + '-VOLITIONAL' + '\t' + sent

        instance.append(output)


input_file.close()
output_file.close()

