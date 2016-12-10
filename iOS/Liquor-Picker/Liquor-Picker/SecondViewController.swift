//
//  SecondViewController.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 11/28/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import UIKit
import MapKit

class SecondViewController: UIViewController, MKMapViewDelegate {
   
    
   
    @IBOutlet weak var segueLabel: UILabel!
    var test: String!
    var annotation: BarAnnotation!
    override func viewDidLoad() {
        super.viewDidLoad()
        segueLabel.text = annotation.Bar?.idBars
        print(annotation.Bar?.website)
        //print(test)
        print(annotation.Bar?.idBars)
        print(annotation.Bar?.dealCount)
        //Label.text = annotation.title
        // Do any additional setup after loading the view.
    }



    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
