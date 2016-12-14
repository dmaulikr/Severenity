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
        WireFrame.sharedInstance.viperInteractors[kChatInteractor] = self
    }
    
    // MARK: Service interaction
    
    func sendChatMessage(with text: String) {
        Log.info(message: "ChatInteractor was called from ChatPresenter to send message", sender: self)
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            Log.error(message: "Cannot send chat message", sender: self)
            return
        }
        
        FacebookService.sharedInstance.getFBProfileInfo(with: fbUserID, and: { (info) in
            let messageJSON = ["senderName":info["name"] ?? "",
                               "senderId":fbUserID,
                               "text":text,
                               "timestamp":Date().iso8601] as [String:Any]
            SocketService.sharedInstance.sendMessageToServer(with: messageJSON)
        })
    }
    
    func recieveChatMessage(with dictionary: Dictionary<String,String>) {
        Log.info(message: "Message recieved: \(dictionary)", sender: self)
        delegate?.newMessageDidArriveWith(dictionary: dictionary)
    }
}
