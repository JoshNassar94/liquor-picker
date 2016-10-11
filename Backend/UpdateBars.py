from googleplaces import GooglePlaces, types
import requests

#GLOBALS
API_KEY = 'AIzaSyDMGi7ufEqMurcKKJHXDkpgqU5JqIl1JBY'
url = "http://cise.ufl.edu/~jnassar/liquor-picker/updateBars.php"
startingLat = 29.651762
startingLon = -82.325344
coordDiff = 0.03
coordStep = 0.005

print "Connecting to Google API..."
google_places = GooglePlaces(API_KEY)
print "Connected!"




#FUNCTIONS

#This function runs across a given range of coordinates and finds all the bars in that area
#Since Google can only return <=20 results for each query, we have to keep the distance difference small
def getBars():
    count = 0
    total = int((2*coordDiff/coordStep) * (2*coordDiff/coordStep))
    alreadyFound = set()
    for x in my_range(-coordDiff, coordDiff, coordStep):
        for y in my_range(-coordDiff, coordDiff, coordStep):
            count += 1
            latitude = startingLat + x
            longitude = startingLon + y
            query_result = google_places.nearby_search(
                lat_lng={'lat': latitude, 'lng': longitude},
                radius=1000, types=[types.TYPE_BAR, types.TYPE_NIGHT_CLUB]
            )
            print "Searching at (" + str(latitude) + ", " + str(longitude) + ") : " + str(count) + "/" + str(total)

            for place in query_result.places:
                if place.place_id not in alreadyFound:
                    print place.name
                    place.get_details()
                    insertIntoDB(place)
                    alreadyFound.add(place.place_id)


#This function inserts a place into our database
def insertIntoDB(place):
    latitude = str(place.geo_location['lat'])
    longitude = str(place.geo_location['lng'])
    payload = {'id':place.place_id, 'name':place.name, 'website':place.website, 'lat':latitude, 'lon':longitude}
    requests.post(url, data=payload)


#This function works in my for loops to use non-integer ranges and steps
def my_range(start, end, step):
    while start <= end:
        yield start
        start += step


#MAIN
print "Searching for bars..."
getBars()

print "Retrieved all the bars!"