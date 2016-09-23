import urllib2
from bs4 import BeautifulSoup
import bs4
import urlparse
import MySQLdb

#GLOBALS
hostname = "seniordesigninstance.c2zrygvejuhn.us-east-1.rds.amazonaws.com"
username = "SeniorDesign"
password = "JoshAndAlan"
database = "liquor_picker"
port = 3306
connection = MySQLdb.connect(host=hostname, user=username, passwd=password, db=database, port=port)  # 2
cursor = connection.cursor()



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
        if keywordLines.count(line) > 1 or (line+1 not in headerLines and line+1 in keywordLines):
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
    pageText = pageText.split('\n')
    return removeEmptyLines(lowercaseText), removeEmptyLines(pageText)

#Returns the keywords and headerWords
def readFiles():
    keywords = [line.rstrip('\n') for line in open('keywords.txt')]
    headerWords = [line.rstrip('\n') for line in open('headerWords.txt')]
    return keywords, headerWords


#Function that is called on each URL to actually find the deals on that page
def findDeals(url, deals):
    print "Finding deals for " + url

    try:
        page = urllib2.urlopen(url)
        soup = BeautifulSoup(page, "lxml")
        text = soup.findAll(text=True)
        lowercaseText, pageText = getModifiedText(text)
        keywords, headerWords = readFiles()
        headerLines, keyLines = getLines(lowercaseText, keywords, headerWords)

        for line in headerLines:
            newDeal = pageText[line] + "\n"
            header = pageText[line]
            index = line + 1
            while index in keyLines and index not in headerLines:
                newDeal += pageText[index] + "\n"
                index = index + 1
            deals.append(newDeal)
            headers.append(header)
    except urllib2.HTTPError, e:
        print e.code
        print e.msg


#Gets all the bars from the sql table
def getBars():
    cursor.execute("Select * from Bars;")
    data = cursor.fetchall()
    return data


#MAIN
bars = getBars()
for row in bars:
    print row[2]    #Prints all the bar websites

#Need to iterate through the bars and then save off the deals for each one in the Deals db

url = "http://www.durtynellys.us"
page = urllib2.urlopen(url)
soup = BeautifulSoup(page, "lxml")
deals = list()
headers = list()

#Find all the different links on the homepage. Use those to find the deals.
alreadyDone = list()
for tag in soup.findAll('a', href=True):
    newURL = urlparse.urljoin(url, tag['href'])
    if url in newURL:
        if newURL not in alreadyDone and ".pdf" not in newURL:
            #findDeals(newURL, deals)
            alreadyDone.append(newURL)

for index, deal in enumerate(deals):
    print headers[index]
    print deal

cursor.close()
connection.commit()
connection.close()