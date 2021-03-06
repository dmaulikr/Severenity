//
//  QuestsPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class QuestsPresenter: NSObject {
    
    internal var interactor: QuestsInteractor?
    weak var delegate: QuestsPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = QuestsInteractor()
        interactor?.delegate = self
    }
    
    // MARK: QuestsViewController events
    
    func questsViewEvent() {
        Log.info(message: "User interacted with QuestsViewController. QuestsPresenter responds.", sender: self)
        interactor?.questsPresenterEvent()
    }

}

// MARK: QuestsInteractor delegate

extension QuestsPresenter: QuestsInteractorDelegate {
    
    func questsInteractorDidCallPresenter() {
        Log.info(message: "QuestsPresenter is called from QuestsInteractor", sender: self)
        delegate?.questsPresenterDidCallView()
    }
    
}
