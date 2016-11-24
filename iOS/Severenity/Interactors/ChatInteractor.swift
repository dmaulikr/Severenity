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
    
    // MARK: Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ChatInteractor"] = self
    }
    
    // MARK: Service interaction
    
    func sendChatMessage(with text: String) {
        Log.info(message: "ChatInteractor was called from ChatPresenter to send message")
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            Log.error(message: "Cannot send chat message")
            return
        }
        
        FacebookService.sharedInstance.getFBProfileInfo(with: fbUserID, and: { (info) in
            let messageJSON = ["senderName":info["name"] ?? "",
                               "senderId":fbUserID,
                               "text":text,
                               "timestamp":self.currentTimeStamp] as [String:Any]
            SocketService.sharedInstance.sendMessageToServer(with: messageJSON)
        })
    }
    
    func recieveChatMessage(with dictionary: Dictionary<String,String>) {
        Log.info(message: "message recieved: \(dictionary)")
        delegate?.newMessageDidArrive(with: dictionary)
    }
}
