//
//  HomeModel.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 10/26/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import Foundation

protocol HomeModelProtocol: class {
    func itemsDownloaded(items: NSArray)
}

class HomeModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegete: HomeModelProtocol!
    
    var data : NSMutableData = NSMutableData()
    
    let urlPath: String = "http://cise.ufl.edu/~jnassar/liquor-picker/getBars.php"
    
    
}
