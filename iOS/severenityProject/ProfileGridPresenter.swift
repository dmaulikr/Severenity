//
//  ProfileGridPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileGridPresenter: NSObject, ProfileGridInteractorDelegate {
    
    private var interactor: ProfileGridInteractor?
    private var dataForTheView = [String]()
    weak var delegate: ProfileGridPresenterDelegate?
    
    override init() {
        super.init()
        interactor = ProfileGridInteractor()
        interactor?.delegate = self
    }
    
    func provideProfileGridData() {
        print("ProfileGrid View needs data. ProfileGrid Presenter responds.")
        interactor?.profileGridPresenterNeedsData()
    }
    
    func profileGridInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        print("ProfileGrid Interactor did call Presenter")
        delegate?.profileGridPresenterDidCallView(withData: dataForTheView)
    }
}
