//
//  SettingsViewController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 29.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import MessageUI

class SettingsViewController: UIViewController {
    
    internal var presenter: SettingsPresenter?
    
    // MARK: Init
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        presenter = SettingsPresenter()
        presenter?.delegate = self
        Log.info(message: "Settings VIPER module init did complete", sender: self)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Log.info(message: "SettingsViewController presented", sender: self)
    }
    
    // MARK: View actions
    
    @IBAction func closeButtonTouch(_ sender: Any) {
        dismiss(animated: true, completion: nil)
        Log.info(message: "SettingsViewController dismissed", sender: self)
    }
    
    @IBAction func shareLogFile(_ sender: Any) {
        presenter?.userWantsToSendLogFile()
    }
    
}

// MARK: ShopPresenter delegate

extension SettingsViewController: SettingsPresenterDelegate {
    
    func present(emailController: MFMailComposeViewController) {
        present(emailController, animated: true, completion: nil)
        Log.info(message: "SettingsViewController is called from SettingsPresenter to present MFMailComposeViewController", sender: self)
    }
    
    func dismissEmailController() {
        dismiss(animated: true, completion: nil)
        Log.info(message: "SettingsViewController is called from SettingsPresenter to dismiss MFMailComposeViewController", sender: self)
    }
    
}
