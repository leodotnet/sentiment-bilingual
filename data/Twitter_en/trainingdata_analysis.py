import sys
import re
from copy import copy
from collections import defaultdict
from optparse import OptionParser

parser = OptionParser(usage="%prog test predictions [OPTIONS]")
parser.add_option("-l", "--linear", dest="linear", action="store_true", default=False,
                  help="Predictions are in linear format")
parser.add_option("-c", "--with-confidence",
                  action="store_true", dest="with_confidence", default=False,
                  help="Print out posteriors")
parser.add_option("-v", "--verbose", action="store_true", dest="verbose", default=False)
parser.add_option("-t", "--top-5", action="store", dest="top_5", default=False)
(options, args) = parser.parse_args()


global wanted_NEs

wanted_NEs = ("B", "I", "B_VOLITIONAL", "I_VOLITIONAL", "B_ORGANIZATION", "I_ORGANIZATION", "B_PERSON", "I_PERSON", "Bsentiment", "Bpositive", "Bnegative", "Bneutral", "I", "Inegative", "Ipositive", "Isentiment", "Ineutral")


if len(sys.argv) < 2:
    print ("Usage:  python calc_accuracy.py test predictions")
    sys.exit()
test = open(sys.argv[1], "r")

top_5_list = []
if options.top_5:
    top_5_fid = open(options.top_5, "r").readlines()
    for line in top_5_fid:
        top_5_list += [line.strip()]

by_example = False
#predicted_NE_sent = open(sys.argv[2], "r")

if options.with_confidence:
    conf_file = open("acc_conf.csv", "w+")


def get_predicted(predicted, answers=defaultdict(lambda: defaultdict(defaultdict))):
    global wanted_NEs
    example = 0
    word_index = 0
    entity = []
    last_ne = "O"
    last_entity = []

    answers[example] = []
    for line in predicted:
        line = line.strip()
        if line.startswith("//"):
            continue
        elif len(line) == 0:
            if entity:
                answers[example].append(list(entity))
                entity = []

            example += 1
            answers[example] = []
            word_index = 0
            continue
        else:
            split_line = line.split("\t")
            #word = split_line[0]
            value = split_line[0]
            ne = value[0]
            sent = value[2:]

            last_entity = []

            #check if it is start of entity
            if ne == 'B' or (ne == 'I' and last_ne == 'O'):
                if entity:
                    last_entity = list(entity)

                entity = [sent]
                entity.append(word_index)

            elif ne == 'I':
                entity.append(word_index)

            elif ne == 'O':
                if last_ne == 'B' or last_ne == 'I':
                    last_entity =list(entity)
                entity = []


            if last_entity:
                answers[example].append(list(last_entity))
                last_entity = []


        last_ne = ne
        word_index += 1


    # Uncomment to norm the answers.
    #answers = norm_answers(answers)
    return answers

def get_observed(observed):
    global wanted_NEs

    example = 0
    word_index = 0
    entity = []
    last_ne = "O"
    last_entity = []

    observations=defaultdict(defaultdict)
    observations[example] = []



    for line in observed:
        line = line.strip()
        if line.startswith("//"):
            go = True
            continue
        elif len(line) == 0:
            if entity:
                observations[example].append(list(entity))
                entity = []

            example += 1
            observations[example] = []
            word_index = 0
            continue

        else:
            split_line = line.split("\t")
            word = split_line[0]
            value = split_line[9]
            ne = value[0]
            sent = value[2:]

            last_entity = []

            #check if it is start of entity
            if ne == 'B' or (ne == 'I' and last_ne == 'O'):
                if entity:
                    last_entity = entity

                entity = [sent]
                entity.append(word_index)

            elif ne == 'I':
                entity.append(word_index)

            elif ne == 'O':
                if last_ne == 'B' or last_ne == 'I':
                    last_entity = entity
                entity = []


            if last_entity:
                observations[example].append(list(last_entity))
                last_entity = []


        last_ne = ne
        word_index += 1



    return observations


def split_NE_sentiment(input_NE):
    NE_tag = input_NE[0]
    sent_tag = input_NE[1:]
    if NE_tag == "I":
        sent_tag = ""
    return (NE_tag, sent_tag)
    

