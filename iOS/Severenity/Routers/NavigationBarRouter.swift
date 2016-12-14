//
//  NavigationBarRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol NavigationBarPresenterDelegate: class {
    func navigationBarPresenterDidCallViewWithProfile(picture: UIImage)
}

protocol NavigationBarInteractorDelegate: class {
    func navigationBarInteractorDidCallPresenterWithProfile(picture: UIImage)
}
