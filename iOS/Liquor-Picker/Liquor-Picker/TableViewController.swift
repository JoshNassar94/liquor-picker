//
//  TableViewController.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 11/29/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import UIKit
import SwiftyJSON
import Alamofire
import MapKit

class TableViewController: UITableViewController {

   
    var idBar: String?
    var DealList:[Deal]?
    var CommentList:[Comment]?
    override func viewDidLoad() {
        super.viewDidLoad()
        
        uploadDealList(idBars: idBar!) { DealList in
            self.DealList = DealList
            self.tableView.reloadData()
            print("Got deal list")
        }
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 300
    }
   

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return DealList?.count ?? 0
        
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DealCell", for: indexPath) as! DealCell

        cell.dealLabel?.text = DealList?[indexPath.row].deal
        let upVote = DealList?[indexPath.row].upVotes
        let downVote = DealList?[indexPath.row].downVotes
        let count = upVote! - downVote!
        cell.countLabel.text = String(count)
        cell.upvoteBtn.tag = indexPath.row
        cell.upvoteBtn.addTarget(self, action: #selector(upvoteHit), for: .touchUpInside)
        cell.downvoteBtn.tag = indexPath.row
        cell.downvoteBtn.addTarget(self, action: #selector(downvoteHit), for: .touchUpInside)
        return cell
    }
 
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        //print("in segue")
        //print(selectedAnnotation.title)
        if segue.identifier == "ToCommentScene"{
            if let destination = segue.destination as? CommentTableViewController{
                destination.idBar = idBar
                print("to Comment Scene")
            }
        }
    }
    
    func downvoteHit(sender: UIButton){
        let indexPathOfThisCell = sender.tag
        let id = DealList?[indexPathOfThisCell].id
        if id != nil {
            let url = "http://cise.ufl.edu/~jnassar/liquor-picker/downvote.php?id="+String(describing: id!)
            
            Alamofire.request(url)
            print("downVoted deal at this url \(url)")
            let newDownVote = (DealList?[indexPathOfThisCell].downVotes)! + 1
            DealList?[indexPathOfThisCell].downVotes = newDownVote
            self.tableView.reloadData()
            print("updated deal list")
        }
        
    }
    func upvoteHit(sender: UIButton){
        let indexPathOfThisCell = sender.tag
        let id = DealList?[indexPathOfThisCell].id
        if id != nil {
            let url = "http://cise.ufl.edu/~jnassar/liquor-picker/upvote.php?id="+String(describing: id!)
       
            Alamofire.request(url)
            print("upVoted deal at this url \(url)")
            let newUpVote = (DealList?[indexPathOfThisCell].upVotes)! + 1
            DealList?[indexPathOfThisCell].upVotes = newUpVote
            self.tableView.reloadData()
            print("updated deal list")
        }
    }

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension TableViewController{
    
    func uploadDealList(idBars: String, completionHandler: @escaping (_ data: [Deal]) -> ()) -> (){
        let url = "http://cise.ufl.edu/~jnassar/liquor-picker/getDeals.php?id=" + idBars
        Alamofire.request(url).responseJSON{ response in
            guard response.result.error == nil else{
                print(response.result.error!)
                return
            }
            guard(response.result.value as? [[String: AnyObject]]) != nil else{
                print("no objects in JSON from API")
                return
            }
            let json = JSON(response.result.value!)
            let count = json.count
            var DealList:[Deal] = []
            for i in 0..<count {
                let deal = Deal(deal: json[i]["Deal"].stringValue, barID: json[i]["Bar_ID"].stringValue, valid: json[i]["Valid"].intValue,
                                upVotes: json[i]["UpVotes"].intValue, downVotes: json[i]["DownVotes"].intValue, id: json[i]["id"].intValue)
             
                DealList.append(deal)
            }
            completionHandler(DealList)
        }
    }

}
