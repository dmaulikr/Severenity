//
//  ChatInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class ChatInteractor: NSObject {
    
    weak var delegate: ChatInteractorDelegate?
    
    override init() {
        super.init()
        (UIApplication.shared.delegate as! AppDelegate).viperInteractors["ChatInteractor"] = self
    }
    
    func chatPresenterEvent() {
        print("Chat Interactor was called from Chat Presenter")
        delegate?.chatInteractorDidCallPresenter()
    }
}
