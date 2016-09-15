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

class RealmPlace: Object {
    
    dynamic var placeId = ""
    dynamic var name = ""
    dynamic var locationType = ""
    dynamic var locationLatitude = 0.0
    dynamic var locationLongtitude = 0.0
    dynamic var type = 0.0
    dynamic var createdDate = ""
    var owners = List<RealmPlaceOwner>()

}

class RealmPlaceOwner: Object {
    
    dynamic var owner = ""
}
