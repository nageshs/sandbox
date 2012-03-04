import sys
import codecs
from xml.dom.minidom import parseString
from camelCase import getCamelCase

country2_code = {}
code2Name = {}
codes = []
missing = []
country_code_abbrevs = []

def generate_code(t, value):
    #print "<item>%s (+%s)</item>" %(t, value)
    country2_code[t]= value
    try:
        country_name = code2Name[t]
        print "<item>%s (+%s)</item>" %(getCamelCase(country_name.encode('utf-8').replace("'","\\'")), value.encode('utf-8'))
        codes.append("+%s" %value.encode('utf-8'))
        country_code_abbrevs.append(t)
    except KeyError as k:
        missing.append(t)
        

def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


def processCountry2Code():
    file = open('./codes.xml')
    data = file.read()
    file.close()
    dom = parseString(data)
    iccs = dom.getElementsByTagName("icc")
    theone = iccs[0]
    for i in theone.childNodes:
        if i.nodeType == i.ELEMENT_NODE:
            generate_code(i._get_tagName(), getText(i.childNodes))
            

def parseNode(node):
    code = node.getElementsByTagName("ISO_3166-1_Alpha-2_Code_element")
    name = node.getElementsByTagName("ISO_3166-1_Country_name")
    codeName = getText(code[0].childNodes)
    countryName = getText(name[0].childNodes)
    code2Name[codeName] = countryName

def processFullNames():
    file = open('./iso_3166-1_list_en.xml')
    data = file.read()
    file.close()
    dom = parseString(data)
    elms = dom.getElementsByTagName("ISO_3166-1_Entry")
    for node in elms:
        parseNode(node)


    
processFullNames()

# validate that all arrays are equals
if len(codes) != len(country_code_abbrevs):
    raise Error

print "<string-array name=\"country_code_array\">"
processCountry2Code()
print "</string-array>"

print ""

print "<string-array name=\"country_code_array_values\">"
for i in codes:
    print "<item>%s</item>" %i
print "</string-array>"

print ""
print "<string-array name=\"country_code_abbrevs\">"
for i in country_code_abbrevs:
    print "<item>%s</item>" %i
print "</string-array>"

