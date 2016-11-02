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
import FBSDKLoginKit

class PlacesService: NSObject {

    private var realm: Realm?
    
    // MARK: Init

    override init() {
        do {
            realm = try Realm()
        } catch let error as NSError {
            print("Realm error: \(error.localizedDescription)")
        }
    }
    
    // MARK: Methods
    
    /**- This method returns Array with data in callback. 
     It checks whether data is already in Realm. If yes than it goes to getDataFromRealm method
     if no than it calls requestDataFromServer to get data from server.*/
    func provideData(_ completion: @escaping (_ result: NSArray) -> Void) {
        if checkIfRealmIsEmpty() {
            print("Realm is empty, asking server for data")
            requestDataFromServer {
                self.getDataFromRealm { data in
                    completion(data)
                }
            }
        } else {
            print("Realm is not empty, loading data")
            getDataFromRealm { data in
                completion(data)
            }
        }
    }
    
    private func checkIfRealmIsEmpty() -> Bool {
        let realmReadQuery = realm?.objects(RealmPlace.self)
        return realmReadQuery?.isEmpty ?? false
    }
    
    
    /**- requestDataFromServer gets called when Realm is empty. It than perform request to server,
     gets JSON response, parse it and set to Realm. When completed, returns to provideData method.*/
    private func requestDataFromServer(_ completion: @escaping () -> Void) {
        
        guard let serverURL = URL.init(string: kPlacesServerURL) else {
            print("Cannot create server url")
            return
        }
        
        let serverRequest = URLRequest.init(url: serverURL)
        
        Alamofire.request(serverRequest).responseJSON { response in

            guard let places = response.result.value as? [[String: Any]] else {
                print("Response does not contain places.")
                return
            }
            
            for place in places {
                guard let owners = place["owners"] as? [String] else {
                    print("Can't find owners in place item.")
                    return
                }
                
                for owner in owners where owner == FBSDKAccessToken.current().userID {
                    
                    // Adding data to Realm DB
                    let placeInRealm = RealmPlace(place: place)
                    for object in owners {
                        let placeOwner = RealmPlaceOwner()
                        placeOwner.owner = object
                        placeInRealm.owners.append(placeOwner)
                        
                    }
                    placeInRealm.addToDB()
                }
            }
            completion()
        }
    }
    
    /**- This method makes query to Realm data model and gets all needed data
     that is than returned in Array. */
    private func getDataFromRealm(_ completion: (_ data: NSArray) -> Void) {
        
        let realmReadQuery = realm?.objects(RealmPlace.self)
        var dataFromRealm: [AnyObject] = []
        var ownersArray: [AnyObject] = []
        var isRightOwnerFound = false
        
        guard let query = realmReadQuery else {
            print("Error creating Realm read query")
            return
        }
        for place in query {
            let tempArray = Array(place.owners)
            for owner in tempArray {
                ownersArray.append(owner.owner as AnyObject)
                isRightOwnerFound = owner.owner == FBSDKAccessToken.current().userID
            }
            if isRightOwnerFound {
                let dictionaryWithPlace: [String: AnyObject] = [
                    "placeId" : place.placeId as AnyObject,
                    "name" : place.name as AnyObject,
                    "type" : place.type as AnyObject,
                    "createdDate" : place.createdDate as AnyObject,
                    "owners" : ownersArray as AnyObject,
                    "lat" : place.lat as AnyObject,
                    "lng" : place.lng as AnyObject
                ]
                dataFromRealm.append(dictionaryWithPlace as AnyObject)
            }
            ownersArray.removeAll()
        }
        completion(dataFromRealm as NSArray)
    }
    
    // Try to drop data by type 'Location' etc., not all data
    private func dropDataInRealm() {
        do {
            try realm?.write {
                realm?.deleteAll()
            }
        } catch let error as NSError {
            print("Realm error: \(error.localizedDescription)")
        }
    }
}
