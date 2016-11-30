//
//  SettingsRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 30.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import MessageUI

protocol SettingsPresenterDelegate: class {
    func present(emailController: MFMailComposeViewController)
    func dismissEmailController()
}

protocol SettingsInteractorDelegate: class {
    func present(controller: MFMailComposeViewController)
    func dismissController()
}
