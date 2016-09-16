//
//  Tab2ViewController.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 12.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FacebookLogin
import FBSDKCoreKit
import FBSDKLoginKit

class ProfileViewController: UIViewController, LoginButtonDelegate {

    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var contentModeSwitcher: UISegmentedControl!
    
    // MARK: - Facebook login button delegate
    
    func loginButtonDidCompleteLogin(loginButton: LoginButton, result: LoginResult) {
        
        print("login button did login")
    }
    
    func loginButtonDidLogOut(loginButton: LoginButton) {
        
        if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
            
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewControllerWithIdentifier("loginController")
            self.navigationController?.popViewControllerAnimated(true)
            appDelegate.window?.rootViewController = vc
        }
    }
    
    // MARK: - Managing view presentation
    
    @IBAction func changeContentMode(sender: AnyObject) {
        
        switch contentModeSwitcher.selectedSegmentIndex {
        case 0:
            let listViewController = ListViewController()
            self.addChildViewController(listViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(listViewController.view)
            listViewController.tableView?.frame = contentView.bounds
        case 1:
            let layout = UICollectionViewFlowLayout()
            layout.itemSize = CGSizeMake(100, 50)
            let gridViewController = GridViewController(collectionViewLayout: layout)
            self.addChildViewController(gridViewController)
            contentView.subviews.last?.removeFromSuperview()
            contentView.addSubview(gridViewController.view)
            gridViewController.collectionView?.frame = contentView.bounds
        default:
            return
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        changeContentMode(self)
        
        let loginButton = LoginButton(readPermissions: [ .PublicProfile ])
        loginButton.delegate = self
        loginButton.center = CGPointMake(view.center.x, contentView.bounds.size.height + 122)
        view.addSubview(loginButton)
    }


}
