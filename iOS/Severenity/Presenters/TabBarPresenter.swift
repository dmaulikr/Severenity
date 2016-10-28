//
//  TabBarPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarPresenter: NSObject, TabBarInteractorDelegate {

    private var interactor: TabBarInteractor?
    weak var delegate: TabBarPresenterDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        interactor = TabBarInteractor()
        interactor?.delegate = self
    }
    
    // MARK: - TabBarViewController events
    
    func tabBarViewEvent() {
        print("User interacted with TabBarController. TabBarPresenter responds.")
        interactor?.tabBarPresenterEvent()
    }
    
    // MARK: - TabBarInteractor delegate
    
    func tabBarInteractorDidCallPresenter() {
        print("TabBarPresenter is called from TabBarInteractor")
        delegate?.tabBarPresenterDidCallView()
    }
}
