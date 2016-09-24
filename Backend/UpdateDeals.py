import urllib2
from bs4 import BeautifulSoup
import bs4
import urlparse
import MySQLdb

#GLOBALS
urlHeader = {'User-Agent' : 'liquor-picker bot trying to steal yo deals by /u/josh'}
hostname = "seniordesigninstance.c2zrygvejuhn.us-east-1.rds.amazonaws.com"
username = "SeniorDesign"
password = "JoshAndAlan"
database = "liquor_picker"
port = 3306
print "Connecting to database: " + database + "..."
connection = MySQLdb.connect(host=hostname, user=username, passwd=password, db=database, port=port)
print "Connected!"
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
    badWords = [line.rstrip('\n') for line in open('badWords.txt')]
    return keywords, headerWords, badWords


#Function that is called on each URL to actually find the deals on that page
def findDeals(url, deals, keywords, headerWords):
    print "Finding deals for " + url

    try:
        req = urllib2.Request(url, headers=urlHeader)
        page = urllib2.urlopen(req).read()
        soup = BeautifulSoup(page, "lxml")
        text = soup.findAll(text=True)
        lowercaseText, pageText = getModifiedText(text)
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


def removeNonAscii(text):
    return ''.join([i if ord(i) < 128 else ' ' for i in text])

#MAIN
print "Retrieving bars from database..."
bars = getBars()
print "Bars received!"
keywords, headerWords, badWords = readFiles()
j = 0
print "Starting to search for deals..."
for row in bars:
    url = str(row[2])
    if "http" in url:
        try:
            req = urllib2.Request(url, headers=urlHeader)
            page = urllib2.urlopen(req).read()
            soup = BeautifulSoup(page, "lxml")
            deals = list()
            headers = list()

            #Find all the different links on the homepage. Use those to find the deals.
            alreadyDone = list()
            for tag in soup.findAll('a', href=True):
                newURL = urlparse.urljoin(url, tag['href'])
                if url in newURL and newURL not in alreadyDone:
                    goodURL = True
                    for word in badWords:
                        if word in newURL.lower():
                            goodURL = False
                    if goodURL == True:
                        findDeals(newURL, deals, keywords, headerWords)
                        alreadyDone.append(newURL)

            for index, deal in enumerate(deals):
                #THIS IS WHERE I ADD THEM TO THE DATABASE
                headers[index] = removeNonAscii(headers[index])
                deal = removeNonAscii(deal)
                dealID = deal
                valid = True
                if len(deal) >= 255:
                    dealID = deal[0:255]
                cursor.execute("INSERT IGNORE into Deals VALUES(%s, %s, %s, %s, %s)", (dealID, deal, headers[index], row[0], valid))
        except urllib2.HTTPError, e:
            print e.code
            print e.msg

print "Done seraching for deals!"
cursor.close()

connection.commit()
print "Pushed queries to database!"

connection.close()
print "Closed connection to database!"