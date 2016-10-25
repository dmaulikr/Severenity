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
    
    private override init() {
        super.init()
        print("SocketService shared instance init did complete")
    }
    
    func downloadFBProfilePicture(with fbId: String, and completion: @escaping (_ image: Image) -> Void) {
        
        guard let serverURL = URL.init(string: "https://graph.facebook.com/\(fbId)/picture?type=normal") else {
            print("Cannot create url for FB picture")
            return
        }
        let serverRequest = URLRequest.init(url: serverURL)
        
        Alamofire.request(serverRequest).responseImage { response in
//            debugPrint(response)
//            print(response.request)
//            print(response.response)
//            debugPrint(response.result)
            
            if let image = response.result.value {
                print("FB profile picture downloaded")
                completion(image)
            }
        }
    }
}
