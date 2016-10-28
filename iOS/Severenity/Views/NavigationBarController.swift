//
//  NavigationController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 25.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class NavigationBarController: UINavigationController, NavigationBarPresenterDelegate {
    
    private var presenter: NavigationBarPresenter?
    
    // MARK: - Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = NavigationBarPresenter()
        presenter?.delegate = self
        print("NavigationBar VIPER module init did complete")
    }
    
    // MARK: - Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("NavigationBarController did load")
    }
    
    override func viewWillLayoutSubviews() {
        presenter?.navigationBarViewNeedsData()
        self.navigationBar.frame.size.height = 70
    }
    
    // MARK: - NavigationBarPresenter delegate
    
    func navigationBarPresenterDidCallView(with picture: UIImage, and info: Dictionary<String,String>) {
        print("NavigationBar presenter did call view")
        let profilePictureView = UIImageView.init(frame: CGRect.init(x: 0, y: 0, width: 40, height: 40))
        profilePictureView.image = picture
        profilePictureView.center = CGPoint.init(x: self.navigationBar.center.x, y: 20)
        profilePictureView.layer.cornerRadius = 20
        profilePictureView.clipsToBounds = true
        self.navigationBar.addSubview(profilePictureView)
        self.navigationBar.topItem?.title = info["name"]
    }
}
