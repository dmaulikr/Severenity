//
//  ProfileListRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ProfileListPresenterDelegate: class {
    func profileListPresenterDidCallView(withData data: [String])
}

protocol ProfileListInteractorDelegate: class {
    func profileListInteractorDidCallPresenter(withData data: Array<Dictionary<String, AnyObject>>)
}

class ProfileListRouter: NSObject {

}
