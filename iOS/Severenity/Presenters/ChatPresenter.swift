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
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ChatInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ChatViewController events
    
    func userSendsMessage(with text: String) {
        print("User interacted with Chat View. Chat Presenter responds.")
        //interactor?.sendMessageToServer(with: text)
    }
    
    // MARK: ChatInteractor delegate
    
    func chatInteractorDidCallPresenter() {
        print("Chat Presenter is called from Chat Interactor")
        delegate?.chatPresenterDidCallView()
    }
}
