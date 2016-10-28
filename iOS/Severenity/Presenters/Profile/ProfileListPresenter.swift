//
//  ProfileListPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileListPresenter: NSObject, ProfileListInteractorDelegate {
    
    private var interactor: ProfileListInteractor?
    private var dataForTheView = [String]()
    weak var delegate: ProfileListPresenterDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        interactor = ProfileListInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ProfileListViewController events
    
    func provideProfileListData() {
        print("ProfileListViewController needs data. ProfileListPresenter responds.")
        interactor?.profileListPresenterNeedsData()
    }
    
    func profileListCell(selected cell: IndexPath) {
        print("ProfileListViewController item was selected. ProfileListPresenter responds.")
        interactor?.profileListPresenterAskForTransition(withParam: cell.row)
    }
    
    // MARK: ProfileListInteractor delegate
    
    func profileListInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        print("ProfileListInteracor did call ProfileListPresenter")
        for element in data {
            if let name = element["name"] as? String {
                dataForTheView.append(name)
            }
        }
        
        delegate?.profileListPresenterDidCallView(withData: dataForTheView)
    }
}
