//
//  Tab4ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class ChatViewController: UIViewController, ChatPresenterDelegate, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    
    private var presenter: ChatPresenter?
    private var messages = [Dictionary<String,Any>]()
    
    @IBOutlet weak var messagesTableView: UITableView!
    @IBOutlet weak var newMessageTextField: UITextField!
    
    // MARK: - Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = ChatPresenter()
        presenter?.delegate = self
        print("Chat VIPER module init did complete")
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        messagesTableView.delegate = self
        messagesTableView.dataSource = self
        newMessageTextField.delegate = self
        messagesTableView.register(UINib(nibName: "MessageView", bundle: nil), forCellReuseIdentifier: "MessageView")
        messagesTableView.backgroundColor = UIColor.black
        messagesTableView.separatorColor = UIColor.clear
        print("Chat tab did load");
    }
    
    @IBAction func sendMessageButtonTap(_ sender: AnyObject) {
        if newMessageTextField.text != "" {
            let messageToSend = ["messageId":"999999999999",
                                 "senderName":"User Name",
                                 "senderId":(FBSDKAccessToken.current().userID)!,
                                 "text":newMessageTextField.text!,
                                 "timestamp":"2016-10-29"] as [String : Any]
            presenter?.userSendsMessage(with: messageToSend)
        }
    }
    
    // MARK: - ChatPresenter delegate
    
    func displayNewMessage(with dictionary: Dictionary<String,String>) {
        print("ChatViewController is called from ChatPresenter with message: \(dictionary)")
        messages.append(dictionary)
        messagesTableView.reloadData()
    }
    // MARK: - UITableView delegate
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 105.0
    }
    
    // MARK: - UITableView data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messages.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell
        if let c = tableView.dequeueReusableCell(withIdentifier: "MessageView", for: indexPath) as? MessageView {
            let message = messages[(indexPath as NSIndexPath).row]
            c.infoLabel.text = "\((message["senderName"] as! String?)!), \((message["timestamp"] as! String?)!)"
            c.messageText.text = message["text"] as! String!
            if let senderFbId = message["senderId"]{
                FacebookService.sharedInstance.getFBProfilePicture(with: senderFbId as! String, and: { (image) in
                    c.profilePicture.image = image
                })
            }
            cell = c
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "MessageView", for: indexPath)
        }
        return cell
    }
    
    // MARK: UITextField delegate

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if newMessageTextField.text != "" {
            let messageToSend = ["messageId":"999999999999",
                                 "senderName":"User Name",
                                 "senderId":(FBSDKAccessToken.current().userID)!,
                                 "text":newMessageTextField.text!,
                                 "timestamp":"2016-10-29"] as [String : Any]
            presenter?.userSendsMessage(with: messageToSend)
        }
        return true
    }
}
