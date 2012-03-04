import sys

def processTokens(tokens):
    result='';
    for token in tokens:
        if token is not None:
            result=result+token.title()
            result += ' '
    return result

def processString(string,separator=' '):
    li=string.split(separator)
    if li is not []:
        #result=li[0].upper()
        result= processTokens(li)
    return result

def getCamelCase(string,separator=' '):
    return processString(string,separator)


if __name__=="__main__":
    if sys.argv.__len__()<3:
        print 'usage: camelcase [input filename] [output filename]'
    else:        
        f=open(sys.argv[1],'r')
        o=open(sys.argv[2],'w')
        for line in f:
            o.write(getCamelCase(line))
        f.close()
        o.close()
