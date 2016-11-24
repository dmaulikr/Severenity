//
//  TabBarPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarPresenter: NSObject {

    internal var interactor: TabBarInteractor?
    weak var delegate: TabBarPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = TabBarInteractor()
        interactor?.delegate = self
    }
    
    // MARK: TabBarViewController events
    
    func tabBarViewEvent() {
        Log.info(message: "User interacted with TabBarController. TabBarPresenter responds.")
        interactor?.tabBarPresenterEvent()
    }
    
}

// MARK: TabBarInteractor delegate

extension TabBarPresenter: TabBarInteractorDelegate {
    
    func tabBarInteractorDidCallPresenter() {
        Log.info(message: "TabBarPresenter is called from TabBarInteractor")
        delegate?.tabBarPresenterDidCallView()
    }
    
}
