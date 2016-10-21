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
    
    override init() {
        super.init()
        interactor = ShopInteractor()
        interactor?.delegate = self
    }
    
    func shopInteractorDidCallPresenter() {
        print("Shop Presenter is called from Shop Interactor")
        delegate?.shopPresenterDidCallView()
    }
    
    func shopViewEvent() {
        print("User interacted with Shop View. Shop Presenter responds.")
        interactor?.shopPresenterEvent()
    }
}
