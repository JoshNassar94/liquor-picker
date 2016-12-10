//
//  CommentTableViewController.swift
//  Liquor-Picker
//
//  Created by Alan Liou on 12/2/16.
//  Copyright © 2016 Alan Liou. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class CommentTableViewController: UITableViewController, UITextFieldDelegate{

    var idBar: String?
    var CommentList: [Comment]?
    var savedText: String!
    
    @IBOutlet weak var textField: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        textField.delegate = self
        
        uploadCommentList(idBars: idBar!) { CommentList in
            self.CommentList = CommentList
            self.tableView.reloadData()
            //print(CommentList)
            print("Got comments")
            
        }
        tableView.delegate = self
        
        tableView.dataSource = self
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 300
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        savedText = textField.text
        textField.text = ""
        sendComment()
        textField.resignFirstResponder()
        return false
    }
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return CommentList?.count ?? 1
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CommentCell", for: indexPath)
        if (CommentList?.count == 0)
        {
            print("empty comment")
            cell.textLabel?.text = "No Comments Yet!"
        }else{
            cell.textLabel?.text = CommentList?[indexPath.row].comment
        }
        return cell
    }
}

// Network Functions
extension CommentTableViewController{
    func uploadCommentList(idBars: String, completionHandler: @escaping (_ data: [Comment]) -> ()) -> (){
        let url = "http://cise.ufl.edu/~jnassar/liquor-picker/getComments.php?id=" + idBars
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
            var CommentList:[Comment] = []
            for i in 0..<count {
                let comment = Comment(id: json[i]["id"].intValue, comment: json[i]["Comment"].stringValue, barID: json[i]["Bar_ID"].stringValue)
                CommentList.append(comment)
            }
            completionHandler(CommentList)
        }
    }
    
    func sendComment(){
        // let comment = Foundation.URL(string: savedText)
        let comment = savedText.encodeUrl()
        let url = "http://cise.ufl.edu/~jnassar/liquor-picker/addComment.php?id=" + idBar! + "&comment=" + comment
        
        Alamofire.request(url)
        CommentList?.append(Comment(id: 0, comment: savedText, barID: idBar!))
        self.tableView.reloadData()
        
        print("Got comments")
    }
    
}

extension String
{
    func encodeUrl() -> String
    {
        return self.addingPercentEncoding( withAllowedCharacters: NSCharacterSet.urlQueryAllowed)!
    }
    func decodeUrl() -> String
    {
        return self.removingPercentEncoding!
    }
    
}
