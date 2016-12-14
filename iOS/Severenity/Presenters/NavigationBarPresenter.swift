//
//  NavigationPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class NavigationBarPresenter: NSObject {

    internal var interactor: NavigationBarInteractor?
    weak var delegate: NavigationBarPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = NavigationBarInteractor()
        interactor?.delegate = self
    }
    
    // MARK: NavigationBarViewController events
    
    func navigationBarViewNeedsData() {
        Log.info(message: "User interacted with NavigationBarController. NavigationBarPresenter responds.", sender: self)
        interactor?.navigationBarPresenterNeedsData()
    }
    
    func userDataLoaded() {
        interactor?.saveUserData()
    }

}


// MARK: NavigationBarInteractor delegate

extension NavigationBarPresenter: NavigationBarInteractorDelegate {
    
    func navigationBarInteractorDidCallPresenterWithProfile(picture: UIImage) {
        Log.info(message: "NavigationBarPresenter is called from NavigationBarInteractor", sender: self)
        delegate?.navigationBarPresenterDidCallViewWithProfile(picture: picture)
    }
}
