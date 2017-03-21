import sys
import re
from sentiment import Sentiment
from optparse import OptionParser


usage = "Need to fill this out."
parser = OptionParser(usage=usage)
parser.add_option("-u", "--unique-id", type="string", help="Unique identifier for each sentence.", default="## Tweet", dest="UniqueID")
parser.add_option("-l", "--language", type="string", help="Sentiment language.", default="en", dest="langa")
parser.add_option("-i", "--input", type="string", help="input.", default="tweet.en.data.train.word", dest="inputfilename")
parser.add_option("-o", "--output", type="string", help="output.", default="tweet.en.data.train.sentiment", dest="outputfilename")
(options, args) = parser.parse_args()
(options, args) = parser.parse_args()
UniqueID = options.UniqueID
langa = options.langa
inputfilename = options.inputfilename
outputfilename = options.outputfilename

input_file = open(inputfilename, 'r', encoding='utf-8')
output_file = open(outputfilename, 'w', encoding='utf-8')


s = Sentiment(langa)

line_num = 0
for word in input_file:
    line_num += 1
    word = word.strip()
    #print(word)
    if word.startswith(UniqueID):
        output_file.write(word + '\n')
    elif word == "":
        output_file.write('\n')
    else:
        #print(word)
        split_line = word.split('\t')
        #print(str(split_line) + ' ' + str(len(split_line)))
        #if len(split_line) > 1:
        #    sys.stderr.write("\n\nPossible error on line + " + str(line_num) + ":  Expected a single word, but found " + word + ".\n")
        word = split_line[0].lower()
        previous_polarity = s.get_sentiment_polarity(word)
        ther_sentiment_polarity = s.get_ther_sentiment_polarity(word)
        output_file.write(previous_polarity + "\t" + ther_sentiment_polarity + '\n')

input_file.close()
output_file.close()