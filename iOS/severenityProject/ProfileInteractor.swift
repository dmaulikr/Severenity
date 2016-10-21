//
//  ProfileInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileInteractor: NSObject {
    
    weak var delegate: ProfileInteractorDelegate?
    
    override init() {
        super.init()
        WireFrame.sharedWireFrame.viperInteractors["ProfileInteractor"] = self
    }
    
    func profilePresenterEvent() {
        print("Profile Interactor was called from Profile Presenter")
        delegate?.profileInteractorDidCallPresenter()
    }
}
