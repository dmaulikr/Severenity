//
//  ShopInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 19.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ShopInteractor: NSObject {
    
    weak var delegate: ShopInteractorDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ShopInteractor"] = self
    }
    
    // MARK: - ShopPresenter events
    
    func shopPresenterEvent() {
        print("ShopInteractor was called from ShopPresenter")
        delegate?.shopInteractorDidCallPresenter()
    }
}
