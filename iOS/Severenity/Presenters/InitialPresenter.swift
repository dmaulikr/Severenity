//
//  InitialPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 12.12.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class InitialPresenter: NSObject {
    
    internal var interactor: InitialInteractor?
    weak var delegate: InitialPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = InitialInteractor()
        interactor?.delegate = self
    }
    
    // MARK: InitialViewController events
    
    func initialViewEvent() {
        Log.info(message: "User interacted with InitialViewController. InitialPresenter responds.", sender: self)
        interactor?.initialPresenterEvent()
    }
    
}

// MARK: InitialInteractor delegate

extension InitialPresenter: InitialInteractorDelegate {
    
    func initialInteractorDidCallPresenter() {
        Log.info(message: "InitialrPresenter is called from InitialInteractor", sender: self)
        delegate?.initialPresenterDidCallView()
    }
    
}
