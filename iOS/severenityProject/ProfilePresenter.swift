//
//  ProfilePresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfilePresenter: NSObject, ProfileInteractorDelegate {
    
    private var interactor: ProfileInteractor?
    weak var delegate: ProfilePresenterDelegate?
    
    override init() {
        super.init()
        interactor = ProfileInteractor()
        interactor?.delegate = self
    }
    
    func profileInteractorDidCallPresenter() {
        print("Profile Presenter is called from Profile Interactor")
        delegate?.profilePresenterDidCallView()
    }
    
    func profileViewEvent() {
        print("User interacted with Profile View. Profile Presenter responds.")
        interactor?.profilePresenterEvent()
    }
}
