import urllib2
from bs4 import BeautifulSoup
import bs4


#Something else that needs to be considered is that deals will not always be on the home page...need a way to figure that one out.
#I wonder if beautifulsoup has a way to go to the different pages of a website....
#If not, I could look for specific words as links and go to those pages...could prove to be even more difficult, though.
#GLOBALS



#FUNCTIONS

#Get the index of the keyword
def getIndexFromKeyword(text, keyword):
    keyword = keyword.lower()
    hitIndex = text.find(keyword)
    return hitIndex


#Returns list of lines taht include the keywords
def getLinesWithKeywords(lines, keywords):
    indices = list()
    for index, line in enumerate(lines):
        for word in keywords:
            if lineIncludes(line, word):
                indices.append(index)
    return indices


#Returns distinct list of lines that that include the keywords
def getHeaderLines(lines, keywords):
    indices = list()
    for index, line in enumerate(lines):
        for word in keywords:
            if lineIncludes(line, word):
                indices.append(index)
    return getDistinctList(indices)


#Removes duplicates from list
def getDistinctList(list):
    seen = set()
    result = []
    for item in list:
        if item not in seen:
            seen.add(item)
            result.append(item)
    return result


#Returns true if the line includes str; else returns false
def lineIncludes(line, str):
    if getIndexFromKeyword(line, str) != -1 and getIndexFromKeyword(line, 'website') == -1 and getIndexFromKeyword(line, 'gainesville,') == -1:
        return True;
    return False;


#Helper to remove empty lines
def removeEmptyLines(lines):
    while '' in lines:
        lines.remove('')
    return lines



#Helper to clean up the HTML
def getVisibleText(element):
    if element.parent.name in ['style', 'script', '[document]', 'head', 'title']:
        return False
    elif isinstance(element, bs4.element.Comment):
        return False
    return True


#Keeps me from having two header lines in a row, if one is just the day and the next line repeats the day
def removeDoubleHeaders(text, headerLines, keywordLines):
    indices = list()
    for line in headerLines:
        if len(text[line]) >= 12 or (line+1 not in headerLines and line+1 in keywordLines):
            indices.append(line)
        elif line in keywordLines:
            keywordLines.remove(line)
    keyLines = keywordLines
    return indices, keyLines


#Encapsulates multiple line getting functions to make main method cleaner
def getLines(text, keywords, headerWords):
    keywordLines = getLinesWithKeywords(text, keywords)
    headerLines = getHeaderLines(text, headerWords)
    return removeDoubleHeaders(text, headerLines, keywordLines)


#Returns text that is needed to parse after all modifications have been done
def getModifiedText(text):
    visible = filter(getVisibleText, text)
    pageText = ''.join(visible)
    lowercaseText = pageText.lower()
    lowercaseText = lowercaseText.split('\n')
    return removeEmptyLines(lowercaseText)

#Returns the keywords and headerWords
def readFiles():
    keywords = [line.rstrip('\n') for line in open('keywords.txt')]
    headerWords = [line.rstrip('\n') for line in open('headerWords.txt')]
    return keywords, headerWords



#MAIN
#page = urllib2.urlopen("http://www.doughreligion.com")
#page = urllib2.urlopen("http://www.motherspub.com/specials")
page = urllib2.urlopen("http://www.durtynellys.us/specials")
soup = BeautifulSoup(page, "lxml")
text = soup.findAll(text=True)
lowercaseText = getModifiedText(text)
keywords, headerWords = readFiles()
headerLines, keyLines = getLines(lowercaseText, keywords, headerWords)


for line in headerLines:
    print lowercaseText[line]
    index = line + 1
    while index in keyLines and index not in headerLines:
        print lowercaseText[index]
        index = index + 1

    print "-----------------------------------------------------------------"

