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
    
    override init() {
        super.init()
        (UIApplication.shared.delegate as! AppDelegate).viperInteractors["ProfileGridInteractor"] = self
    }
    
    func profileGridPresenterNeedsData() {
        print("ProfileList Interactor was called from ProfileList Presenter")
        delegate?.profileGridInteractorDidCallPresenter(withData: [["key":"value" as AnyObject]])
    }
}
