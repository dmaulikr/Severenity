//
//  Tab5ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class QuestsViewController: UIViewController {
    
    internal var presenter: QuestsPresenter?
    
    // MARK: Init

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = QuestsPresenter()
        presenter?.delegate = self
        Log.info(message: "Quests VIPER module init did complete")
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.info(message: "Quests tab did load");
    }
    
    @IBAction func userInteractionTest(_ sender: AnyObject) {
        presenter?.questsViewEvent()
    }
    
}

// MARK: QuestsPresenter delegate

extension QuestsViewController: QuestsPresenterDelegate {
    
    func questsPresenterDidCallView() {
        Log.info(message: "QuestsViewController is called from QuestsPresenter")
    }
    
}
