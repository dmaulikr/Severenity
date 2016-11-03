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
        print("User changed location, MapPresenter recieved new data.")
        
        guard let fbUserID = FacebookService.sharedInstance.accessTokenUserID else {
            print("Cannot process user location update")
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
    
    func displayPlace(with data: Dictionary<String,Any>) {
        print("MapPresenter is called from MapInteractor with data: \(data)")
        delegate?.addNewPlaceToMap(with: data)
    }
    
    func displayPlayer(with picture: UIImage, and coordinates: CLLocationCoordinate2D, and info: Dictionary<String,String>) {
        delegate?.addNewPlayerToMap(with: picture, and: coordinates, and: info)
    }
    
}
