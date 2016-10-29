//
//  FacebookService.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 25.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import FBSDKLoginKit

class FacebookService: NSObject {
    
    static let sharedInstance = FacebookService()
    
    // MARK: - Init
    
    private override init() {
        super.init()
        print("SocketService shared instance init did complete")
    }
    
    // MARK: - Methods
    
    func getFBProfilePicture(with fbUserId: String, and completion: @escaping (_ image: Image) -> Void) {
        
        guard let serverURL = URL.init(string: "https://graph.facebook.com/\(fbUserId)/picture?type=normal") else {
            print("Cannot create url for Facebook profile picture")
            return
        }
        let serverRequest = URLRequest.init(url: serverURL)
        
        Alamofire.request(serverRequest).responseImage { response in
            
            if let image = response.result.value {
                print("Facebook profile picture downloaded")
                completion(image)
            }
        }
    }
    
    func getFBProfileInfo(with fbUserID: String, and completion: @escaping (_ info: Dictionary<String,String>) -> Void){
        FBSDKGraphRequest(graphPath: fbUserID, parameters: ["fields": "id, name, first_name, last_name, relationship_status"]).start(completionHandler: { (connection, result, error) -> Void in
            if (error == nil){
                if let fbDetails = result as? Dictionary<String, String> {
                    completion(fbDetails)
                }
            }
        })
    }
    
    
    
}
