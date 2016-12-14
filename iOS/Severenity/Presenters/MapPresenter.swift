//
//  MapPresenter.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 20.10.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import UIKit
import CoreLocation

class MapPresenter: NSObject {
    
    internal var interactor: MapInteractor?
    weak var delegate: MapPresenterDelegate?
    
    // MARK: Init
    
    override init() {
        super.init()
        interactor = MapInteractor()
        interactor?.delegate = self
    }
    
    // MARK: MapViewController events
    
    func userLocationUpdate(_ newLocation: CLLocation) {
        Log.info(message: "User changed location, MapPresenter recieved new data.", sender: self)
        
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            Log.error(message: "Cannot process user location update", sender: self)
            return
        }
        
        let currentLocationDictionary: [String:Any] = ["lat":newLocation.coordinate.latitude,
                                                       "lng":newLocation.coordinate.longitude,
                                                       "id":fbUserID]
        interactor?.processUserLocationUpdate(with: currentLocationDictionary)
    }
    
}

// MARK: MapInteractor delegate

extension MapPresenter: MapInteractorDelegate {
    
    func displayPlaceWith(dictionary: Dictionary<String,Any>) {
        Log.info(message: "MapPresenter is called from MapInteractor with data: \(dictionary)", sender: self)
        delegate?.addPlaceToMapWith(dictionary: dictionary)
    }
    
    func displayPlayerWith(picture: UIImage, coordinates: CLLocationCoordinate2D, info: Dictionary<String,String>) {
        delegate?.addPlayerToMapWith(image: picture, coordinates: coordinates, info: info)
    }
    
}
