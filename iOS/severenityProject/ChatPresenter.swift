//
//  ChatPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatPresenter: NSObject, ChatInteractorDelegate {
    
    private var interactor: ChatInteractor?
    weak var delegate: ChatPresenterDelegate?
    
    override init() {
        super.init()
        interactor = ChatInteractor()
        interactor?.delegate = self
    }
    
    func chatInteractorDidCallPresenter() {
        print("Chat Presenter is called from Chat Interactor")
        delegate?.chatPresenterDidCallView()
    }
    
    func chatViewEvent() {
        print("User interacted with Chat View. Chat Presenter responds.")
        interactor?.chatPresenterEvent()
    }
}
