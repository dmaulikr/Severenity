//
//  SettingsPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 30.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import MessageUI

class SettingsPresenter: NSObject {
    
    internal var interactor: SettingsInteractor?
    weak var delegate: SettingsPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = SettingsInteractor()
        interactor?.delegate = self
    }
    
    // MARK: SettingsViewController events
    
    func userWantsToSendLogFile() {
        Log.info(message: "User wants to send log file via email. SettingsPresenter responds.", sender: self)
        interactor?.sendLogFileEmail()
    }
    
}

// MARK: ShopInteractor delegate

extension SettingsPresenter: SettingsInteractorDelegate {
    
    func present(controller: MFMailComposeViewController) {
        Log.info(message: "SettingsPresenter is called from SettingsInteractor to present email controller", sender: self)
        delegate?.present(emailController: controller)
    }
    
    func dismissController() {
        Log.info(message: "SettingsPresenter is called from SettingsInteractor to dismiss email controller", sender: self)
        delegate?.dismissEmailController()
    }
    
}
