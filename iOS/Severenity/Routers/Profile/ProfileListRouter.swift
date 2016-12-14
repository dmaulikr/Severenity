//
//  ProfileListRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ProfileListPresenterDelegate: class {
    func profileListPresenterDidCallViewWith(data: [String])
}

protocol ProfileListInteractorDelegate: class {
    func profileListInteractorDidCallPresenterWith(data: Array<Dictionary<String, AnyObject>>)
}
