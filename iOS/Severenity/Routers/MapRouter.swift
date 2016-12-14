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
    func addPlaceToMapWith(dictionary: Dictionary<String,Any>)
    func addPlayerToMapWith(image: UIImage, coordinates: CLLocationCoordinate2D, info: Dictionary<String,String>)
}

protocol MapInteractorDelegate: class {
    func displayPlaceWith(dictionary: Dictionary<String,Any>)
    func displayPlayerWith(picture: UIImage, coordinates: CLLocationCoordinate2D, info: Dictionary<String,String>)
}
