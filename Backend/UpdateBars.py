from googleplaces import GooglePlaces, types, lang
import MySQLdb

#GLOBALS
API_KEY = 'AIzaSyDMGi7ufEqMurcKKJHXDkpgqU5JqIl1JBY'
hostname = "seniordesigninstance.c2zrygvejuhn.us-east-1.rds.amazonaws.com"
username = "SeniorDesign"
password = "JoshAndAlan"
database = "liquor_picker"
port = 3306
connection = MySQLdb.connect(host=hostname, user=username, passwd=password, db=database, port=port)  # 2
cursor = connection.cursor()
google_places = GooglePlaces(API_KEY)


#FUNCTIONS
def getBars():
    query_result = google_places.nearby_search(
        lat_lng={'lat': 29.651762, 'lng': -82.325344},
        radius=1000, types=[types.TYPE_BAR]
    )

    if query_result.has_attributions:
        print query_result.html_attributions

    for place in query_result.places:
        print place.name
        place.get_details()
        insertIntoDB(place)

    cursor.close()
    connection.commit()
    connection.close()

def insertIntoDB(place):
    latitude = str(place.geo_location['lat'])
    longitude = str(place.geo_location['lng'])
    cursor.execute("INSERT into Bars VALUES(%s, %s, %s, %s, %s) ON DUPLICATE KEY UPDATE website = %s",
                (place.place_id, place.name, place.website, latitude, longitude, place.website))


#MAIN
getBars()






#EXAMPLE CODE TO SAVE FOR LATER
# cursor.execute("create table lfy(name varchar(40), author varchar(40))")  #4
# cursor.execute("insert into lfy values('Foss Bytes','LFY Team')")         #5
# cursor.execute("insert into lfy values('Connecting MySql','Ankur Aggarwal')")
# cursor.execute("select * from lfy")                  #6
# multiplerow=cursor.fetchall()                        #7
# print "Displaying All the Rows:  ", multiplerow
# print multiplerow[0]
# cursor.execute("select * from lfy")
# row=cursor.fetchone()                                #8
# print  "Displaying the first row: ", row
# print "No of rows: ", cursor.rowcount                #9

