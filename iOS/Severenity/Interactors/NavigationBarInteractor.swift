//
//  NavigationInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class NavigationBarInteractor: NSObject {
    
    weak var delegate: NavigationBarInteractorDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["NavigationBarInteractor"] = self
    }
    
    // MARK: NavigationBarPresenter events
    
    func navigationBarPresenterNeedsData() {
        Log.info(message: "NavigationBarInteractor was called from NavigationBarPresenter", sender: self)
        
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            Log.error(message: "Cannot get FB Token", sender: self)
            return
        }
        FacebookService.sharedInstance.getFBProfilePicture(for: fbUserID, size: .normal, completion: { (image) in
            FacebookService.sharedInstance.getFBProfileInfo (with: "me", and: { (info) in
                    self.delegate?.navigationBarInteractorDidCallPresenter(with: image, and: info)
            })
        })
    }
}
