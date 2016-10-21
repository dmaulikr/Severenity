//
//  QuestsRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

protocol QuestsPresenterDelegate: class {
    func questsPresenterDidCallView()
}

protocol QuestsInteractorDelegate: class {
    func questsInteractorDidCallPresenter()
}

class QuestsRouter: NSObject {

}
