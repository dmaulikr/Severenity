//
//  ProfileListPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileListPresenter: NSObject {
    
    internal var interactor: ProfileListInteractor?
    internal var dataForTheView = [String]()
    weak var delegate: ProfileListPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ProfileListInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ProfileListViewController events
    
    func provideProfileListData() {
        Log.info(message: "ProfileListViewController needs data. ProfileListPresenter responds.")
        interactor?.profileListPresenterNeedsData()
    }
    
    func profileListCell(selected cell: IndexPath) {
        Log.info(message: "ProfileListViewController item was selected. ProfileListPresenter responds.")
        interactor?.profileListPresenterAskForTransition(withParam: cell.row)
    }

}

// MARK: ProfileListInteractor delegate

extension ProfileListPresenter: ProfileListInteractorDelegate {
    
    func profileListInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        Log.info(message: "ProfileListInteracor did call ProfileListPresenter")
        for element in data {
            if let name = element["name"] as? String {
                dataForTheView.append(name)
            }
        }
        
        delegate?.profileListPresenterDidCallView(withData: dataForTheView)
    }
    
}
