//
//  ShopRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 19.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ShopPresenterDelegate: class {
    func shopPresenterDidCallView()
}

protocol ShopInteractorDelegate: class {
    func shopInteractorDidCallPresenter()
}
