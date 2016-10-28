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
    private var navBarView: NavigationBarView!
    
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
        navigationBar.frame.size.height = 70
    }
    
    // MARK: - NavigationBarPresenter delegate
    
    func navigationBarPresenterDidCallView(with picture: UIImage, and info: Dictionary<String,String>) {
        print("NavigationBarPresenter did call NavigationBarViewController")
        if self.navBarView == nil, let navBarView = NavigationBarView.loadFromNibNamed(nibNamed: "NavigationBarView") as? NavigationBarView {
            navBarView.userPicture.image = picture.roundedImageWithBorder(with: 2, and: UIColor.white)
            navBarView.userName.text = info["name"]
            self.navBarView = navBarView
            navigationBar.addSubview(self.navBarView)
        }
    }
}
