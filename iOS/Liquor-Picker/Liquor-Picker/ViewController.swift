//
//  ViewController.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 10/24/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//



import UIKit
import MapKit
import AddressBook
import SwiftyJSON
import Alamofire

class ViewController: UIViewController, CLLocationManagerDelegate {

    // storyboard controllers
    @IBOutlet var mainView: UIView!
    @IBOutlet weak var mapView: MKMapView!
   
    // global variables for app
    var locationManager: CLLocationManager!
    var selectedAnnotation: BarAnnotation!
    var test = "hello"
    override func viewDidLoad() {
        super.viewDidLoad()
        mapKitController()
        uploadBars() { BarList in
            self.showBarsOnMap(BarList: BarList)
        }
    }

    func locationManager(_ manager: CLLocationManager!, didUpdateLocations locations: [AnyObject]!){
        let location = locations.last as! CLLocation
        let center = CLLocationCoordinate2D(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
        let span = MKCoordinateSpanMake(0.02, 0.02)
        let region = MKCoordinateRegion(center: center, span: span)
        self.mapView.setRegion(region, animated: true)
        locationManager.stopUpdatingLocation()
    }
    
    // Buttons
    @IBAction func centerButton(_ sender: AnyObject) {
        locationManager.startUpdatingLocation()
    }
}

// MapKit functions
extension ViewController: MKMapViewDelegate {
    func mapKitController()->Void{
        mapView.showsUserLocation = true
        // Enabling location services to find user's current location
        if (CLLocationManager.locationServicesEnabled()){
            locationManager = CLLocationManager()
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.requestAlwaysAuthorization()
            locationManager.startUpdatingLocation()
        }
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView?{
        if annotation is MKUserLocation {return nil}

        let reuseId = "pin"

        var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseId) as? MKPinAnnotationView
        
        if pinView == nil {
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
            pinView!.canShowCallout = true
            pinView!.animatesDrop = true
            let calloutButton = UIButton(type: .detailDisclosure)
            pinView!.rightCalloutAccessoryView = calloutButton
            pinView!.sizeToFit()
        }
        else {
            pinView!.annotation = annotation
        }
        
        return pinView
    }
    
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        if control == view.rightCalloutAccessoryView {
            selectedAnnotation = view.annotation as! BarAnnotation
            super.performSegue(withIdentifier: "NextScene", sender: self)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "NextScene"{
            if let destination = segue.destination as? TableViewController{
                destination.idBar = self.selectedAnnotation.Bar?.idBars
                print("to Deal Scene")
            }
        }
    }
    
    func showBarsOnMap(BarList: [Bar])-> (){
        let count = BarList.count
        for i in 0..<count {
            if BarList[i].dealCount > 0 {
                let centerCoordinate = CLLocationCoordinate2DMake(BarList[i].latitude, BarList[i].longitude)
                let annotation = BarAnnotation()
                annotation.Bar = BarList[i]
                annotation.title = BarList[i].name
                annotation.coordinate = centerCoordinate
                self.mapView.addAnnotation(annotation)
                self.mapView.delegate = self
            }
        }
    }
}

// Networking functions
extension ViewController{
    func uploadBars(completionHandler: @escaping (_ data: [Bar]) -> ())->(){
        let url = "http://cise.ufl.edu/~jnassar/liquor-picker/getBars.php"
        Alamofire.request(url).responseJSON{ response in
            guard response.result.error == nil else{
                print(response.result.error!)
                return
            }
            guard (response.result.value as? [[String: AnyObject]]) != nil else{
                print("no objects in JSON from API")
                return
            }
            let json = JSON(response.result.value!)
            let count = json.count
            var BarList:[Bar] = []
            for i in 0..<count {
                let bar = Bar(name: json[i]["Name"].stringValue, website: json[i]["Website"].stringValue, idBars: json[i]["idBars"].stringValue,
                              latitude: json[i]["Lat"].doubleValue, longitude: json[i]["Lon"].doubleValue, dealCount: json[i]["DealCount"].doubleValue)
    
                BarList.append(bar)
            }
            completionHandler(BarList)
        }
    }
}

