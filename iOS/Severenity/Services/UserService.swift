//
//  UserService.swift
//  Severenity
//
//  Created by Yura Yasinskyy on 13.12.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import Alamofire

class UserService: NSObject {
    
    static let sharedInstance = UserService()
    
    // MARK: Init
    
    private override init() {
        super.init()
        Log.info(message: "UserService shared instance init did complete", sender: self)
    }
    
    // MARK: Authorizing user
    
    func authorizeUserWith(userId: String, completion: @escaping (_ result: Bool) -> Void) {
        Alamofire.request(kUsersURL, method: .post, parameters: ["userId":userId]).responseJSON { response in
            guard let responseResult = response.result.value as? [String: Any], let authResult = responseResult["result"] as? String else {
                    Log.error(message: "Response does not contain data", sender: self)
                    return
            }
            switch authResult {
                case "success":
                    Log.info(message: "User authorization result: success", sender: self)
                    self.setCurrentUserWith(data: responseResult)
                    completion(true)
                case "continue":
                    Log.info(message: "User authorization result: continue", sender: self)
                    if let authReason = responseResult["reason"] as? Int, authReason == 1 {
                        self.createUserWith(userId: userId)
                    }
                    completion(true)
                case "error":
                    Log.error(message: "User authorization result: error", sender: self)
                    completion(false)
            default:
                Log.error(message: "Unknown user authorization result", sender: self)
                completion(false)
            }
        }
    }
    
    private func createUserWith(userId: String) {
        Log.info(message: "Strarting new user registration", sender: self)
        FacebookService.sharedInstance.getFBProfileInfo(with: userId) { data in
            guard let fbName = data["name"], let fbEmail = data["email"] else {
                Log.error(message: "Cannot get Facebook user info by FBid", sender: self)
                return
            }
            let dataToCreateUser = ["userId":userId,
                                    "name":fbName,
                                    "email":fbEmail]
            Alamofire.request(kCreateUserURL, method: .post, parameters: dataToCreateUser).responseJSON(completionHandler: { response in
                guard let responseResult = response.result.value as? [String: Any] else {
                        Log.error(message: "Error while creating new user", sender: self)
                        return
                }
                self.setCurrentUserWith(data: responseResult)
            })
        }
        Log.info(message: "New user created succesfully", sender: self)
    }

    private func setCurrentUserWith(data: Dictionary<String,Any>) {
        guard let data = data["user"] as? Dictionary<String,Any>, let email = data["email"] as? String, let name = data["name"] as? String,
            let profile = data["profile"] as? Dictionary<String,Int>, let userId = data["userId"] as? String else {
                Log.error(message: "Cannot set current user", sender: self)
                return
        }
        if let createdDate = data["createdDate"] as? String {
            User.current.createdDate = createdDate
        }
        if let devices = data["devices"] as? Array<Any> {
            User.current.devices = devices
        }
        User.current.email = email
        User.current.name = name
        User.current.profile = profile
        if let quests = data["quests"] as? Array<Any> {
            User.current.quests = quests
        }
        if let team = data["team"] as? String {
            User.current.team = team
        }
        User.current.userId = userId
        Log.info(message: "Current user is set", sender: self)
        User.current.printDescription()
    }
    
}
