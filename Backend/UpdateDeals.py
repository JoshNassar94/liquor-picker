import urllib2
#from BeautifulSoup import BeautifulSoup

#This one in particular is nasty and won't really work that well.....but we'll figure it out
f = urllib2.urlopen("http://saltydogsaloon.com/")
s = f.read()
print s
#print s[s.find("building community"):]

#page = urllib2.urlopen("http://www.thursdaycg.xyz")
#soup = BeautifulSoup(page)
#print soup.prettify()


#Search the string for a given thing (Ladies night, BOGO, dollar signs, etc.)
#If you find it, figure out what the div is like that it's in (maybe search for the last < > characters).
#Figure out the class, id, etc of that specific element that it's in.
#Use beautifulsoup to get the actual text out of that section
#Try to find more sections like it in the html
#Repeat