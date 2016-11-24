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
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["QuestsInteractor"] = self
    }
    
    // MARK: QuestsPresenter events
    
    func questsPresenterEvent() {
        Log.info(message: "QuestsInteractor was called from QuestsPresenter")
        delegate?.questsInteractorDidCallPresenter()
    }
}
