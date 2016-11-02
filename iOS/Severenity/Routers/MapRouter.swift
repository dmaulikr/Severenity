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
    func addNewPlaceToMap(with data: Dictionary<String,Any>)
    func addNewPlayerToMap(with image: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>)
}

protocol MapInteractorDelegate: class {
    func displayPlace(with data: Dictionary<String,Any>)
    func displayPlayer(with picture: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>)
}
