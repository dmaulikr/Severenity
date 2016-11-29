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
        sendEmail()
    }
    
}

extension SettingsViewController: MFMailComposeViewControllerDelegate {
    
    func sendEmail() {
        let emailController = MFMailComposeViewController()
        emailController.mailComposeDelegate = self
        if MFMailComposeViewController.canSendMail(), let filePath = kDocumentDirPath?.appendingPathComponent(kLogFileName) {
            emailController.setSubject("Severenity")
            emailController.setMessageBody("Severenity log file", isHTML: false)
            emailController.setToRecipients(["severenity@herokuapp.com"])
            do {
                let fileData = try Data.init(contentsOf: filePath)
                let mimeType = "text/txt"
                emailController.addAttachmentData(fileData, mimeType: mimeType, fileName: "log")
            } catch {
                Log.error(message: "Cannot find log file", sender: self)
            }
            present(emailController, animated: true, completion: nil)
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
            Log.error(message: "Failed to send email with log fiel", sender: self)
        }
        dismiss(animated: true, completion: nil)
    }
    
}
