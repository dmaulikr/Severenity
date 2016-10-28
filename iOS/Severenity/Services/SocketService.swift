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
    var socket: SocketIOClient = SocketIOClient(socketURL: URL(string: "https://severenity.herokuapp.com")!)
    
    // MARK: - Init
    
    private override init() {
        super.init()
        print("SocketService shared instance init did complete")
    }
    
    // MARK: - Methods
    
    func establishConnection() {
        socket.connect()
        addSocketHandlers()
        print("socket connection established")
    }
    
    func closeConnection() {
        socket.disconnect()
        print("socket connection closed")
    }
    
    func sendLocationToServer(with placeJSON: Dictionary<String,Any>) {
        socket.emit("location", placeJSON)
        print("socket message: \(placeJSON) was sent to server")
        
        let selector = #selector(MapInteractor.processNewUser(with:))
        let _ = WireFrame.sharedInstance.viperInteractors["MapInteractor"]?.perform(selector, with: placeJSON)
    }
    
    func addSocketHandlers() {
        socket.on("location") { (data, ack) in
            print("socket 'location' recieved with data: \(data)")
            let selector = #selector(MapInteractor.processNewUser(with:))
            let _ = WireFrame.sharedInstance.viperInteractors["MapInteractor"]?.perform(selector, with: data)
        }
        socket.on("chat message") { (data, ack) in
            print("socket 'chat message' recieved with data: \(data)")
        }
    }
}
