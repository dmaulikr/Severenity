//
//  Tab5ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class QuestsViewController: UIViewController, QuestsPresenterDelegate {
    
    private var presenter: QuestsPresenter?

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = QuestsPresenter()
        presenter?.delegate = self
        print("Chat VIPER module init did complete")
        print("Quests VIPER module init did complete")
    }

    @IBAction func userInteractionTest(_ sender: AnyObject) {
        presenter?.questsViewEvent()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Quests tab did load");
    }
    
    func questsPresenterDidCallView() {
        print("Quests View is called from Quests Presenter")
    }
}
