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
    
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        
        print("FB login button did login")
    }
    
    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        
        if let appDelegate = UIApplication.shared.delegate as? AppDelegate {
            
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "loginController")
            _ = self.navigationController?.popViewController(animated: true)
            appDelegate.window?.rootViewController = vc
        }
    }
    
    // MARK: - Managing view presentation
    
    @IBAction func changeContentMode(_ sender: AnyObject) {
        
        switch contentModeSwitcher.selectedSegmentIndex {
            case 0:
                let listViewController = ListViewController()
                self.addChildViewController(listViewController)
                contentView.subviews.last?.removeFromSuperview()
                contentView.addSubview(listViewController.view)
                listViewController.tableView?.frame = contentView.bounds
            case 1:
                let layout = UICollectionViewFlowLayout()
                layout.itemSize = CGSize(width: 100, height: 50)
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
        
        let loginButton = LoginButton(readPermissions: [ .publicProfile ])
        loginButton.delegate = self
        loginButton.center = CGPoint(x: view.center.x, y: contentView.bounds.size.height + 122)
        view.addSubview(loginButton)
    }
}