def compare_observed_to_predicted(observed, predicted):
    #print observed
    #print predicted
    #sys.exit()
    #NE_matrix = {"B":{"B":0, "I":0, "O":0}, "I":{"B":0, "I":0, "O":0}, "O":{"B":0, "I":0, "O":0}}
    #sentiment_matrix = {"":{"":0}, "positive":{"positive":0, "negative":0, "neutral":0}, "negative":{"positive":0, "negative":0, "neutral":0}, "neutral":{"positive":0, "negative":0, "neutral":0}}
    acc_hash = {"true":0.0, "false":0.0}
    joint_hash = {"true":0.0, "false":0.0}

    subjective_hash = {"matched":0}

    sentiment_hash = {"true positive":0.0, "true negative":0.0, "false positive":0.0, "false negative":0.0, "matched":0}

    NE_hash = {"true positive":0.0, "true negative":0.0, "false positive":0.0, "false negative":0.0, "matched":0}


    stat_hash = {"NE_matched":0.0, "sent_matched":0.0, "subj_matched":0.0}

    wanted_sentiments = ("positive", "negative", "Bpositive", "Bnegative", "Bsentiment", "sentiment")

    total_observed = 0.0
    total_predicted = 0.0


    for example in observed:
        # # if (example > 10):
        #     break

        observed_instance = observed[example]
        predicted_instance = predicted[example]
##        print(observed_instance)
##        print(predicted_instance)
##        print()
        total_observed += len(observed_instance)
        total_predicted += len(predicted_instance)

        for span in predicted_instance:
            matched = False
            span_begin = span[1]
            span_length = len(span) - 1
            span_ne = (span_begin, span_length)
            span_sent = span[0]
            span_subj = span_sent in wanted_sentiments

            for observed_span in observed_instance:
                begin = observed_span[1]
                length = len(observed_span) - 1
                ne = (begin, length)
                sent = observed_span[0]
                subj = sent in wanted_sentiments

                #NE matched
                if span_ne == ne:
                    NE_hash["matched"] += 1

                    if span_sent == sent:
                        sentiment_hash["matched"] += 1

                    if span_subj == subj:
                        subjective_hash["matched"] += 1


    

    print('###Stats')
    print('#observed: %d' % (total_observed))
    print('#predicted: %d' % (total_predicted))

    prec = NE_hash["matched"]/total_predicted
    rec = NE_hash["matched"]/total_observed
    f = 2 * prec * rec / (prec + rec)
    print('NE precision: %.4f' % (prec))
    print('NE recall: %.4f' %   (rec))
    print('NE F: %.4f' % (f))

    prec = sentiment_hash["matched"]/total_predicted
    rec = sentiment_hash["matched"]/total_observed
    f = 2 * prec * rec / (prec + rec)

    print('Sentiment precision: %.4f' % (prec))
    print('Sentiment recall: %.4f' % (rec))
    print('Sentiment F: %.4f' % (f))

    prec = subjective_hash["matched"]/total_predicted
    rec = subjective_hash["matched"]/total_observed
    f = 2 * prec * rec / (prec + rec)
    print('Subjectivity precision: %.4f' % (prec))
    print('Subjectivity recall: %.4f' % (rec))
    print('Subjectivity F: %.4f' % (f))

    print()

def analyze_observed(observed):

    total_observed = 0
    stats_hash = defaultdict(defaultdict)

    for example in observed:
        observed_instance = observed[example]

        num_ne = len(observed_instance)
        total_observed += num_ne

        key = 'num_NE_sentence_' + str(num_ne)
        t = stats_hash.setdefault(key, 0)
        stats_hash[key] = t + 1

        for ne in observed_instance:
            ne_length = len(ne) - 1
            key = 'NE_length_' + str(ne_length)
            t = stats_hash.setdefault(key, 0)
            stats_hash[key] = t + 1

            sent = ne[0]

            key = 'sent_' + sent
            t = stats_hash.setdefault(key, 0)
            stats_hash[key] = t + 1


    stats_hash['#sentence'] = len(observed)
    stats_hash['#NE'] = total_observed
    for key in sorted(stats_hash):
        print(key,stats_hash[key])





#predicted = get_predicted(predicted_NE_sent)
observed = get_observed(test)
analyze_observed(observed)
#compare_observed_to_predicted(observed, predicted)

