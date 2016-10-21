//
//  QuestsPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class QuestsPresenter: NSObject, QuestsInteractorDelegate {
    
    private var interactor: QuestsInteractor?
    weak var delegate: QuestsPresenterDelegate?
    
    override init() {
        super.init()
        interactor = QuestsInteractor()
        interactor?.delegate = self
    }
    
    func questsInteractorDidCallPresenter() {
        print("Quests Presenter is called from Quests Interactor")
        delegate?.questsPresenterDidCallView()
    }
    
    func questsViewEvent() {
        print("User interacted with Quests View. Quests Presenter responds.")
        interactor?.questsPresenterEvent()
    }
}
