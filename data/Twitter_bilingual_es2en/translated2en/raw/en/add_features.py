import subprocess
import sys

dataname = ''

if len(sys.argv) <= 1:
    print('python ' + Path + 'add_features.py datanem outputfile')
    exit()

lang = 'en'

if sys.argv[1]:
    lang = sys.argv[1]

if sys.argv[2]:
    dataname = sys.argv[2]

outputfile = dataname

if sys.argv[3]:
    outputfile = sys.argv[3]

if dataname == '':
    exit()

Path = ''


#subprocess.call('python SemEvalToColl2.py ' + dataname + '.xml ' + outputfile +  ' >  ' + dataname + '.annotate', shell=True)
subprocess.call('python ' + Path + 'get_word.py  -l ' + lang + ' -i ' + dataname + '.annotate  -o ' + dataname + '.word', shell=True)
subprocess.call('python ' + Path + 'sentiment_to_conll.py -l en -i ' + dataname + '.word  -o ' + dataname + '.sentiment', shell=True)
subprocess.call('python ' + Path + 'get_postag.py -l en -i ' + dataname + '.word -o ' + dataname + '.postag', shell=True)
subprocess.call('python ' + Path + 'clusters_to_pos.py feature_files//brown_clusters 3  -l en -i ' + dataname + '.word -o ' + dataname + '.cluster3', shell=True)
subprocess.call('python ' + Path + 'clusters_to_pos.py feature_files//brown_clusters 5  -l en -i ' + dataname + '.word -o ' + dataname + '.cluster5', shell=True)
subprocess.call('java -DTwitterTokenizer.unicode=jerboa//unicode.csv -DTwitterTokenizer.full=true -cp jerboa//jerboa.jar edu.jhu.jerboa.processing.TwitterTokenizer ' + dataname + '.word > ' + dataname + '.jerboatmp', shell=True)
subprocess.call('python ' + Path + 'jerboa_to_conll.py < ' + dataname + '.jerboatmp > ' + dataname + '.jerboa', shell=True)
subprocess.call('python ' + Path + 'combine.py ' + dataname + ' ' + outputfile + '.coll', shell=True)
