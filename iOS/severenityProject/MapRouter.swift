//
//  MapRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol MapPresenterDelegate: class {
    func mapPresenterDidCallView(with data: Dictionary<String,AnyObject>)
}

protocol MapInteractorDelegate: class {
    func mapInteractorDidCallPresenter(with data: Dictionary<String,AnyObject>)
}

class MapRouter: NSObject {

}
