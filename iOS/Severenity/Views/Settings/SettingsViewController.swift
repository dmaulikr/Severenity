//
//  SettingsViewController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 29.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class SettingsViewController: UIViewController {
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.info(message: "SettingsViewController presented", sender: self)
    }
    
    @IBAction func closeButtonTouch(_ sender: Any) {
        dismiss(animated: true, completion: nil)
        Log.info(message: "SettingsViewController dismissed", sender: self)
    }

}
