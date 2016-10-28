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
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ChatInteractor"] = self
    }
    
    // MARK: - Service interaction
    
    func sendMessageToServer(with JSON: Dictionary<String, String>) {
        print("ChatInteractor was called from ChatPresenter")
        
        delegate?.chatInteractorDidCallPresenter()
    }
}
