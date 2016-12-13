//
//  CurrentLocationUpdateService.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 24.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import SocketIO

class SocketService: NSObject {
    
    static let sharedInstance = SocketService()
    var socket: SocketIOClient = SocketIOClient(socketURL: URL(string: kServerURL)!)
    
    // MARK: Init
    
    private override init() {
        super.init()
        Log.info(message: "SocketService shared instance init did complete", sender: self)
    }
    
    // MARK: Managing connection
    
    func establishConnection() {
        socket.connect()
        addSocketHandlers()
        Log.info(message: "socket connection established", sender: self)
    }
    
    func closeConnection() {
        socket.disconnect()
        Log.info(message: "socket connection closed", sender: self)
    }
    
    // MARK: Handlers
    
    func addSocketHandlers() {
        socket.on("location") { (data, ack) in
            Log.info(message: "socket 'location' recieved with data: \(data)", sender: self)
            let selector = #selector(MapInteractor.processNewPlayerLocation(with:))
            let _ = WireFrame.sharedInstance.viperInteractors[kMapInteractor]?.perform(selector, with: data.first)
        }
        socket.on("chat message") { (data, ack) in
            Log.info(message: "socket 'chat message' recieved with data: \(data)", sender: self)
            let selector = #selector(ChatInteractor.recieveChatMessage(with:))
            let _ = WireFrame.sharedInstance.viperInteractors[kChatInteractor]?.perform(selector, with: data.first)
        }
    }
    
    // MARK: Methods
    
    func sendLocationToServer(with placeJSON: Dictionary<String,Any>) {
        socket.emit("location", placeJSON)
        Log.info(message: "socket message: \(placeJSON) was sent to server", sender: self)
        let selector = #selector(MapInteractor.processNewPlayerLocation(with:))
        let _ = WireFrame.sharedInstance.viperInteractors[kMapInteractor]?.perform(selector, with: placeJSON)
    }
    
    func sendMessageToServer(with messageJSON: Dictionary<String,Any>) {
        socket.emit("chat message", messageJSON)
        Log.info(message: "socket message: \(messageJSON) was sent to server", sender: self)
    }
}
