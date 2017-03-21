import sys

englishSubjLex = {}
brown_cluster = {}
separator = '\t'



def loadLinguisticFeatureLibary():
    global LinguisticFeaturesLibaryName, LinguisticFeaturesLibaryList
    global englishSubjLex, brown_cluster


## BROWN Cluster
    file = open("feature_files\\brown_clusters", 'r',encoding='utf8')
    cluster = []
    word = []
    comment = []
    for line in file:
        line = line.strip('\n')
        if line.startswith('##'):
            continue
        else:
            fields = line.split(separator)
            cluster.append(fields[0])
            word.append(fields[1])
            comment.append(fields[2])

    brown_cluster['cluster'] = cluster
    brown_cluster['word'] = word
    brown_cluster['comment'] = comment
            
        

# sentiment-bearing word; prior sentiment polarity
    file = open("EnglishSentimentLexicons\\englishSubjLex-MPQA.tsv", 'r')
    word = []
    polarity = []
    subj_score = []
    for line in file:
        line = line.strip('\n')
        if line.startswith('##') or not line:
            continue
        else:
            #logging.warning('****' + line)
            fields = line.split(separator)
            if len(fields) >= 3:
                word.append(fields[0])
                polarity.append(fields[1])
                subj_score.append(fields[2])
            #logging.warning('####')
            
    
    englishSubjLex['word'] = word
    englishSubjLex['polarity'] = polarity
    englishSubjLex['subj_score'] = subj_score

   

def readfiles():
    global englishSubjLex, brown_cluster
    fi = sys.stdin
    line = ''
    for line in fi:
        line = line.strip('\n')
        fields = line.split(' ')
        for word in fields:
            output = ''
            word = word.lower()
            ne = 'O'
            if word in brown_cluster['word']:
                index = brown_cluster['word'].index(word)
                c = brown_cluster['cluster'][index]
                if (len(c) >= 5):
                    brown_cluster_5 = c[:5]
                    brown_cluster_3 = c[:3]
                elif (len(c) == 3):
                    brown_cluster_5 = c
                    brown_cluster_3 = c
            else:
                brown_cluster_5 = '00000'
                brown_cluster_3 = '000'

            jerbo = 'NONE'


            if word in englishSubjLex['word']:
                index = englishSubjLex['word'].index(word)
                sentiment_polarity = englishSubjLex['polarity'][index]
                sentiment_subj_score = englishSubjLex['subj_score'][index]
            else:
                sentiment_polarity = '_'
                sentiment_subj_score = 0
            _sent_ = '_'

            _sent_ther_ = 'THER_SENT_3:' + sentiment_polarity
            if sentiment_polarity == '_':
                _sent_ther_ = '_'

            sentiment = '_'

            POS = '_'

            en_sent = 'O'

            print(word + '\t' + ne + '\t' + brown_cluster_5 + '\t' + jerbo + '\t' + brown_cluster_3 + '\t' + _sent_ + '\t' + _sent_ther_ + '\t' + sentiment + '\t' + POS + '\t' + en_sent)

        print()

loadLinguisticFeatureLibary()
readfiles()



