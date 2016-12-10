//
//  Bar.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 11/28/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import Foundation
import MapKit
struct Bar {
    var name: String
    var website: String
    var idBars: String
    var latitude: Double
    var longitude: Double
    var dealCount: Double
    
    var DealList:[Deal]?
    var CommentList:[Comment]?
    init(name: String, website: String, idBars: String, latitude: Double, longitude: Double, dealCount: Double){
        self.name = name
        self.website = website
        self.idBars = idBars
        self.latitude = latitude
        self.longitude = longitude
        self.dealCount = dealCount
    }
}

struct Deal{
    var deadID: String?
    var deal: String
    var header: String?
    var barID: String
    var valid: Int
    var upVotes: Int
    var downVotes: Int
    var id: Int
    init(deal: String, barID: String, valid: Int, upVotes: Int, downVotes: Int, id: Int){
        
        self.deal = deal
        
        self.barID = barID
        self.valid = valid
        self.upVotes = upVotes
        self.downVotes = downVotes
        self.id = id
    }
    
}

struct Comment{
    var id: Int
    var comment: String
    var barID: String
    init(id: Int, comment: String, barID: String){
        self.id = id
        self.comment = comment
        self.barID = barID
    }
}
class BarAnnotation: MKPointAnnotation{
    var Bar:Bar?
    override init(){
        
    }
}
