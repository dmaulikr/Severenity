//
//  InitialInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 12.12.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class InitialInteractor: NSObject {
    
    weak var delegate: InitialInteractorDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kInitialInteractor] = self
    }
    
    // MARK: InitialPresetner events
    
    func authorizeUserWith(userId: String, completion: @escaping (_ success: Bool) -> Void) {
        Log.info(message: "InitialInteractor was called from InitialPresenter to authorize user", sender: self)
        UserService.sharedInstance.authorizeUserWith(userId: userId) { success in
            if success {
                completion(true)
            } else {
                completion(false)
            }
            
        }
    }
    
}
