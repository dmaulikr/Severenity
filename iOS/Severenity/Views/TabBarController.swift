//
//  TabBarController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 21.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarController: UITabBarController {
    
    internal var presenter: TabBarPresenter?
    
    // MARK: Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = TabBarPresenter()
        presenter?.delegate = self
        print("TabBar VIPER module init did complete")
    }
    
    // MARK: Loading view

    override func viewDidLoad() {
        super.viewDidLoad()
        delegate = self
        selectedIndex = 2
        print("TabBarController did load")
    }

}

// MARK: TabBarPresenter delegate

extension TabBarController: TabBarPresenterDelegate {
    
    func tabBarPresenterDidCallView() {
        print("TabBarController is called from TabBarPresenter")
    }
    
}

// MARK: UITabBarController delegate

extension TabBarController: UITabBarControllerDelegate {
    
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
        let fromView: UIView = tabBarController.selectedViewController!.view
        let toView: UIView = viewController.view
        if fromView == toView {
            return false
        }
        UIView.transition(from: fromView, to: toView, duration: 0.3, options: UIViewAnimationOptions.transitionCrossDissolve) { (finished:Bool) in
        }
        return true
    }
    
}
