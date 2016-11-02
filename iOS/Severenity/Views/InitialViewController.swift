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

class InitialViewController: UIViewController {
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("Facebook SDK version: \(FBSDKSettings.sdkVersion())")
        
        let loginButton = LoginButton(readPermissions: [ .publicProfile, .userFriends, .email ])
        loginButton.delegate = self
        loginButton.center = CGPoint(x: view.center.x, y: view.center.y + 100)
        loginButton.isHidden = false
        
        view.addSubview(loginButton)
    }
    
}

// MARK: Facebook login button delegate

extension InitialViewController: LoginButtonDelegate {
    
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        
        guard let accessToken = FBSDKAccessToken.current() else {
            print("Facebook login did complete with result: \(result)")
            return
        }
        
        print("Facebook access token: \n AppID: \(accessToken.appID) \n userID: \(accessToken.userID) \n token: \(accessToken.tokenString) \n")
        
        view.backgroundColor = UIColor.white
        
        loginButton.isHidden = true
        
        startActivityIndicator(view: view)
        
        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "loggedInController") // double g
            _ = navigationController?.popViewController(animated: true)
            appDelegate.window?.rootViewController = vc
        }
        
        stopActivityIndicator(view: view)
        
    }
    
    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        print("Facebook login button did logout")
    }
    
}
