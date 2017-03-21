import sys
import re
from optparse import OptionParser

usage = "Need to fill this out."

parser = OptionParser(usage=usage)
parser.add_option("-u", "--unique-id", type="string", help="Unique identifier for each sentence.", default="## Tweet", dest="UniqueID")
(options, args) = parser.parse_args()
UniqueID = options.UniqueID

annot_file = open(sys.argv[1] + '.annotate', "r", encoding='utf-8').readlines()
clusters5_file = open(sys.argv[1] + '.cluster5', "r", encoding='utf-8').readlines()
jerboa_file = open(sys.argv[1]+ '.jerboa', "r", encoding='utf-8').readlines()
clusters3_file = open(sys.argv[1]+ '.cluster3', "r", encoding='utf-8').readlines()
sentiment_file = open(sys.argv[1]+ '.sentiment', "r", encoding='utf-8').readlines()
postag_file = open(sys.argv[1]+ '.postag', "r", encoding='utf-8').readlines()

output_file=open(sys.argv[2], 'w', encoding='utf-8')

if len(annot_file) == len(jerboa_file) == len(sentiment_file) == len(clusters5_file) == len(clusters3_file):
    pass
else:
    sys.stderr.write("Error:  Features files don't have same number of lines.\n")
    sys.stderr.write("Annotation file: " + str(len(annot_file)) + " Clusters files: " + str(len(clusters5_file)) + " "+ str(len(clusters3_file)) + " Jerboa file:" + str(len(jerboa_file)) + " Sentiment file:" + str(len(sentiment_file)) + "\n")
    sys.exit()

x = 0
index = 0
Start = True
sentence_id = ''
while x < len(annot_file):
    line = annot_file[x].strip()
    if line.startswith(UniqueID):
        #print (line)
        sentence_id = line[9:]
        xxxx = 1
    elif line == "":
        output_file.write('\n')
        index += 1
        Start = True
    else:
        if Start:
            output_file.write('## Tweet ' + sentence_id + '\n')
            Start = False
        split_line = line.split("\t")
        cluster5 = clusters5_file[x].strip()
        cluster3 = clusters3_file[x].strip()
        jerb = jerboa_file[x].strip()
        postag = postag_file[x].strip()
        if jerb == "null":
            jerb = "NONE"
        rada_ther_sent = sentiment_file[x].strip()
        sent_split = rada_ther_sent.split()
        rada_sent = sent_split[0]
        ther_sent = sent_split[1]
        output = 'O' if split_line[1][0] == 'O'else split_line[1][0] + '-' + split_line[-1]
        output_file.write(split_line[0] + "\t" + split_line[1] + "\t" + cluster5 + "\t" + jerb + "\t" + cluster3 + "\t" + rada_sent + "\t" + ther_sent + "\t" + split_line[-1] + "\t" + postag + "\t" + output + '\n')
    x += 1


output_file.close()