//
//  TabBarInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class TabBarInteractor: NSObject {
    
    weak var delegate: TabBarInteractorDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors[kTabBarInteractor] = self
    }
    
    // MARK: TabBarPresetner events
    
    func tabBarPresenterEvent() {
        Log.info(message: "TabBarInteractor was called from TabBarPresenter", sender: self)
        delegate?.tabBarInteractorDidCallPresenter()
    }
}
