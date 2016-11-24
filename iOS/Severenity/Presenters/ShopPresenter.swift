//
//  ShopPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 19.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ShopPresenter: NSObject {
    
    internal var interactor: ShopInteractor?
    weak var delegate: ShopPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ShopInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ShopViewController events
    
    func shopViewEvent() {
        Log.info(message: "User interacted with ShopViewController. ShopPresenter responds.")
        interactor?.shopPresenterEvent()
    }

}

// MARK: ShopInteractor delegate

extension ShopPresenter: ShopInteractorDelegate {
    
    func shopInteractorDidCallPresenter() {
        Log.info(message: "ShopPresenter is called from ShopInteractor")
        delegate?.shopPresenterDidCallView()
    }
    
}
