//
//  Tab4ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatViewController: UIViewController, ChatPresenterDelegate, UITextFieldDelegate {
    
    private var presenter: ChatPresenter?
    
    @IBOutlet weak var chatHistory: UITextView!
    @IBOutlet weak var newMessage: UITextField!
    

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = ChatPresenter()
        presenter?.delegate = self
        print("Chat VIPER module init did complete")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        newMessage.delegate = self
        print("Chat tab did load");
    }
    
    func chatPresenterDidCallView() {
        print("Chat View is called from Chat Presenter")
    }
    
    // MARK: UITextFieldDelegate
    
    @IBAction func sendMessage(_ sender: AnyObject) {
        presenter?.userSendsMessage(with: newMessage.text!)
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        presenter?.userSendsMessage(with: newMessage.text!)
        return true
    }
}
