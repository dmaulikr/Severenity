//
//  ChatRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol ChatPresenterDelegate: class {
    func displayNewMessage(with dictionary: Dictionary<String,String>)
}

protocol ChatInteractorDelegate: class {
    func newMessageDidArrive(with dictionary: Dictionary<String,String>)
}
