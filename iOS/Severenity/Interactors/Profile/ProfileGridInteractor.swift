//
//  ProfileGridInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ProfileGridInteractor: NSObject {
    
    weak var delegate: ProfileGridInteractorDelegate?
    private let locationsServerManager = PlacesService()
    private var placesData = [AnyObject]()
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ProfileGridInteractor"] = self
    }
    
    // MARK: - ProfileGridPresenter events
    
    func profileGridPresenterNeedsData() {
        print("ProfileListInteractor was called from ProfileListPresenter")
        delegate?.profileGridInteractorDidCallPresenter(withData: [["key":"value" as AnyObject]])
    }
}
