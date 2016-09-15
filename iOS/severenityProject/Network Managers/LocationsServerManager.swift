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

    // Get the default Realm
    // You only need to do this once (per thread)
    let realm = try! Realm()
    // So should i put it in AppDelegate?
    
    
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
                                
                                // Adding data to array (to show on table)
                                print("Owner: \(owner) found in place \(place["name"])")
                                dataFromServer.append(place)
                                
                                
                                // Adding data to Realm DB
                                let placeInRealm = RealmPlace()
                                placeInRealm.placeId = place["placeId"] as! String
                                placeInRealm.name = place["name"] as! String
                                placeInRealm.locationType = place["location"]!!["type"] as! String
                                placeInRealm.locationLangtitude = place["location"]!!["coordinates"]!![0] as! Double
                                placeInRealm.locationLongtitude = place["location"]!!["coordinates"]!![1] as! Double
                                placeInRealm.type = place["type"] as! Double // but it has to be enum, not double?
                                placeInRealm.createdDate = place["createdDate"] as! String
                                
                                let placeOwner = RealmPlaceOwner()
                                for object in owners {
                                
                                placeOwner.owner = object as! String
                                placeInRealm.owners.append(placeOwner)
                                    
                                }
                                
//                                try! self.realm.write {
//                                    self.realm.deleteAll()
//                                }
                                
                                try! self.realm.write {
                                    self.realm.add(placeInRealm)
                                }
                                
                                let testQuerying = self.realm.objects(RealmPlace.self) // retrieves all Places from the default Realm
                                print("Data from Realm: \(testQuerying)")
                                
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
