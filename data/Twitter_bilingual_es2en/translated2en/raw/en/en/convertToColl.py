import sys
import xml.etree.ElementTree as ET

tree = ET
#fi = sys.stdin
debug = False
num_arg = len(sys.argv)
arg = sys.argv

tree = ET.parse(arg[1])
root = tree.getroot()






