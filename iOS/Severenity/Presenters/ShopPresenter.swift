//
//  ShopPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 19.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ShopPresenter: NSObject, ShopInteractorDelegate {
    
    private var interactor: ShopInteractor?
    weak var delegate: ShopPresenterDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        interactor = ShopInteractor()
        interactor?.delegate = self
    }
    
    // MARK: - ShopViewController events
    
    func shopViewEvent() {
        print("User interacted with ShopViewController. ShopPresenter responds.")
        interactor?.shopPresenterEvent()
    }
    
    // MARK: - ShopInteractor delegate
    
    func shopInteractorDidCallPresenter() {
        print("ShopPresenter is called from ShopInteractor")
        delegate?.shopPresenterDidCallView()
    }
}
