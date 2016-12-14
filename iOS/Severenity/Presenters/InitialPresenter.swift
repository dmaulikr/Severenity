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
    
    func facebookLoginDidCompleteWith(userId: String, completion: @escaping (_ success: Bool) -> Void) {
        Log.info(message: "User did login in InitialViewController. InitialPresenter responds.", sender: self)
        interactor?.authorizeUserWith(userId: userId, completion: { success in
            if success {
                completion(true)
            } else {
                completion(true)
            }
        })
    }
    
}

// MARK: InitialInteractor delegate

extension InitialPresenter: InitialInteractorDelegate {
    
    func initialInteractorDidCallPresenter() {
        Log.info(message: "InitialrPresenter is called from InitialInteractor", sender: self)
        delegate?.initialPresenterDidCallView()
    }
    
}
