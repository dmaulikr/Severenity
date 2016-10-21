//
//  Tab4ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatViewController: UIViewController, ChatPresenterDelegate {
    
    private var presenter: ChatPresenter?

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = ChatPresenter()
        presenter?.delegate = self
        print("Chat VIPER module init did complete")
    }
    
    @IBAction func userInteractionTest(_ sender: AnyObject) {
        presenter?.chatViewEvent()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Chat tab did load");
    }
    
    func chatPresenterDidCallView() {
        print("Chat View is called from Chat Presenter")
    }
}
