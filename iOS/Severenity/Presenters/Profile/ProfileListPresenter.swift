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
        print("ProfileList View needs data. ProfileList Presenter responds.")
        interactor?.profileListPresenterNeedsData()
    }
    
    func profileListCell(selected cell: IndexPath) {
        print("ProfileList View item was selected. ProfileList Presenter responds.")
        interactor?.profileListPresenterAskForTransition(withParam: cell.row)
    }
    
    // MARK: ProfileListInteractor delegate
    
    func profileListInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>) {
        print("ProfileList Presenter did call ProfileList Presenter")
        for element in data {
            dataForTheView.append(element["name"] as! String)
        }
        
        delegate?.profileListPresenterDidCallView(withData: dataForTheView)
    }
}
