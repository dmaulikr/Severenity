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
    
    // MARK: TabBarPresetner events
    
    func initialPresenterEvent() {
        Log.info(message: "InitialInteractor was called from InitialPresenter", sender: self)
        delegate?.initialInteractorDidCallPresenter()
    }
}
