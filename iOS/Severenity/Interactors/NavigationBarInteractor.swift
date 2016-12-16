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
        UserService.sharedInstance.authorizeUserWith(userId: fbUserID) { success in
            FacebookService.sharedInstance.getFBProfilePicture(for: fbUserID, size: .normal, completion: { image in
                self.delegate?.navigationBarInteractorDidCallPresenterWithProfile(picture: image)
            })
        }
    }
    
    func saveUserData() {
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            Log.error(message: "Cannot get FB Token", sender: self)
            return
        }
        FacebookService.sharedInstance.getFBProfilePicture(for: fbUserID, size: .normal, completion: { image in
            self.delegate?.navigationBarInteractorDidCallPresenterWithProfile(picture: image)
            self.saveSharedData(image: image)
        })
    }
    
    /// Saves user profile data to UserDefaults to share it with notification center widget-extension
    private func saveSharedData(image: UIImage) {
        let userDefaults = UserDefaults(suiteName: "group.severenity.DataSharing")
        if let userName = User.current.name {
            userDefaults?.set(userName, forKey: "userName")
        } else {
            userDefaults?.set("", forKey: "userName")
        }
        let pictureData = UIImagePNGRepresentation(image)
        userDefaults?.set(pictureData, forKey: "profilePicture")
        userDefaults?.synchronize()
        Log.info(message: "User profile info saved to UserDefaults", sender: self)
    }
}
