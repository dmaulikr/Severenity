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
    // Should I put it in AppDelegate?
    
    func provideData(completion: (result: NSArray) -> Void) {
        
        if checkIfRealmIsEmpty() {
            print("Realm is empty, asking server for data")
            requestDataFromServer({ 
                self.getDataFromRealm({ (data) in
                    completion(result: data)
                })
            })
        } else {
            print("Realm is not empty, loading data")
            getDataFromRealm({ (data) in
                completion(result: data)
            })
        }
        
    }
    
    func checkIfRealmIsEmpty() -> Bool {
        let realmReadQuery = self.realm.objects(RealmPlace.self)
        if realmReadQuery.isEmpty {
            return true
        }
        else {
         return false
        }
    }
    
    func requestDataFromServer(completion: () -> Void) {
        
        //var dataFromServer: [AnyObject] = []
        
        if let serverURL = NSURL.init(string: serverURLString) {
            
        let serverRequest = NSURLRequest.init(URL: serverURL)
        
        Alamofire.request(serverRequest).responseJSON { response in

            if let JSON = response.result.value as? NSArray {
                
                for place in JSON {
                    if let owners = place["owners"] as? NSArray {
                        for owner in owners
                            where owner.isEqualToString("931974540209503") { //should be Facebook token userID

                                // Adding data to Realm DB
                                let placeInRealm = RealmPlace()
                                if let placeId = place["placeId"] as? String {
                                    placeInRealm.placeId = placeId
                                }
                                if let name = place["name"] as? String {
                                    placeInRealm.name = name
                                }
                                if let locationType = place["location"]??["type"] as? String {
                                    placeInRealm.locationType = locationType
                                }
                                if let locationLongtitude = place["location"]??["coordinates"]??[0] as? Double {
                                    placeInRealm.locationLongtitude = locationLongtitude
                                }
                                if let locationLatitude = place["location"]??["coordinates"]??[1] as? Double {
                                    placeInRealm.locationLatitude = locationLatitude
                                }
                                if let type = place["type"] as? Double {
                                    placeInRealm.type = type
                                }
                                if let createdDate = place["createdDate"] as? String {
                                    placeInRealm.createdDate = createdDate
                                }
                                
                                let placeOwner = RealmPlaceOwner()
                                for object in owners {
                                
                                    if let owner = object as? String {
                                        
                                        placeOwner.owner = owner
                                        placeInRealm.owners.append(placeOwner)
                                    }
                                    
                                }
                                
                                struct Tokens { static var token: dispatch_once_t = 0 }
                                dispatch_once(&Tokens.token) {
                                    try! self.realm.write {
                                        self.realm.add(placeInRealm)
                                    }
                                }
                            }
                        
                    }
                }

                completion()
            }
        }
      }
        
    }
    
    func getDataFromRealm(completion: (data: NSArray) -> Void) {
        
        let realmReadQuery = self.realm.objects(RealmPlace.self)
        var dataFromRealm: [AnyObject] = []
        var ownersArray: [AnyObject] = []
        var isRightOwnerFound = false
        
        for place in realmReadQuery {
            let tempArray = Array(place.owners)
            for owner in tempArray {
                ownersArray.append(owner.owner)
                if owner.owner == "931974540209503" { //should be Facebook token userID
                    isRightOwnerFound = true
                }
            }
            if isRightOwnerFound {
                let dictionaryWithPlace: [String: AnyObject] = [
                    "placeId" : place.placeId,
                    "name" : place.name,
                    "type" : place.type,
                    "createdDate" : place.createdDate,
                    "owners" : ownersArray,
                    "locationType" : place.locationType,
                    "locationLatitude" : place.locationLatitude,
                    "locationLongtitude" : place.locationLongtitude
                ]
                dataFromRealm.append(dictionaryWithPlace)
            }

        }
        completion(data: dataFromRealm)

    }
    
    private func dropDataInRealm() {
       try! self.realm.write {
          self.realm.deleteAll()
       }
    }

    
}
