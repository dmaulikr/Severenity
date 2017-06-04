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
    
    enum ProfilePictureSize: String {
        case small = "small"
        case normal = "normal"
        case album = "album"
        case large = "large"
        case square = "square"
    }
    
    static let sharedInstance = FacebookService()
    
    var accessTokenUserID: String?
    
    // MARK: Init
    
    private override init() {
        super.init()
        if let fbUserID = FBSDKAccessToken.current().userID {
            accessTokenUserID = fbUserID
        }
        Log.info(message: "FacebookService shared instance init did complete", sender: self)
    }
    
    // MARK: Methods
    
    func getFBProfilePicture(for fbUserID: String, size: ProfilePictureSize, completion: @escaping (_ image: Image) -> Void) {
        
        guard let serverURL = URL.init(string: "https://graph.facebook.com/\(fbUserID)/picture?type=\(size.rawValue)") else {
            Log.error(message: "Cannot create url for Facebook profile picture", sender: self)
            return
        }
        let serverRequest = URLRequest.init(url: serverURL)
        
        Alamofire.request(serverRequest).responseImage { response in
            
            if let image = response.result.value {
                Log.info(message: "Facebook profile picture downloaded", sender: self)
                completion(image)
            }
        }
    }
    
    func getFBProfileInfo(with fbUserID: String, and completion: @escaping (_ info: Dictionary<String,String>) -> Void) {
        FBSDKGraphRequest(graphPath: fbUserID, parameters: ["fields": "id, name, first_name, last_name, email"]).start(completionHandler: { (connection, result, error) -> Void in
            guard error == nil, let fbDetails = result as? Dictionary<String, String> else {
                Log.error(message: "Cannot get Facebook profile info: \(String(describing: error))", sender: self)
                return
            }
            completion(fbDetails)
        })
    }
    
}
