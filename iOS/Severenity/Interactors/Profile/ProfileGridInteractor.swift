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
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kProfileGridInteractor] = self
    }
    
    // MARK: ProfileGridPresenter events
    
    func profileGridPresenterNeedsData() {
        Log.info(message: "ProfileListInteractor was called from ProfileListPresenter", sender: self)
        delegate?.profileGridInteractorDidCallPresenterWith(data: [["key":"value" as AnyObject]])
    }
}
