//
//  DealCell.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 11/30/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import UIKit
import Alamofire
class DealCell: UITableViewCell {
    
  
    @IBOutlet weak var countLabel: UILabel!
    @IBOutlet weak var upvoteBtn: UIButton!
    @IBOutlet weak var downvoteBtn: UIButton!{
        didSet{
            downvoteBtn.addTarget(self, action: #selector(DealCell.downvoteHit), for: UIControlEvents.touchUpInside)
        }
    }
    @IBOutlet weak var dealLabel: UILabel!
    func downvoteHit(sender: UIButton){
        let indexPathOfThisCell = sender.tag
        print("This btn is at \(indexPathOfThisCell) row")
    }
}
