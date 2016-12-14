//
//  ProfileGridRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ProfileGridPresenterDelegate: class {
    func profileGridPresenterDidCallViewWith(data: [String])
}

protocol ProfileGridInteractorDelegate: class {
    func profileGridInteractorDidCallPresenterWith(data: Array<Dictionary<String, AnyObject>>)
}
