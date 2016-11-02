//
//  ProfilePresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfilePresenter: NSObject {
    
    internal var interactor: ProfileInteractor?
    weak var delegate: ProfilePresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ProfileInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ProfileViewController events
    
    func profileViewEvent() {
        print("User interacted with ProfileViewController. ProfilePresenter responds.")
        interactor?.profilePresenterEvent()
    }
    
}

// MARK: ProfileInteractor delegate

extension ProfilePresenter: ProfileInteractorDelegate {
    
    func profileInteractorDidCallPresenter() {
        print("ProfilePresenter is called from ProfileInteractor")
        delegate?.profilePresenterDidCallView()
    }
    
}
