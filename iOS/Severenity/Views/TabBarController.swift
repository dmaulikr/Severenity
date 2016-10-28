//
//  TabBarController.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 21.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarController: UITabBarController, TabBarPresenterDelegate {
    
    private var presenter: TabBarPresenter?
    
    // MARK: - Init
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        presenter = TabBarPresenter()
        presenter?.delegate = self
        print("TabBar VIPER module init did complete")
    }
    
    // MARK: - Loading view

    override func viewDidLoad() {
        super.viewDidLoad()
        print("TabBarController did load")
    }
    
    // MARK: - TabBarPresenter delegate
    
    func tabBarPresenterDidCallView() {
        print("TabBarController is called from TabBarPresenter")
    }
}
