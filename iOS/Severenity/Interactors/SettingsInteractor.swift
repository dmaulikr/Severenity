//
//  SettingsInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 30.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import MessageUI

class SettingsInteractor: NSObject {
    
    weak var delegate: SettingsInteractorDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kSettingsInteractor] = self
    }
    
    // MARK: ShopPresenter events
    
    func sendLogFileEmail() {
        Log.info(message: "SettingsInteractor was called from SettingsPresenter to send log file via email", sender: self)
        sendLogEmail()
    }
}


// MARK: MFMailComposeViewControllerDelegate

extension SettingsInteractor: MFMailComposeViewControllerDelegate {
    
    func sendLogEmail() {
        let emailController = MFMailComposeViewController()
        emailController.mailComposeDelegate = self
        if MFMailComposeViewController.canSendMail(), let filePath = kDocumentDirPath?.appendingPathComponent(kLogFileName) {
            emailController.setSubject("Severenity")
            emailController.setMessageBody("Severenity log file", isHTML: false)
            emailController.setToRecipients(["severenity@herokuapp.com"])
            do {
                let fileData = try Data.init(contentsOf: filePath)
                let mimeType = "text/txt"
                emailController.addAttachmentData(fileData, mimeType: mimeType, fileName: "log.txt")
            } catch {
                Log.error(message: "Cannot find log file", sender: self)
            }
            delegate?.present(controller: emailController)
        } else {
            Log.error(message: "Email service is not set up on device", sender: self)
        }
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        switch result {
        case .sent:
            Log.info(message: "Email with log file was sent", sender: self)
        case .saved:
            Log.info(message: "Email with log file was saved", sender: self)
        case .cancelled:
            Log.info(message: "Email with log file was canceled", sender: self)
        case .failed:
            Log.error(message: "Failed to send email with log file", sender: self)
        }
        delegate?.dismissController()
    }
    
}
