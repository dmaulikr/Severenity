//
//  NavigationController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 25.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class NavigationBarController: UINavigationController {
    
    internal var presenter: NavigationBarPresenter?
    internal var navBarView: NavigationBarView!
    
    // MARK: Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = NavigationBarPresenter()
        presenter?.delegate = self
        Log.info(message: "NavigationBar VIPER module init did complete", sender: self)
    }
    
    // MARK: Loading view
    
    override func viewDidLoad() {
        super.viewDidLoad()
        startActivityIndicator(location: CGPoint.init(x: navigationBar.frame.width/2, y: 35), view: navigationBar)
        Log.info(message: "NavigationBarController did load", sender: self)
    }
    
    override func viewWillLayoutSubviews() {
        presenter?.navigationBarViewNeedsData()
        navigationBar.frame.size.height = 70
    }
    

}

// MARK: NavigationBarPresenter delegate

extension NavigationBarController: NavigationBarPresenterDelegate {

    func navigationBarPresenterDidCallView(with picture: UIImage, and info: Dictionary<String,String>) {
        Log.info(message: "NavigationBarPresenter did call NavigationBarViewController", sender: self)
        
        if navBarView == nil, let viewForNavBar = NavigationBarView.loadFromNibNamed(nibNamed: "NavigationBarView") as? NavigationBarView {
            viewForNavBar.userPicture.image = picture.roundedImageWithBorder(with: 4, and: #colorLiteral(red: 0.5176470588, green: 0.3411764706, blue: 0.6, alpha: 1))
            viewForNavBar.userName.text = info["name"]
            navBarView = viewForNavBar
            stopActivityIndicator(view: navigationBar)
            navigationBar.addSubview(navBarView)
        }
    }
    
}
