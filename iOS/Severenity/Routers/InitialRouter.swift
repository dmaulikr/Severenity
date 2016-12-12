//
//  InitialRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 12.12.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol InitialPresenterDelegate: class {
    func initialPresenterDidCallView()
}

protocol InitialInteractorDelegate: class {
    func initialInteractorDidCallPresenter()
}

