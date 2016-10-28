//
//  NavigationPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class NavigationBarPresenter: NSObject, NavigationBarInteractorDelegate {

    private var interactor: NavigationBarInteractor?
    weak var delegate: NavigationBarPresenterDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        interactor = NavigationBarInteractor()
        interactor?.delegate = self
    }
    
    // MARK: - NavigationBarViewController events
    
    func navigationBarViewNeedsData() {
        print("User interacted with NavigationBarController. NavigationBarPresenter responds.")
        interactor?.navigationBarPresenterNeedsData()
    }
    
    // MARK: - NavigationBarInteractor delegate
    
    func navigationBarInteractorDidCallPresenter(with picture: UIImage, and info: Dictionary<String,String>) {
        print("NavigationBarPresenter is called from NavigationBarInteractor")
        delegate?.navigationBarPresenterDidCallView(with: picture, and: info)
    }
}
