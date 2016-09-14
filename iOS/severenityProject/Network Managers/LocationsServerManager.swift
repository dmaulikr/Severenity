//
//  LocationsServerManager.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 13.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import Alamofire

class LocationsServerManager: NSObject {
    
    let serverURLString = "https://severenity.herokuapp.com/places/all"
    
    func requestLocationsFromServer(completion: (result: NSArray) -> Void) {
        
        var dataFromServer: [AnyObject] = []
        
        if let serverURL = NSURL.init(string: serverURLString) {
            
        let serverRequest = NSURLRequest.init(URL: serverURL)
        
        Alamofire.request(serverRequest).responseJSON { response in
            //Server response info
//            print("Response request: \(response.request)")
//            print("Response response: \(response.response)")
//            print("Response data: \(response.data)")
//            print("Response result: \(response.result)")
//            print("Response timeline: \(response.timeline)")

            if let JSON = response.result.value as? NSArray {
                //print("JSON: \(JSON)")
            
                for location in JSON {
                    if let owners = location["owners"] as? NSArray {
                        for owner in owners {
                            if owner.isEqualToString("931974540209503") {
                                print("Owner: \(owner) found in place \(location["name"])")
                                dataFromServer.append(location)
                            }
                        }
                    }
                    
                }
                completion(result: dataFromServer)
            }
        }
      }
    }
    
}
