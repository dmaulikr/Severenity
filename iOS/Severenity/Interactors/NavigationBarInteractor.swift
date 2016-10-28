//
//  NavigationInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class NavigationBarInteractor: NSObject {
    
    weak var delegate: NavigationBarInteractorDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["NavigationBarInteractor"] = self
    }
    
    // MARK: - NavigationBarPresenter events
    
    func navigationBarPresenterNeedsData() {
        print("NavigationBarInteractor was called from NavigationBarPresenter")
        
        FacebookService.sharedInstance.getFBProfilePicture(with: (FBSDKAccessToken.current().userID)!) { (image) in
            FacebookService.sharedInstance.getFBProfileInfo { (info) in
                self.delegate?.navigationBarInteractorDidCallPresenter(with: image, and: info)
            }
        }
    }
}
