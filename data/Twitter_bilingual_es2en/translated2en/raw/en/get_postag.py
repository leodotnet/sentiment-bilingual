import sys
import re

from optparse import OptionParser
import nltk
from nltk.corpus import cess_esp as cess
from nltk import UnigramTagger as ut
from nltk import BigramTagger as bt
from nltk.tag.perceptron import PerceptronTagger



usage = "Need to fill this out."
parser = OptionParser(usage=usage)
parser.add_option("-u", "--unique-id", type="string", help="Unique identifier for each sentence.", default="##", dest="UniqueID")
parser.add_option("-l", "--language", type="string", help="Sentiment language.", default="es", dest="langa")
parser.add_option("-i", "--input", type="string", help="input.", default="tweet.en.data.train.annotate", dest="inputfilename")
parser.add_option("-o", "--output", type="string", help="output.", default="tweet.en.data.train.word", dest="outputfilename")
(options, args) = parser.parse_args()
UniqueID = options.UniqueID
langa = options.langa
inputfilename = options.inputfilename
outputfilename = options.outputfilename

#print(inputfilename, outputfilename)

input_file = open(inputfilename, 'r', encoding='utf-8')
output_file = open(outputfilename, 'w', encoding='utf-8')

if langa == "es":
    cess_sents = cess.tagged_sents()
    uni_tag = ut(cess_sents)
elif langa == 'du':
    tagger = PerceptronTagger(load=False)
    tagger.load('model.perc.dutch_tagger_small.pickle')
#uni_tag.tag(sentence.split(" "))
#train = int(len(cess_sents)*90/100)
#bi_tag = bt(cess_sents[:train])
#bi_tag.evaluate(cess_sents[train+1:])
#bi_tag.tag(sentence.split(" "))

def postagging(sentence):

    if langa == "es":
        return uni_tag.tag(sentence)
    elif langa == 'en':
        return nltk.pos_tag(sentence)
    elif langa == 'du':
        return tagger.tag(sentence)
    else:
        tags = []
        for item in sentence:
            tags.append('_')
        return tags



line_num = 0
sentence = []
for word in input_file:
    line_num += 1
    word = word.strip()
    if word.startswith(UniqueID):
        output_file.write('##\n')
        sentence = []
        #continue
    elif word == "":
        #print(sentence)
        postags = postagging(sentence)
        for item in postags:
            output_file.write(item[1] + '\n')
        output_file.write('\n')
        sentence = []
    else:
        sentence.append(word)
        #if len(split_line) > 1:
        #    sys.stderr.write("\n\nPossible error on line + " + str(line_num) + ":  Expected a single word, but found " + word + ".\n")
        #word = split_line[0]


if len(sentence) > 0:
    #print(sentence)
    postags = postagging(sentence)
    for item in postags:
        output_file.write(item[1] + '\n')
    output_file.write('\n')
    sentence = []

        
