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
    
    let serverURLString = "https://severenity.herokuapp.com/places/all" // to constants

    // Get the default Realm
    // You only need to do this once (per thread)
    let realm = try! Realm()
    // Should put it in AppDelegate
    
    /**- This method returns Array with data in callback. 
     It checks whether data is already in Realm. If yes than it goes to getDataFromRealm method
     if no than it calls requestDataFromServer to get data from server.*/
    func provideData(_ completion: @escaping (_ result: NSArray) -> Void) {
        
        if checkIfRealmIsEmpty() {
            print("Realm is empty, asking server for data")
            requestDataFromServer({ 
                self.getDataFromRealm({ (data) in
                    completion(data)
                })
            })
        } else {
            print("Realm is not empty, loading data")
            getDataFromRealm({ (data) in
                completion(data)
            })
        }
        
    }
    
    func checkIfRealmIsEmpty() -> Bool {
        let realmReadQuery = self.realm.objects(RealmPlace.self)
        return realmReadQuery.isEmpty
    }
    
    
    /**- requestDataFromServer gets called when Realm is empty. It than perform request to server,
     gets JSON response, parse it and set to Realm. When completed, returns to provideData method.*/
    func requestDataFromServer(_ completion: @escaping () -> Void) {
        
        guard let serverURL = URL.init(string: serverURLString), let serverRequest = URLRequest.init(url: serverURL) else {
            print("Cannot create url request")
            return
        }
        
        Alamofire.request(serverRequest).responseJSON { response in

            guard let places = response.result.value as? NSArray else {
                print("Response does not contain places.")
                return
            }
            
            for place in places {
                guard let owners = place["owners"] as? NSArray else {
                    print("Can't find owners in place item.")
                    return
                }
                
                for owner in owners where owner.isEqualToString("931974540209503") { //should be Facebook token userID
                    // Adding data to Realm DB
                    let placeInRealm = RealmPlace(place)
                    
                    for object in owners {
                        if let owner = object as? String {
                            let placeOwner = RealmPlaceOwner()
                            placeOwner.owner = owner
                            placeInRealm.owners.append(placeOwner)
                        }
                    }
                    
                    databaseManager.addToDB(placeInRealm)
                    placeInRealm.addToDB()
                }
            }

            completion()
        }
    }
    
    /**- This method makes query to Realm data model and gets all needed data
     that is than returned in Array. */
    func getDataFromRealm(_ completion: (_ data: NSArray) -> Void) {
        
        let realmReadQuery = self.realm.objects(RealmPlace.self)
        var dataFromRealm: [AnyObject] = []
        var ownersArray: [AnyObject] = []
        var isRightOwnerFound = false
        
        for place in realmReadQuery {
            let tempArray = Array(place.owners)
            for owner in tempArray {
                ownersArray.append(owner.owner)
                isRightOwnerFound = owner.owner == "931974540209503" //should be Facebook token userID
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
            ownersArray.clean()
        }
        completion(dataFromRealm as NSArray)

    }
    
    // Try to drop data by type 'Location' etc., not all data
    fileprivate func dropDataInRealm() {
       try! self.realm.write {
          self.realm.deleteAll()
       }
    }

    
}
