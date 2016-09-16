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
    
    func loginButtonDidCompleteLogin(loginButton: LoginButton, result: LoginResult) {
        
        if let accessToken = FBSDKAccessToken.currentAccessToken() {
            print("FB access token: \n AppID: \(accessToken.appID) \n userID: \(accessToken.userID) \n token: \(accessToken.tokenString) \n")
            
            // Adding login indicator to the view
            view.backgroundColor = UIColor.whiteColor()
            loginButton.hidden = true
            welcomeLabel.hidden = true
            let loginIndicator = UIActivityIndicatorView()
            loginIndicator.color = UIColor.blueColor()
            loginIndicator.hidesWhenStopped = true
            view.addSubview(loginIndicator)
            loginIndicator.center = view.center
            loginIndicator.startAnimating()
            //self.performSegueWithIdentifier("didLoginSegue", sender: self)
            if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let vc = storyboard.instantiateViewControllerWithIdentifier("logedInController")
                self.navigationController?.popViewControllerAnimated(true)
                appDelegate.window?.rootViewController = vc
            }
            loginIndicator.stopAnimating()
            loginIndicator.removeFromSuperview()
            
        } else {
            print("Facebook login did complete with result: \(result)")
        }
        
    }

    func loginButtonDidLogOut(loginButton: LoginButton) {
        
        print("login button did logout")
    }

    // MARK: - Loading view
    
    override func viewWillAppear(animated: Bool) {
        
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        print("Facebook SDK version \(FBSDKSettings .sdkVersion())")
        let loginButton = LoginButton(readPermissions: [ .PublicProfile ])
        loginButton.delegate = self
        loginButton.center = CGPointMake(view.center.x, view.center.y+100)
        view.addSubview(loginButton)
        loginButton.hidden = false
        welcomeLabel.hidden = false
    }
    


}
