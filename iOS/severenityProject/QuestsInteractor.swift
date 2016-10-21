//
//  QuestsInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class QuestsInteractor: NSObject {

    weak var delegate: QuestsInteractorDelegate?
    
    override init() {
        super.init()
        WireFrame.sharedWireFrame.viperInteractors["QuestsInteractor"] = self
    }
    
    func questsPresenterEvent() {
        print("Quests Interactor was called from Quests Presenter")
        delegate?.questsInteractorDidCallPresenter()
    }
}
