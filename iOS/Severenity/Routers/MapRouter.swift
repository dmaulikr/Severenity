//
//  MapRouter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation

protocol MapPresenterDelegate: class {
    func mapPresenterDidCallView(with data: Dictionary<String,AnyObject>)
    func addNewMarkerToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D, and fbUserId: String)
}

protocol MapInteractorDelegate: class {
    func mapInteractorDidCallPresenter(with data: Dictionary<String,AnyObject>)
    func addNewUserToMap(with picture: UIImage, and coordinates: CLLocationCoordinate2D, and fbUserId: String)
}
