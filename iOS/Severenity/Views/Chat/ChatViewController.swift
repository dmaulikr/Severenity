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
    @IBOutlet weak var sendMessageButton: UIButton!
    
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
        messagesTableView.register(UINib(nibName: "MessageOutView", bundle: nil), forCellReuseIdentifier: "MessageOutView")
        messagesTableView.register(UINib(nibName: "MessageInView", bundle: nil), forCellReuseIdentifier: "MessageInView")
        messagesTableView.backgroundColor = UIColor.black
        messagesTableView.separatorColor = UIColor.clear
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        print("Chat tab did load");
    }
    
    @IBAction func sendMessageButtonTap(_ sender: AnyObject) {
        if newMessageTextField.text != "" {
            presenter?.userWantsToSendMessage(with: newMessageTextField.text!)
            newMessageTextField.text = ""
            newMessageTextField.resignFirstResponder()
        }
    }
    
    // MARK: - Managing view layout on keyboard appear/disappear
    
    func keyboardWillShow(notification: NSNotification) {
        print(self.view.frame.origin)
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            newMessageTextField.frame.origin.y -= keyboardSize.height - 50 // magical number
            sendMessageButton.frame.origin.y -= keyboardSize.height - 50
        }
    }
    
    func keyboardWillHide(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            newMessageTextField.frame.origin.y += keyboardSize.height - 50
            sendMessageButton.frame.origin.y += keyboardSize.height - 50
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
        return 105.0 // size of the cell xib
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
        let message = messages[(indexPath as NSIndexPath).row]
        
        guard let senderName = message["senderName"] as? String, let timestamp = message["timestamp"] as? String,
            let messageText = message["text"] as? String, let senderFbId = message["senderId"] as? String else {
            print("Cannot create chat message cell")
            cell = UITableViewCell()
            return cell
        }
        
        if message["senderId"] as! String != (FBSDKAccessToken.current().userID)! {
            if let c = tableView.dequeueReusableCell(withIdentifier: "MessageInView", for: indexPath) as? MessageInView {
                c.infoLabel.text = "\(senderName), \(timestamp)"
                c.messageText.text = messageText
                FacebookService.sharedInstance.getFBProfilePicture(with: senderFbId, and: { (image) in
                        c.profilePicture.image = image
                    })
                cell = c
            }
            else {
                cell = tableView.dequeueReusableCell(withIdentifier: "MessageInView", for: indexPath)
            }
        }
        else {
            if let c = tableView.dequeueReusableCell(withIdentifier: "MessageOutView", for: indexPath) as? MessageOutView {
                c.infoLabel.text = "\(senderName), \(timestamp)"
                c.messageText.text = messageText
                FacebookService.sharedInstance.getFBProfilePicture(with: senderFbId, and: { (image) in
                    c.profilePicture.image = image
                })
                cell = c
            }
            else {
                cell = tableView.dequeueReusableCell(withIdentifier: "MessageOutView", for: indexPath)
            }
        }
        cell.backgroundColor = UIColor.clear
        
        tableView.scrollToRow(at: NSIndexPath.init(row: messages.count-1, section: 0) as IndexPath, at: .bottom, animated: true)
            
        return cell
    }
    
    // MARK: UITextField delegate

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if newMessageTextField.text != "" {
            if newMessageTextField.text != "" {
                presenter?.userWantsToSendMessage(with: newMessageTextField.text!)
                newMessageTextField.text = ""
                newMessageTextField.resignFirstResponder()
            }
        }
        return true
    }
}
