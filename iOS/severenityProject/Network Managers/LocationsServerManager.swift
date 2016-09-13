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
    
    let serverURLString: String = "https://severenity.herokuapp.com/places/all"
    
    func requestLocationsFromServer(completion: (result: NSArray) -> Void) {
        
        let dataFromServer: NSMutableArray = []
        
        let serverURL = NSURL.init(string: serverURLString)
        let serverRequest = NSURLRequest.init(URL: serverURL!)
        
        Alamofire.request(serverRequest).responseJSON { response in
//            print("Response request: \(response.request)")
//            print("Response response: \(response.response)")
//            print("Response data: \(response.data)")
//            print("Response result: \(response.result)")
//            print("Response timeline: \(response.timeline)")
            
            if let JSON = response.result.value {
                //print("JSON: \(JSON)")
                for location in JSON as! NSArray { // if let
                    //print("Location: \(location)")
                    let owners = location["owners"] as! NSArray // if let
                    for owner in owners {
                        if owner.isEqualToString("931974540209503") {
                            print("Owner: \(owner) found!")
                            dataFromServer.addObject(location)
                        }
                    }
                    //dataFromServer.addObject(location)

                }
                completion(result: dataFromServer.copy() as! NSArray) // if let
                
            }
 
        }

    }
    
}
