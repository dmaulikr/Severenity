//
//  LocationsServerManager.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 13.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import Alamofire
import RealmSwift

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
            
                for place in JSON {
                    if let owners = place["owners"] as? NSArray {
                        for owner in owners {
                            if owner.isEqualToString("931974540209503") {
                                
                                print("Owner: \(owner) found in place \(place["name"])")
                                dataFromServer.append(place)
                                
                            }
                        }
                    }
                    
                }
                
                
                let placeInRealm = RealmPlace()
                placeInRealm.placeId = "placeId"
                placeInRealm.name = "name"
                placeInRealm.locationType = "locationType"
                placeInRealm.locationLangtitude = 0.0
                placeInRealm.locationLongtitude = 0.0
                placeInRealm.type = 0.0
                placeInRealm.createdDate = "createdDate"
                
                let placeOwner = RealmPlaceOwner()
                placeOwner.owner = "Owner 1"
                placeInRealm.owners.insert(placeOwner, atIndex: 0)
                
                // Get the default Realm
                let realm = try! Realm()
                // You only need to do this once (per thread)
                // So should i put it in AppDelegate?
                
                // Add to the Realm inside a transaction
                try! realm.write {
                    realm.add(placeInRealm)
                }
                
                
                let testQuerying = realm.objects(RealmPlace.self) // retrieves all Places from the default Realm
                print("Data from Realm: \(testQuerying)")

                
                completion(result: dataFromServer)
            }
        }
      }
    }
    
}
