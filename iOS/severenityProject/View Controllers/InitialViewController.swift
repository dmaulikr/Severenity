//
//  InitialTabBarController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FacebookLogin
import FBSDKCoreKit
import FBSDKLoginKit

class InitialViewController: UIViewController, LoginButtonDelegate {
    
    @IBOutlet weak var welcomeLabel: UILabel!
    
    // MARK: - Facebook login button delegate
    
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        
        guard let accessToken = FBSDKAccessToken.currentAccessToken() else {
            print("Facebook login did complete with result: \(result)")
            return
        }
        
        print("FB access token: \n AppID: \(accessToken.appID) \n userID: \(accessToken.userID) \n token: \(accessToken.tokenString) \n")
        
        view.backgroundColor = UIColor.white
        
        loginButton.hidden = true
        
        welcomeLabel.isHidden = true
        
        // Adding login indicator to the view
        let loginIndicator = UIActivityIndicatorView()
        loginIndicator.color = UIColor.blue
        loginIndicator.hidesWhenStopped = true
        loginIndicator.center = view.center
        loginIndicator.startAnimating()
        
        view.addSubview(loginIndicator)
        
        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "loggedInController") // double g
            self.navigationController?.popViewController(animated: true)
            appDelegate.window?.rootViewController = vc
        }
        
        loginIndicator.stopAnimating()
        loginIndicator.removeFromSuperview()
        
    }

    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        
        print("FB login button did logout")
    }

    // MARK: - Loading view

    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("Facebook SDK version \(FBSDKSettings .sdkVersion())")
        
        let loginButton = LoginButton(readPermissions: [ .PublicProfile ])
        loginButton.delegate = self
        loginButton.center = CGPoint(x: view.center.x, y: view.center.y+100)
        loginButton.hidden = false
        
        welcomeLabel.isHidden = false
        
        view.addSubview(loginButton)
    }
}
