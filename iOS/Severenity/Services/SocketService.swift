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
    var socket: SocketIOClient = SocketIOClient(socketURL: URL(string: kSocketServerURL)!)
    
    // MARK: - Init
    
    private override init() {
        super.init()
        print("SocketService shared instance init did complete")
    }
    
    // MARK: - Managing connection
    
    func establishConnection() {
        socket.connect()
        addSocketHandlers()
        print("socket connection established")
    }
    
    func closeConnection() {
        socket.disconnect()
        print("socket connection closed")
    }
    
    // MARK: - Handlers
    
    func addSocketHandlers() {
        socket.on("location") { (data, ack) in
            print("socket 'location' recieved with data: \(data)")
            let selector = #selector(MapInteractor.processNewPlayerLocation(with:))
            let _ = WireFrame.sharedInstance.viperInteractors["MapInteractor"]?.perform(selector, with: data)
        }
        socket.on("chat message") { (data, ack) in
            print("socket 'chat message' recieved with data: \(data)")
            let selector = #selector(ChatInteractor.recieveChatMessage(with:))
            let _ = WireFrame.sharedInstance.viperInteractors["ChatInteractor"]?.perform(selector, with: data.first)
        }
    }
    
    // MARK: - Methods
    
    func sendLocationToServer(with placeJSON: Dictionary<String,Any>) {
        socket.emit("location", placeJSON)
        print("socket message: \(placeJSON) was sent to server")
        let selector = #selector(MapInteractor.processNewPlayerLocation(with:))
        let _ = WireFrame.sharedInstance.viperInteractors["MapInteractor"]?.perform(selector, with: placeJSON)
    }
    
    func sendMessageToServer(with messageJSON: Dictionary<String,Any>) {
        socket.emit("chat message", messageJSON)
        print("socket message: \(messageJSON) was sent to server")
    }
}
