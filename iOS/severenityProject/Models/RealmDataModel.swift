//
//  RealmDataModel.swift
//  severenityProject
//
//  Created by Yura Yasinskyy on 14.09.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import Foundation
import RealmSwift

class BusinessObject: Object {
    func addToDB() {
        // template method
    }
}

class RealmPlace: BusinessObject {
    
    dynamic var placeId = ""
    dynamic var name = ""
    dynamic var locationType = "" // remove
    dynamic var locationLatitude = 0.0 // rename to latitude or lat // cannot do it now because project is not building
    dynamic var locationLongtitude = 0.0 // rename to longitude or lng
    dynamic var type = 0.0
    dynamic var createdDate = ""
    var owners = List<RealmPlaceOwner>()
    
    init() {
        
    }
    
    convenience init(place: NSArray) {
        super.init()
        if let placeId = place["placeId"] as? String {
            self.placeId = placeId
        }
        
        if let name = place["name"] as? String {
            self.name = name
        }
        
        if let locationType = place["location"]??["type"] as? String {
            self.locationType = locationType
        }
        
        if let locationLongtitude = place["location"]??["coordinates"]??[0] as? Double {
            self.locationLongtitude = locationLongtitude
        }
        
        if let locationLatitude = place["location"]??["coordinates"]??[1] as? Double {
            self.locationLatitude = locationLatitude
        }
        
        if let type = place["type"] as? Double {
            self.type = type
        }
        
        if let createdDate = place["createdDate"] as? String {
            self.createdDate = createdDate
        }
    }
    
    override func addToDB() {
        struct Tokens { static var token: dispatch_once_t = 0 }
        dispatch_once(&Tokens.token) {
            try! self.realm.write {
                self.realm.add(self)
            }
        }
    }

}

class RealmPlaceOwner: Object {
    dynamic var id = 0
    dynamic var owner = ""
}
