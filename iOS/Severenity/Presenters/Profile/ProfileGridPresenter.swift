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
        print("ProfileGridViewController needs data. ProfileGridPresenter responds.")
        interactor?.profileGridPresenterNeedsData()
    }
    

}


// MARK: ProfileGridInteracor delegate

extension ProfileGridPresenter: ProfileGridInteractorDelegate {
    
    func profileGridInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        print("ProfileGridInteractor did call ProfileGridPresenter")
        delegate?.profileGridPresenterDidCallView(withData: dataForTheView)
    }
}
