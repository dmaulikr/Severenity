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
        //(UIApplication.shared.delegate as! AppDelegate).viperInteractors["ChatInteractor"] = self
        WireFrame.sharedInstance.viperInteractors["ChatInteractor"] = self
    }
    
    // MARK: - Service interaction
    
    func sendMessageToServer(with JSON: Dictionary<String, String>) {
        print("Chat Interactor was called from Chat Presenter")
        
        delegate?.chatInteractorDidCallPresenter()
    }
}
