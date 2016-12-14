//
//  ChatPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatPresenter: NSObject {
    
    internal var interactor: ChatInteractor?
    weak var delegate: ChatPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = ChatInteractor()
        interactor?.delegate = self
    }
    
    // MARK: ChatViewController events
    
    func userWantsToSendMessage(with text: String) {
        Log.info(message: "User wants to send message from with ChatViewController. ChatPresenter responds.", sender: self)
        interactor?.sendChatMessage(with: text)
    }

}

// MARK: ChatInteractor delegate

extension ChatPresenter: ChatInteractorDelegate {
    
    func newMessageDidArriveWith(dictionary: Dictionary<String,String>) {
        Log.info(message: "ChatPresenter is called from ChatInteractor with message: \(dictionary)", sender: self)
        delegate?.displayNewMessageWith(dictionary: dictionary)
    }
    
}
