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
    dynamic var lat = 0.0
    dynamic var lng = 0.0
    dynamic var type = 0.0
    dynamic var createdDate = ""
    var owners = List<RealmPlaceOwner>()
    
    convenience init(place: Dictionary<String, Any>) {
        self.init()
        if let placeId = place["placeId"] as? String {
            self.placeId = placeId
        }
        
        if let name = place["name"] as? String {
            self.name = name
        }
        
        guard let location = place["location"] as? NSDictionary else {
            Log.error(message: "Cannot find 'location' attribute in response.", sender: self)
            return
        }

        if let lat = (location["coordinates"] as? NSArray)?[1] as? Double {
            self.lat = lat
        }
        
        if let lng = (location["coordinates"] as? NSArray)?[0] as? Double {
            self.lng = lng
        }
        
        if let type = place["type"] as? Double {
            self.type = type
        }
        
        if let createdDate = place["createdDate"] as? String {
            self.createdDate = createdDate
        }
    }
    
    override func addToDB() {
        do {
            let realm = try Realm()
            try realm.write {
                realm.add(self)
            }
        } catch {
            Log.error(message: "Realm error: \(error.localizedDescription)", sender: self)
        }
    }
}

class RealmPlaceOwner: Object {
    dynamic var id = 0
    dynamic var owner = ""
}
