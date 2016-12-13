//
//  CurrentUser.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 13.12.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit

class User: NSObject {
    
    // MARK: Shared instance
    
    static let current = User()
    
    // MARK: User properties
    
    var createdDate: String?
    var devices: Array<Any>?
    var email: String?
    var name: String?
    var profile: Dictionary<String,Int>?
    var quests: Array<Any>?
    var team: String?
    var userId: String?
    
    // MARK: Init
    
    private override init() {
        super.init()
        Log.info(message: "UserService shared instance init did complete", sender: self)
    }

    func printDescription() {
        let description = "\(createdDate)\n" +
            "\(devices)\n" +
            "\(email)\n" +
            "\(name)\n" +
            "\(profile)\n" +
            "\(quests)\n" +
            "\(quests)\n" +
            "\(team)\n" +
            "\(userId)"
        Log.info(message: "Current user description:\n \(description)", sender: self)
    }
    
}
