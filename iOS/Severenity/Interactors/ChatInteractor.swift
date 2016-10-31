//
//  ChatInteractor.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import FBSDKLoginKit

class ChatInteractor: NSObject {
    
    weak var delegate: ChatInteractorDelegate?
    
    // MARK: - Init
    
    override init() {
        super.init()
        WireFrame.sharedInstance.viperInteractors["ChatInteractor"] = self
    }
    

    
    // MARK: - Service interaction
    
    func sendChatMessage(with text: String) {
        FacebookService.sharedInstance.getFBProfileInfo(with: (FBSDKAccessToken.current().userID)!, and: { (info) in
            let messageJSON = ["senderName":info["name"] ?? "",
                               "senderId":(FBSDKAccessToken.current().userID)!,
                               "text":text,
                               "timestamp":self.currentTimeStamp] as [String:Any]
            SocketService.sharedInstance.sendMessageToServer(with: messageJSON)
            
        })
        print("ChatInteractor was called from ChatPresenter to send message")
    }
    
    func recieveChatMessage(with dictionary: Dictionary<String,String>) {
        print("message recieved: \(dictionary)")
        delegate?.newMessageDidArrive(with: dictionary)
    }
}
