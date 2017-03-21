import sys
import re
from collections import defaultdict
from optparse import OptionParser

global UniqueID



usage = "usage: cat words | %prog brown_clusters k [options]"
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


b = open(sys.argv[1], "r", encoding='utf-8')
brown_clusters = b.readlines()
b.close()
k = int(sys.argv[2])

def read_clusters(brown_clusters):
    cluster_hash = {}
    for line in brown_clusters:
        line = line.strip()
        split_line = line.split("\t")
        cluster = split_line[0]
        word = split_line[1]
        cut_cluster = cluster[:k]
        cluster_hash[word] = cut_cluster
    return cluster_hash

def words_to_clusters(words, cluster_hash):
    global UniqueID
    line_num = 0
    for word in words:
        line_num += 1
        word = word.strip()
        if word.startswith(UniqueID):
            output_file.write(word + '\n')
            continue
        elif word == "":
            output_file.write('\n')
        else:
            split_line = word.split('\t')
            #if len(split_line) > 1:
            #    sys.stderr.write("\n\nPossible error on line + " + str(line_num) + ":  Expected a single word, but found " + word + ".\n")
            word = split_line[0]
            try:
                cluster = cluster_hash[word]
            except KeyError:
                cluster = "NONE"
            output_file.write(cluster + '\n')


cluster_hash = read_clusters(brown_clusters)
words_to_clusters(input_file, cluster_hash)
