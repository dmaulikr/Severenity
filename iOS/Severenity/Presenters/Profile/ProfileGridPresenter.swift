//
//  ProfileGridPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileGridPresenter: NSObject {
    
    internal var interactor: ProfileGridInteractor?
    internal var dataForTheView = [String]()
    weak var delegate: ProfileGridPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ProfileGridInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ProfileGridViewController events
    
    func provideProfileGridData() {
        Log.info(message: "ProfileGridViewController needs data. ProfileGridPresenter responds.", sender: self)
        interactor?.profileGridPresenterNeedsData()
    }
    

}


// MARK: ProfileGridInteracor delegate

extension ProfileGridPresenter: ProfileGridInteractorDelegate {
    
    func profileGridInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        Log.info(message: "ProfileGridInteractor did call ProfileGridPresenter", sender: self)
        delegate?.profileGridPresenterDidCallView(withData: dataForTheView)
    }
}
