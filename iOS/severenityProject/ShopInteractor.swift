//
//  ShopInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 19.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ShopInteractor: NSObject {
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ShopInteractor"] = self
    }
    
    weak var delegate: ShopInteractorDelegate?
    
    func shopPresenterEvent() {
        print("Shop Interactor was called from Shop Presenter")
        delegate?.shopInteractorDidCallPresenter()
    }
}
