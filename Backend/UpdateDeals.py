import urllib2
from bs4 import BeautifulSoup
import bs4

#ANOTHER IDEA
#Get all of the text and separate it by new lines into an array of strings
#Parse through the strings looking for key words and then separate into blocks of text
#Block 1 starts at the first key word and continues until it hits the line with the next keyword
#Once I have these blocks, try to figure out a way to remove lines that have no important information on them
#The remaining blocks should be deals
#If I find a day, there can only be one deal with that day. So if I find Tuesday listed twice, only the first one counts. This does not apply to other keywords.
#Have some sort of ratio of keywords to length of string to decide if a given string is valid
#Maybe recursively search blocks after breaking them down?


#Search the string for a given thing (Ladies night, BOGO, dollar signs, etc.)
#If you find it, take that whole line of text and the next lines of text until you don't find words in the following lists:
#drink, beer, $, martini, free, half, and basically anything in the other list except not quite...gotta find a good way of doing that. Or until you find an empty line.
#All of these things will denote it's time for the next one.
#Once you find a deal, that whole block of text should be erased so that it's not searched again.
#Repeat
#I think this could actually work decently...

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


#Gets the entire line that has the keyword
def getLineFromKeyword(text, keyword):
    keyword = keyword.lower()
    hitIndex = text.find(keyword)
    startIndex = text[0:hitIndex].rfind('\n')
    endIndex = text[hitIndex:].find('\n') + hitIndex
    str = text[startIndex:endIndex]
    print str
    return str


#Gets the entire line that contains that index
def getLineFromIndex(text, index):
    startIndex = text[0:index].rfind('\n')
    endIndex = text[index:].find('\n') + index
    str = text[startIndex:endIndex]
    print str
    return str


#Returns the index of the first character on the line containing this index
def getStartOfLineFromIndex(text, index):
    return text[0:index].rfind('\n')


#Returns sorted list of indices of occurrences of days of the week
def searchForDays(text):
    days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
    indices = [0]
    for day in days:
        newIndex = getIndexFromKeyword(text, day)
        indices.append(getStartOfLineFromIndex(text, newIndex))

    indices = sorted(indices)
    lastIndex = 0
    for index in indices:
        if lastIndex != 0:
            print text[lastIndex: index]
        lastIndex = index
    #print text[lastIndex:]
    return indices


#Helper to clean up the HTML
def getVisibleText(element):
    if element.parent.name in ['style', 'script', '[document]', 'head', 'title']:
        return False
    elif isinstance(element, bs4.element.Comment):
        return False
    return True


#MAIN
page = urllib2.urlopen("http://www.doughreligion.com")
soup = BeautifulSoup(page, "lxml")
text = soup.findAll(text=True)
visible = filter(getVisibleText, text)
pageText = ''.join(visible)
#print pageText
lowercaseText = pageText.lower()

searchForDays(lowercaseText)