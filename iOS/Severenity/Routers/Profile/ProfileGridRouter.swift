//
//  ProfileGridRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ProfileGridPresenterDelegate: class {
    func profileGridPresenterDidCallView(withData data: [String])
}

protocol ProfileGridInteractorDelegate: class {
    func profileGridInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>)
}
