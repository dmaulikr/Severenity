//
//  Tab4ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatViewController: UIViewController, ChatPresenterDelegate, UITextFieldDelegate, UITableViewDelegate {
    
    private var presenter: ChatPresenter?
    private var messages: Dictionary<String,Dictionary<String,Any>> = [:]
    
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
        newMessageTextField.delegate = self
        messagesTableView.register(UINib(nibName: "MessageView", bundle: nil), forCellReuseIdentifier: "MessageView")
        messagesTableView.backgroundColor = UIColor.black
        messagesTableView.separatorColor = UIColor.magenta
        print("Chat tab did load");
    }
    
    @IBAction func sendMessageButtonTap(_ sender: AnyObject) {
        presenter?.userSendsMessage(with: newMessageTextField.text!)
    }
    
    // MARK: - ChatPresenter delegate
    
    func displayNewMessage(with dictionary: Dictionary<String,String>) {
        print("ChatViewController is called from ChatPresenter with message: \(dictionary)")
        if let messageId = dictionary["messageId"] {
            messages[messageId] = dictionary
        }
        messagesTableView.reloadData()
    }
    // MARK: - UITableView delegate
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 70.0
    }
    
    // MARK: - UITableView data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messages.count
    }
    
    private func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell
        if let c = tableView.dequeueReusableCell(withIdentifier: "MessageView", for: indexPath) as? MessageView {
            c.messageText.text = "TRTRTR"
            cell = c
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "MessageView", for: indexPath)
        }
        return cell
    }
    
    // MARK: UITextField delegate

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        presenter?.userSendsMessage(with: newMessageTextField.text!)
        return true
    }
}
