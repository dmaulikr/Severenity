//
//  TabBarRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol TabBarPresenterDelegate: class {
    func tabBarPresenterDidCallView()
}

protocol TabBarInteractorDelegate: class {
    func tabBarInteractorDidCallPresenter()
}
