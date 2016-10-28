//
//  NavigationBarRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 28.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol NavigationBarPresenterDelegate: class {
    func navigationBarPresenterDidCallView(with picture: UIImage, and info: Dictionary<String,String>)
}

protocol NavigationBarInteractorDelegate: class {
    func navigationBarInteractorDidCallPresenter(with picture: UIImage, and info: Dictionary<String,String>)
}
