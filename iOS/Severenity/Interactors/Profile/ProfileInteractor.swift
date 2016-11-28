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
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kProfileInteractor] = self
    }
    
    // MARK: ProfilePresenter events
    
    func profilePresenterEvent() {
        Log.info(message: "ProfileInteractor was called from ProfilePresenter", sender: self)
        delegate?.profileInteractorDidCallPresenter()
    }
}
